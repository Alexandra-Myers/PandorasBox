package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.CityMapper;
import ivorius.pandorasbox.utils.*;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CityMapperCreator(Either<Block, TagKey<Block>>[] targets, List<WeightedEntity> spawnerEntities, List<EquipmentSet> equipmentSets, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items) implements BlockMapperCreator {
    public static final MapCodec<CityMapperCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").forGetter(CityMapperCreator::targets),
                            WeightedEntity.NO_SPECIAL_CODEC.listOf().fieldOf("spawner_entities").forGetter(CityMapperCreator::spawnerEntities),
                            EquipmentSet.INDIRECT_CODEC.listOf().fieldOf("equipment_sets").forGetter(CityMapperCreator::equipmentSets),
                            RandomizedItemStack.LIST_CODEC.fieldOf("items").forGetter(CityMapperCreator::items))
                    .apply(instance, CityMapperCreator::new));


    @Override
    public BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random) {
        WeightedEntity[] entitySelection = PandorasBoxHelper.getRandomEntityList(random, spawnerEntities);
        List<EntityType<?>> entities = new ArrayList<>();
        for (WeightedEntity entity : entitySelection) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.tryParse(entity.entityID()));
            entities.add(type);
        }

        return new CityMapper(targets, entities, equipmentSets, items);
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapperCreator> codec() {
        return CODEC;
    }
}
