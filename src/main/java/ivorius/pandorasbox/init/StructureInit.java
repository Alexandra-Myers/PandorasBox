package ivorius.pandorasbox.init;

import ivorius.pandorasbox.worldgen.structure.PandoraShrineStructure;
import ivorius.pandorasbox.worldgen.structure.piece.PandoraShrinePiece;
import ivorius.pandorasbox.worldgen.structure.processor.EndStoneReplaceProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class StructureInit {
    private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, MOD_ID);
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MOD_ID);
    private static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, MOD_ID);

    public static final RegistryObject<StructurePieceType> PANDORA_SHRINE_PIECE = registerTemplateStructurePiece("pandora_shrine_piece", () -> PandoraShrinePiece::new);
    public static final RegistryObject<StructureType<PandoraShrineStructure>> PANDORA_SHRINE = registerStructure("pandora_shrine", () -> () -> PandoraShrineStructure.CODEC);
    public static final RegistryObject<StructureProcessorType<EndStoneReplaceProcessor>> END_STONE_REPLACE = registerStructureProcessor("end_stone_replace", () -> () -> EndStoneReplaceProcessor.CODEC);
    private static RegistryObject<StructurePieceType> registerTemplateStructurePiece(String name, Supplier<StructurePieceType.StructureTemplateType> feature) {
        return STRUCTURE_PIECE_TYPES.register(name, feature);
    }
    private static <T extends Structure> RegistryObject<StructureType<T>> registerStructure(String name, Supplier<StructureType<T>> feature) {
        return STRUCTURE_TYPES.register(name, feature);
    }
    private static <T extends StructureProcessor> RegistryObject<StructureProcessorType<T>> registerStructureProcessor(String name, Supplier<StructureProcessorType<T>> feature) {
        return STRUCTURE_PROCESSOR_TYPES.register(name, feature);
    }
    public static void registerStructures(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        STRUCTURE_PIECE_TYPES.register(bus);
        STRUCTURE_PROCESSOR_TYPES.register(bus);
    }
}
