package com.ordana.immersive_weathering.data.rute_tests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ordana.immersive_weathering.data.block_growths.Operator;
import com.ordana.immersive_weathering.reg.ModRuleTests;
import com.ordana.immersive_weathering.util.StrOpt;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BlockPropertyTest extends RuleTest {

    // Now MapCodec to satisfy 1.21+ RuleTestType.codec() return type.
    public static final MapCodec<BlockPropertyTest> CODEC = PropPredicate.CODEC.listOf().fieldOf("properties")
            .xmap(BlockPropertyTest::new, (t) -> t.propPredicates);

    private final List<PropPredicate> propPredicates;

    private BlockPropertyTest(List<PropPredicate> propPredicates) {
        this.propPredicates = propPredicates;
    }

    @Override
    public boolean test(BlockState state, RandomSource random) {
        for(var p : propPredicates){
            if(!p.test(state))return false;
        }
        return true;
    }

    @Override
    protected RuleTestType<BlockPropertyTest> getType() {
        return ModRuleTests.BLOCK_PROPERTY_TEST.get();
    }


    private static final class PropPredicate implements Predicate<BlockState> { //

        // 1.21: partialDispatch's inner lookup now returns MapCodec<? extends E>
        // (was Codec<? extends E> in 1.20.x). Rather than chain two
        // partialDispatches and bridge Codec->MapCodec at each level, we
        // flatten to a single MapCodec that reads from_block / property /
        // value / operator and validates property+value at apply time.
        public static Codec<PropPredicate> CODEC = RecordCodecBuilder.create(i -> i.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("from_block").forGetter(PropPredicate::getFromBlock),
                Codec.STRING.fieldOf("property").forGetter(p -> p.getProperty().getName()),
                StrOpt.of(Codec.STRING, "value")
                        .forGetter(p -> p.getTargetValue().map(Object::toString)),
                StrOpt.of(Operator.CODEC, "operator", Operator.EQUAL).forGetter(PropPredicate::getOperator)
        ).apply(i, (block, propName, valStr, op) -> {
            BlockState state = block.defaultBlockState();
            Property<?> property = null;
            for (var p : state.getProperties()) {
                if (p.getName().equals(propName)) {
                    property = p;
                    break;
                }
            }
            if (property == null) {
                throw new IllegalStateException("Unknown property " + propName + " on block " + block);
            }
            final Property<?> resolvedProperty = property;
            Optional<Comparable<?>> value = valStr.flatMap(s ->
                    resolvedProperty.getValue(s).map(c -> (Comparable<?>) c));
            return new PropPredicate(block, resolvedProperty, value, op);
        }));

        private final Block fromBlock;
        private final Property<?> property;
        private final Operator operator;

        @Nullable
        private final Comparable<?> targetValue;
        private final Integer intValue;


        public PropPredicate(Block fromBlock, Property<?> property, Optional<Comparable<?>> value, Operator operator) {
            this.property = property;
            this.targetValue = value.orElse(null);
            this.fromBlock = fromBlock;
            this.operator = operator;
            if (property instanceof IntegerProperty && operator != null && targetValue instanceof Integer i) {
                intValue = i;
            } else intValue = null;
        }



        public Block getFromBlock() {
            return fromBlock;
        }

        public Optional<Comparable<?>> getTargetValue() {
            return Optional.ofNullable(targetValue);
        }

        public Property<?> getProperty() {
            return property;
        }

        public Operator getOperator() {
            return operator;
        }

        @Override
        public boolean test(BlockState state) {
            var val = state.getOptionalValue(property);
            if (val.isPresent()) {
                if (intValue != null) {
                    return operator.apply((Integer)val.get(),  intValue);
                }
                return targetValue == null || val.get() == targetValue;
            }
            return false;
        }

    }

    protected static Codec<Comparable<?>> valueCodec(Property<?> property) {
        return Codec.STRING.flatXmap(string -> property.getValue(string).map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown Property value" + string + " in " + property)),
                value -> DataResult.success(value.toString())
        );
    }

    protected static Codec<Property<? extends Comparable<?>>> propertyCodec(BlockState state) {
        return Codec.STRING.flatXmap(string -> {
                    for (var p : state.getProperties()) {
                        if (p.getName().equals(string)) {
                            return DataResult.success(p);
                        }
                    }
                    return DataResult.error(() -> "Unknown Property " + string + " in " + state);
                },
                property1 -> DataResult.success(property1.getName())
        );
    }




}


