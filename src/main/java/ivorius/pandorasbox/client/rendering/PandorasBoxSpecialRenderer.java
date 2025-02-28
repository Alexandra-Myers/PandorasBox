package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.*;

@Environment(EnvType.CLIENT)
public class PandorasBoxSpecialRenderer implements NoDataSpecialModelRenderer {
	private final PandorasBoxBlockEntityRenderer renderer;

	public PandorasBoxSpecialRenderer(PandorasBoxBlockEntityRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void render(ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl) {
		renderer.renderItem(poseStack, multiBufferSource, i, j);
	}

	@Environment(EnvType.CLIENT)
	public record Unbaked() implements SpecialModelRenderer.Unbaked {
		public static final MapCodec<PandorasBoxSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

		@Override
		public MapCodec<PandorasBoxSpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
			return new PandorasBoxSpecialRenderer(new PandorasBoxBlockEntityRenderer(entityModelSet));
		}
	}
}