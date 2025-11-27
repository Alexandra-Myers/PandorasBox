package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.worldgen.structure.PandoraShrineStructure;
import ivorius.pandorasbox.worldgen.structure.piece.PandoraShrinePiece;
import ivorius.pandorasbox.worldgen.structure.processor.EndStoneReplaceProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class StructureInit {
    public static final StructurePieceType PANDORA_SHRINE_PIECE = registerTemplateStructurePiece("pandora_shrine_piece", PandoraShrinePiece::new);
    public static final StructureType<PandoraShrineStructure> PANDORA_SHRINE = registerStructure("pandora_shrine", () -> PandoraShrineStructure.CODEC);
    public static final StructureProcessorType<EndStoneReplaceProcessor> END_STONE_REPLACE = registerStructureProcessor("end_stone_replace", () -> EndStoneReplaceProcessor.CODEC);
    private static StructurePieceType registerTemplateStructurePiece(String name, StructurePieceType.StructureTemplateType feature) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name), feature);
    }
    private static <T extends Structure> StructureType<T> registerStructure(String name, StructureType<T> feature) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name), feature);
    }
    private static <T extends StructureProcessor> StructureProcessorType<T> registerStructureProcessor(String name, StructureProcessorType<T> feature) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name), feature);
    }
    public static void registerStructures() {

    }
}