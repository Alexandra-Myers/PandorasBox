package ivorius.pandorasbox.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @WrapOperation(method = "sendPairingData", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0))
    public void checkIfCustomPacket(Consumer<Packet<ClientGamePacketListener>> instance, Object packet, Operation<Void> original, @Local(ordinal = 0) ServerPlayer player) {
        if (packet == null && entity instanceof PandorasBoxEntity pandorasBoxEntity) {
            ServerPlayNetworking.send(player, pandorasBoxEntity.getCustomAddEntityPacket());
            return;
        }
        original.call(instance, packet);
    }
}
