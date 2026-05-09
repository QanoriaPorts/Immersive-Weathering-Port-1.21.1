package com.ordana.immersive_weathering.items.materials;

import com.ordana.immersive_weathering.ImmersiveWeathering;
import com.ordana.immersive_weathering.reg.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

/**
 * In 1.21.0+ {@code ArmorMaterial} is a record, not an interface, so this is
 * now an immutable instance instead of a class implementing the interface.
 * Callers reference {@link #INSTANCE} (now {@code Holder<ArmorMaterial>}).
 */
public final class FlowerCrownMaterial {
    private FlowerCrownMaterial() {}

    private static final EnumMap<ArmorItem.Type, Integer> DEFENSE = new EnumMap<>(ArmorItem.Type.class);
    static {
        for (ArmorItem.Type t : ArmorItem.Type.values()) DEFENSE.put(t, 0);
    }

    private static final ArmorMaterial RAW = new ArmorMaterial(
            DEFENSE,
            64,                                      // enchantmentValue
            SoundEvents.ARMOR_EQUIP_LEATHER,         // Holder<SoundEvent>
            () -> Ingredient.of(ModItems.AZALEA_FLOWERS.get()),
            List.of(new ArmorMaterial.Layer(
                    ResourceLocation.fromNamespaceAndPath(ImmersiveWeathering.MOD_ID, "flower"))),
            0.0f,                                    // toughness
            0.0f                                     // knockbackResistance
    );

    public static final Holder<ArmorMaterial> INSTANCE = Holder.direct(RAW);
}
