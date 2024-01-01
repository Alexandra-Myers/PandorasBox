package ivorius.pandorasbox.events;

import ivorius.pandorasbox.commands.CommandPandorasBox;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.List;
import java.util.Objects;

import static ivorius.pandorasbox.PandorasBox.*;

/**
 * Created by lukas on 29.07.14.
 */
public class PBEventHandler
{
    public void register()
    {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent evt) {
        new CommandPandorasBox(evt.getDispatcher());
    }
    @SubscribeEvent
    public void onPlayerInteractAir(PlayerInteractEvent.RightClickEmpty event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if(item instanceof BlockItem) {
            BlockPos pos = event.getEntity().blockPosition();
            Direction direction = event.getEntity().getDirection();
            BlockPos frontPos = getPosInFront(pos, direction);
            ((BlockItem) item).place(new BlockPlaceContext(event.getEntity(), event.getHand(), stack,
                    new BlockHitResult(new Vec3(frontPos.getX() + 0.5 + direction.getStepX() * 0.5, frontPos.getY() + 0.5 + direction.getStepY() * 0.5, frontPos.getZ() + 0.5 + direction.getStepZ() * 0.5), direction, frontPos, false)));
        }
    }
    @SubscribeEvent
    public void serverInit(ServerStartedEvent event) {
        initPB();
    }
    @SubscribeEvent
    public void datapackReload(OnDatapackSyncEvent event) {
        if(event.getPlayer() != null) return;
        initPB();
    }
    public static void initPB() {
        logs = new ArrayListExtensions<>();
        leaves = new ArrayListExtensions<>();
        flowers = new ArrayListExtensions<>();
        wool = new ArrayListExtensions<>();
        slabs = new ArrayListExtensions<>();
        bricks = new ArrayListExtensions<>();
        terracotta = new ArrayListExtensions<>();
        stained_terracotta = new ArrayListExtensions<>();
        planks = new ArrayListExtensions<>();
        stained_glass = new ArrayListExtensions<>();
        saplings = new ArrayListExtensions<>();
        pots = new ArrayListExtensions<>();
        List<Block> blocks = BuiltInRegistries.BLOCK.stream().toList();
        for (Block block : blocks) {
            if (block.defaultBlockState().is(BlockTags.LOGS)) {
                logs.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.LEAVES)) {
                leaves.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.SMALL_FLOWERS)) {
                flowers.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.WOOL)) {
                wool.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.SLABS)) {
                slabs.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.STONE_BRICKS)) {
                bricks.add(block);
            }
            if (Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath().endsWith("terracotta")) {
                terracotta.add(block);
            }
            if (Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath().endsWith("_terracotta")) {
                stained_terracotta.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.PLANKS)) {
                planks.add(block);
            }
            if (block instanceof StainedGlassBlock) {
                stained_glass.add(block);
            }
            if (block instanceof SaplingBlock) {
                saplings.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.FLOWER_POTS)) {
                pots.add(block);
            }
        }
        PBEffects.registerEffectCreators();
    }
    public BlockPos getPosInFront(BlockPos pos, Direction direction) {
        return switch (direction) {
            case WEST -> pos.west();
            case EAST -> pos.east();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
            default -> pos;
        };
    }
}
