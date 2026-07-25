package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.inventory.NautilusInventoryMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CTBackport.MODID);

	public static final RegistryObject<MenuType<NautilusInventoryMenu>> NAUTILUS_INVENTORY = MENUS.register("nautilus_inventory",
			() -> IForgeMenuType.create(NautilusInventoryMenu::fromNetwork));
}
