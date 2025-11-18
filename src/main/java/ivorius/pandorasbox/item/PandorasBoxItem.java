package ivorius.pandorasbox.item;

import ivorius.pandorasbox.client.rendering.PBBlockEntityWithoutLevelRenderer;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class PandorasBoxItem extends BlockItem {
    public PandorasBoxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        createEffect(world, player, player.blockPosition(), true, player.getItemInHand(hand));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return super.use(world, player, hand);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new PBBlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            }
        });
    }

    public static PandorasBoxEntity createEffect(Level level, Player player, BlockPos pos, boolean floatAway, ItemStack heldStack) {
        if (level.isClientSide()) return null;
        return PBECRegistry.spawnPandorasBox(level, level.random, heldStack, Optional.empty(), true, player, pos, floatAway, HolderSet.direct());
    }
}
