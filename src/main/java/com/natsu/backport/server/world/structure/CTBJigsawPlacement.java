package com.natsu.backport.server.world.structure;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

/**
 * Vanilla 1.18.2 JigsawPlacement with the modern additions the trial chambers rely on :
 * a configurable radius (116 instead of the hardcoded 80), pool aliases resolved per
 * structure instance, and jigsaw selection/placement priorities.
 */
public class CTBJigsawPlacement {

	static final Logger LOGGER = LogUtils.getLogger();

	private static final Comparator<StructureTemplate.StructureBlockInfo> HIGHEST_SELECTION_PRIORITY_FIRST =
			Comparator.comparingInt((StructureTemplate.StructureBlockInfo info) -> info.nbt.getInt("selection_priority")).reversed();

	public static Optional<PieceGenerator<JigsawConfiguration>> addPieces(
			PieceGeneratorSupplier.Context<JigsawConfiguration> context,
			BlockPos startPos,
			int maxDistanceFromCenter,
			Map<ResourceLocation, ResourceLocation> poolAliases) {
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		random.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
		RegistryAccess registryAccess = context.registryAccess();
		JigsawConfiguration config = context.config();
		ChunkGenerator chunkGenerator = context.chunkGenerator();
		StructureManager structureManager = context.structureManager();
		LevelHeightAccessor heightAccessor = context.heightAccessor();
		Predicate<Holder<Biome>> validBiome = context.validBiome();
		StructureFeature.bootstrap();
		Registry<StructureTemplatePool> pools = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
		Rotation rotation = Rotation.getRandom(random);
		StructureTemplatePool startPool = config.startPool().value();
		StructurePoolElement startElement = startPool.getRandomTemplate(random);
		if (startElement == EmptyPoolElement.INSTANCE) {
			return Optional.empty();
		}

		PoolElementStructurePiece startPiece = new PoolElementStructurePiece(structureManager, startElement, startPos,
				startElement.getGroundLevelDelta(), rotation, startElement.getBoundingBox(structureManager, startPos, rotation));
		BoundingBox startBox = startPiece.getBoundingBox();
		int centerX = (startBox.maxX() + startBox.minX()) / 2;
		int centerZ = (startBox.maxZ() + startBox.minZ()) / 2;
		int centerY = startPos.getY();

		if (!validBiome.test(chunkGenerator.getNoiseBiome(QuartPos.fromBlock(centerX), QuartPos.fromBlock(centerY), QuartPos.fromBlock(centerZ)))) {
			return Optional.empty();
		}

		int groundY = startBox.minY() + startPiece.getGroundLevelDelta();
		startPiece.move(0, centerY - groundY, 0);
		return Optional.of((builder, pieceContext) -> {
			List<PoolElementStructurePiece> pieces = Lists.newArrayList();
			pieces.add(startPiece);
			if (config.maxDepth() > 0) {
				AABB limit = new AABB(centerX - maxDistanceFromCenter, centerY - maxDistanceFromCenter, centerZ - maxDistanceFromCenter,
						centerX + maxDistanceFromCenter + 1, centerY + maxDistanceFromCenter + 1, centerZ + maxDistanceFromCenter + 1);
				Placer placer = new Placer(pools, config.maxDepth(), chunkGenerator, structureManager, pieces, random, poolAliases);
				placer.enqueue(new PieceState(startPiece,
						new MutableObject<>(Shapes.join(Shapes.create(limit), Shapes.create(AABB.of(startBox)), BooleanOp.ONLY_FIRST)), 0), 0);

				while (!placer.placing.isEmpty()) {
					PieceState state = placer.placing.poll().state;
					placer.tryPlacingChildren(state.piece, state.free, state.depth, heightAccessor);
				}

				pieces.forEach(builder::addPiece);
			}
		});
	}

	static final class PieceState {
		final PoolElementStructurePiece piece;
		final MutableObject<VoxelShape> free;
		final int depth;

		PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> free, int depth) {
			this.piece = piece;
			this.free = free;
			this.depth = depth;
		}
	}

	/** Queue entry, highest placement priority first and FIFO between equals. */
	static final class QueuedState {
		final PieceState state;
		final int priority;
		final long sequence;

		QueuedState(PieceState state, int priority, long sequence) {
			this.state = state;
			this.priority = priority;
			this.sequence = sequence;
		}
	}

	static final class Placer {
		private final Registry<StructureTemplatePool> pools;
		private final int maxDepth;
		private final ChunkGenerator chunkGenerator;
		private final StructureManager structureManager;
		private final List<? super PoolElementStructurePiece> pieces;
		private final Random random;
		private final Map<ResourceLocation, ResourceLocation> poolAliases;
		final PriorityQueue<QueuedState> placing = new PriorityQueue<>(
				Comparator.<QueuedState>comparingInt(q -> -q.priority).thenComparingLong(q -> q.sequence));
		private long sequence;

		Placer(Registry<StructureTemplatePool> pools, int maxDepth, ChunkGenerator chunkGenerator, StructureManager structureManager,
				List<? super PoolElementStructurePiece> pieces, Random random, Map<ResourceLocation, ResourceLocation> poolAliases) {
			this.pools = pools;
			this.maxDepth = maxDepth;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.pieces = pieces;
			this.random = random;
			this.poolAliases = poolAliases;
		}

		void enqueue(PieceState state, int priority) {
			this.placing.add(new QueuedState(state, priority, this.sequence++));
		}

		private ResourceLocation resolvePool(ResourceLocation pool) {
			return this.poolAliases.getOrDefault(pool, pool);
		}

		void tryPlacingChildren(PoolElementStructurePiece sourcePiece, MutableObject<VoxelShape> contextFree, int depth, LevelHeightAccessor heightAccessor) {
			StructurePoolElement sourceElement = sourcePiece.getElement();
			BlockPos sourcePos = sourcePiece.getPosition();
			Rotation sourceRotation = sourcePiece.getRotation();
			StructureTemplatePool.Projection sourceProjection = sourceElement.getProjection();
			boolean sourceRigid = sourceProjection == StructureTemplatePool.Projection.RIGID;
			MutableObject<VoxelShape> sourceFree = new MutableObject<>();
			BoundingBox sourceBox = sourcePiece.getBoundingBox();
			int sourceMinY = sourceBox.minY();

			List<StructureTemplate.StructureBlockInfo> sourceJigsaws =
					Lists.newArrayList(sourceElement.getShuffledJigsawBlocks(this.structureManager, sourcePos, sourceRotation, this.random));
			sourceJigsaws.sort(HIGHEST_SELECTION_PRIORITY_FIRST);

			label139:
			for (StructureTemplate.StructureBlockInfo sourceJigsaw : sourceJigsaws) {
				Direction direction = JigsawBlock.getFrontFacing(sourceJigsaw.state);
				BlockPos jigsawPos = sourceJigsaw.pos;
				BlockPos attachPos = jigsawPos.relative(direction);
				int jigsawYOffset = jigsawPos.getY() - sourceMinY;
				int surfaceY = -1;
				ResourceLocation poolId = resolvePool(new ResourceLocation(sourceJigsaw.nbt.getString("pool")));
				Optional<StructureTemplatePool> pool = this.pools.getOptional(poolId);
				if (pool.isEmpty() || pool.get().size() == 0 && !Objects.equals(poolId, Pools.EMPTY.location())) {
					LOGGER.warn("Empty or non-existent pool: {}", poolId);
					continue;
				}

				ResourceLocation fallbackId = pool.get().getFallback();
				Optional<StructureTemplatePool> fallback = this.pools.getOptional(fallbackId);
				if (fallback.isEmpty() || fallback.get().size() == 0 && !Objects.equals(fallbackId, Pools.EMPTY.location())) {
					LOGGER.warn("Empty or non-existent fallback pool: {}", fallbackId);
					continue;
				}

				boolean attachInside = sourceBox.isInside(attachPos);
				MutableObject<VoxelShape> free;
				if (attachInside) {
					free = sourceFree;
					if (sourceFree.getValue() == null) {
						sourceFree.setValue(Shapes.create(AABB.of(sourceBox)));
					}
				} else {
					free = contextFree;
				}

				List<StructurePoolElement> candidates = Lists.newArrayList();
				if (depth != this.maxDepth) {
					candidates.addAll(pool.get().getShuffledTemplates(this.random));
				}
				candidates.addAll(fallback.get().getShuffledTemplates(this.random));

				int placementPriority = sourceJigsaw.nbt.getInt("placement_priority");

				for (StructurePoolElement candidate : candidates) {
					if (candidate == EmptyPoolElement.INSTANCE) {
						break;
					}

					for (Rotation candidateRotation : Rotation.getShuffled(this.random)) {
						List<StructureTemplate.StructureBlockInfo> candidateJigsaws =
								candidate.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, candidateRotation, this.random);
						for (StructureTemplate.StructureBlockInfo candidateJigsaw : candidateJigsaws) {
							if (!JigsawBlock.canAttach(sourceJigsaw, candidateJigsaw)) {
								continue;
							}

							BlockPos candidateJigsawPos = candidateJigsaw.pos;
							BlockPos candidateOrigin = attachPos.subtract(candidateJigsawPos);
							BoundingBox candidateBox = candidate.getBoundingBox(this.structureManager, candidateOrigin, candidateRotation);
							int candidateMinY = candidateBox.minY();
							StructureTemplatePool.Projection candidateProjection = candidate.getProjection();
							boolean candidateRigid = candidateProjection == StructureTemplatePool.Projection.RIGID;
							int candidateJigsawY = candidateJigsawPos.getY();
							int connectionOffset = jigsawYOffset - candidateJigsawY + JigsawBlock.getFrontFacing(sourceJigsaw.state).getStepY();
							int targetMinY;
							if (sourceRigid && candidateRigid) {
								targetMinY = sourceMinY + connectionOffset;
							} else {
								if (surfaceY == -1) {
									surfaceY = this.chunkGenerator.getFirstFreeHeight(jigsawPos.getX(), jigsawPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, heightAccessor);
								}
								targetMinY = surfaceY - candidateJigsawY;
							}

							int yShift = targetMinY - candidateMinY;
							BoundingBox shiftedBox = candidateBox.moved(0, yShift, 0);
							BlockPos shiftedOrigin = candidateOrigin.offset(0, yShift, 0);

							if (Shapes.joinIsNotEmpty(free.getValue(), Shapes.create(AABB.of(shiftedBox).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
								continue;
							}
							free.setValue(Shapes.joinUnoptimized(free.getValue(), Shapes.create(AABB.of(shiftedBox)), BooleanOp.ONLY_FIRST));

							int sourceGroundDelta = sourcePiece.getGroundLevelDelta();
							int candidateGroundDelta = candidateRigid ? sourceGroundDelta - connectionOffset : candidate.getGroundLevelDelta();
							PoolElementStructurePiece childPiece = new PoolElementStructurePiece(this.structureManager, candidate,
									shiftedOrigin, candidateGroundDelta, candidateRotation, shiftedBox);
							int junctionY;
							if (sourceRigid) {
								junctionY = sourceMinY + jigsawYOffset;
							} else if (candidateRigid) {
								junctionY = targetMinY + candidateJigsawY;
							} else {
								if (surfaceY == -1) {
									surfaceY = this.chunkGenerator.getFirstFreeHeight(jigsawPos.getX(), jigsawPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, heightAccessor);
								}
								junctionY = surfaceY + connectionOffset / 2;
							}

							sourcePiece.addJunction(new JigsawJunction(attachPos.getX(), junctionY - jigsawYOffset + sourceGroundDelta,
									attachPos.getZ(), connectionOffset, candidateProjection));
							childPiece.addJunction(new JigsawJunction(jigsawPos.getX(), junctionY - candidateJigsawY + candidateGroundDelta,
									jigsawPos.getZ(), -connectionOffset, sourceProjection));
							this.pieces.add(childPiece);
							if (depth + 1 <= this.maxDepth) {
								this.enqueue(new PieceState(childPiece, free, depth + 1), placementPriority);
							}
							continue label139;
						}
					}
				}
			}
		}
	}
}
