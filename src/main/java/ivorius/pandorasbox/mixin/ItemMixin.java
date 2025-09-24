package ivorius.pandorasbox.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import ivorius.pandorasbox.component.PBEffectComponent;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.ComponentInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin {
    @WrapMethod(method = "use")
    public InteractionResult tryUseEffectComponent(Level level, Player player, InteractionHand interactionHand, Operation<InteractionResult> original) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        InteractionResult result = original.call(level, player, interactionHand);
        if (!result.consumesAction() && itemStack.has(ComponentInit.EFFECT_COMPONENT)) {
            PandorasBoxEntity resultBox = itemStack.get(ComponentInit.EFFECT_COMPONENT).createEffect(level, player, player.blockPosition(), true);
            if (resultBox == null) return result;
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else return result;
    }
}
