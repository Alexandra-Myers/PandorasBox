package ivorius.pandorasbox.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Holder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

import static ivorius.pandorasbox.utils.PBNBTHelper.RESOURCE_CONDITION_FAILED_ERROR;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @WrapOperation(method = "loadRegistryContents", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"))
    private static <T> DataResult<T> wrapDecode(Decoder<?> instance, DynamicOps<?> ops, Object input, Operation<DataResult<T>> original) {
        JsonElement element = (JsonElement) input;
        if (element.isJsonObject() && !ResourceConditions.objectMatchesConditions(element.getAsJsonObject())) return DataResult.error(() -> RESOURCE_CONDITION_FAILED_ERROR);
        return original.call(instance, ops, input);
    }
    @WrapOperation(method = "loadRegistryContents", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;getOrThrow(ZLjava/util/function/Consumer;)Ljava/lang/Object;"))
    private static <T> T wrapThrow(DataResult<T> instance, boolean allowPartial, Consumer<String> onError, Operation<T> original) {
        if (instance.error().filter(tPartialResult -> tPartialResult.message().equals(RESOURCE_CONDITION_FAILED_ERROR)).isPresent()) return null;
        return original.call(instance, allowPartial, onError);
    }
    @WrapOperation(method = "loadRegistryContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/WritableRegistry;register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder$Reference;"))
    private static <T> Holder.Reference<T> wrapRegister(WritableRegistry<T> instance, ResourceKey<T> tResourceKey, T t, Lifecycle lifecycle, Operation<Holder.Reference<T>> original) {
        if (t == null) return null;
        return original.call(instance, tResourceKey, t, lifecycle);
    }
}