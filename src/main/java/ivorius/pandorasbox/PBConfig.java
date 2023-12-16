package ivorius.pandorasbox;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Created by lukas on 29.07.14.
 */
public class PBConfig
{
    private static final String CONFIG_PREFIX = "gui." + PandorasBox.MOD_ID + ".config.";

//    public ModConfigSpec.BooleanValue allowLootTableInjection;
    public ModConfigSpec.DoubleValue boxLongevity;
    public ModConfigSpec.DoubleValue boxIntensity;
    public ModConfigSpec.DoubleValue goodEffectChance;
    public ModConfigSpec.IntValue maxEffectsPerBox;

    PBConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

//        allowLootTableInjection = builder.comment("Whether Pandora's Box will inject loot into loot tables")
//                .translation(CONFIG_PREFIX + "allowLootTableInjection").worldRestart()
//                .define("allowLootTableInjection", true);

        boxLongevity = builder.comment("How long a box will last (with continuous effects). Represented by 'chance to continue'.")
                .translation(CONFIG_PREFIX + "boxLongevity").worldRestart()
                .defineInRange("boxLongevity", 0.2, 0, 1);

        boxIntensity = builder.comment("How many effects a box will have at once. Represented by 'chance for another effect'. Ex.: 0 for 'Always exactly one effect', 3 for '3 times the default chance'.")
                .translation(CONFIG_PREFIX + "boxIntensity").worldRestart()
                .defineInRange("boxIntensity", 1.0, 0, 10);

        maxEffectsPerBox = builder.comment("The value up to which the intensity can increase. Keep in mind high values can cause strong lag.")
                .translation(CONFIG_PREFIX + "maxEffectsPerBox").worldRestart()
                .defineInRange("maxEffectsPerBox", 3, 1, 100);

        goodEffectChance = builder.comment("The chance for each effect to be 'positive'.")
                .translation(CONFIG_PREFIX + "goodEffectChance").worldRestart()
                .defineInRange("goodEffectChance", 0.49, 0, 10);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,builder.build());
    }
}
