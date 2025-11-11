package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class PandorasBoxSpecialRenderer implements NoDataSpecialModelRenderer {
	private final PandorasBoxBlockEntityRenderer renderer;

	public PandorasBoxSpecialRenderer(PandorasBoxBlockEntityRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void getExtents(Set<Vector3f> set) {
		this.renderer.getExtents(set);
	}

	@Override
	public void submit(ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, int overlayTexture, boolean hasFoil, int outlineColor) {
		this.renderer.renderItem(poseStack, submitNodeCollector, packedLightIn, overlayTexture, outlineColor, hasFoil);
	}

	@Environment(EnvType.CLIENT)
	public record Unbaked() implements SpecialModelRenderer.Unbaked {
		public static final MapCodec<PandorasBoxSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

		@Override
		public @Nullable SpecialModelRenderer<?> bake(BakingContext bakingContext) {
			return new PandorasBoxSpecialRenderer(new PandorasBoxBlockEntityRenderer(bakingContext.entityModelSet()));
		}

		@Override
		public MapCodec<PandorasBoxSpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}
	}
}