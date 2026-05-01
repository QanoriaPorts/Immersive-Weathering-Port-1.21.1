package com.ordana.immersive_weathering.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public class IceSickleItem extends SwordItem {

    // 1.21.1 SwordItem(Tier, Properties); damage/speed are configured via
    // DataComponents (Item.Properties.attributes(SwordItem.createAttributes(...))),
    // not constructor args. The legacy (int, float) values are no longer used here;
    // wire them through Properties on the registration site if you need custom stats.
    public IceSickleItem(Tier tier, int unusedDamage, float unusedSpeed, Properties properties) {
        super(tier, properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + 80));
        return super.finishUsingItem(stack, level, entity);
    }

    // The 'isEdible' / 'getFoodProperties' overrides that used to live here
    // were removed in 1.20.5+; food is now declared via the FOOD DataComponent
    // when the item is registered. Configure via ModFoods + Item.Properties.food(...).
}
