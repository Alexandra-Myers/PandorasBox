package ivorius.pandorasbox.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ivorius.pandorasbox.init.Init;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @ModifyExpressionValue(method = "load", at = @At(value = "FIELD", target = "Lnet/minecraft/resources/RegistryDataLoader;WORLDGEN_REGISTRIES:Ljava/util/List;"))
    private static List<RegistryDataLoader.RegistryData<?>> editData(List<RegistryDataLoader.RegistryData<?>> original) {
        ArrayList<RegistryDataLoader.RegistryData<?>> newData = new ArrayList<>(original);
        original.addAll(Init.PANDORA_DYNAMIC_REGISTRIES);
        return newData;
    }
}
