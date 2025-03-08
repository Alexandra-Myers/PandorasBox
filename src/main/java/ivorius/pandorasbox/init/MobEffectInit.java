package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.mob_effects.AccessibleMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MobEffectInit {
    public static final Holder<MobEffect> SHRUNK = register("shrunk", new AccessibleMobEffect(MobEffectCategory.HARMFUL, 8986860)
            .addAttributeModifier(Attributes.SCALE, ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "effect.shrunk"), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "effect.shrunk_health"), -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    private static Holder<MobEffect> register(String name, MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mobEffect);
    }
    public static void registerEffects() {

    }
}
