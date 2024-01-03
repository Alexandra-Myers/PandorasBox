package ivorius.pandorasbox.init;

import ivorius.pandorasbox.utils.PBEffectArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;

public class ArgumentInit {
    public static final ArgumentTypeInfo<PBEffectArgument, SingletonArgumentInfo<PBEffectArgument>.Template> PBEFFECTARGUMENT = ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, "pbeffect", PBEffectArgument.class, SingletonArgumentInfo.contextFree(PBEffectArgument::effect));
    public static void registerArguments() {

    }
}
