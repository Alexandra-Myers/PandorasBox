package ivorius.pandorasbox.client;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class PBSpriteSourceProvider extends SpriteSourceProvider {
    public static final ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "entity/pandoras_box");
    public PBSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, fileHelper, PandorasBox.MOD_ID);
    }
    @Override
    protected void addSources() {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(texture, Optional.empty()));
    }
}
