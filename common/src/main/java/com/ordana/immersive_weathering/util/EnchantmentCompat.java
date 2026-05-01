package com.ordana.immersive_weathering.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Bridge for the 1.20.1 -> 1.21.1 enchantment API delta.
 *
 * In 1.20.5+ enchantments became data-driven, so {@code Enchantments.X}
 * is now a {@link ResourceKey}, not an {@code Enchantment} instance, and
 * {@link EnchantmentHelper#getEnchantmentLevel(Holder, LivingEntity)} takes
 * a {@link Holder}. {@code EnchantmentHelper.hasFrostWalker} was also
 * removed.
 *
 * Helpers here resolve the holder via the entity's registry access. Avoid
 * calling them on disconnected entities or before world load.
 */
public final class EnchantmentCompat {
    private EnchantmentCompat() {}

    public static int getEnchantmentLevel(LivingEntity entity, ResourceKey<Enchantment> key) {
        Holder<Enchantment> holder = entity.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(key);
        return EnchantmentHelper.getEnchantmentLevel(holder, entity);
    }

    public static boolean hasFrostWalker(LivingEntity entity) {
        return getEnchantmentLevel(entity, Enchantments.FROST_WALKER) > 0;
    }
}
