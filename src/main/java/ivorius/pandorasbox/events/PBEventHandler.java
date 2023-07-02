package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.commands.CommandPandorasBox;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static ivorius.pandorasbox.PandorasBox.*;

/**
 * Created by lukas on 29.07.14.
 */
@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PBEventHandler
{
    public void register()
    {
        MinecraftForge.EVENT_BUS.register(this);
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
            BlockPos pos = event.getPlayer().blockPosition();
            Direction direction = event.getPlayer().getDirection();
            BlockPos frontPos = getPosInFront(pos, direction);
            ((BlockItem) item).place(new BlockItemUseContext(event.getPlayer(), event.getHand(), stack,
                    new BlockRayTraceResult(new Vector3d(frontPos.getX() + 0.5 + direction.getStepX() * 0.5, frontPos.getY() + 0.5 + direction.getStepY() * 0.5, frontPos.getZ() + 0.5 + direction.getStepZ() * 0.5), direction, frontPos, false)));
        }
    }
    @SubscribeEvent
    public void serverInit(FMLServerStartedEvent event) {
        initPB();
    }
    @SubscribeEvent
    public void datapackReload(OnDatapackSyncEvent event) {
        if(event.getPlayer() != null) return;
        initPB();
    }
    public void initPB() {
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
        for (Block block : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block)) {
                logs.add(block);
            }
            if (BlockTags.LEAVES.contains(block)) {
                leaves.add(block);
            }
            if (BlockTags.SMALL_FLOWERS.contains(block)) {
                flowers.add(block);
            }
            if (BlockTags.WOOL.contains(block)) {
                wool.add(block);
            }
            if (BlockTags.SLABS.contains(block)) {
                slabs.add(block);
            }
            if (BlockTags.STONE_BRICKS.contains(block)) {
                bricks.add(block);
            }
            if (block.getRegistryName().getPath().endsWith("terracotta")) {
                terracotta.add(block);
            }
            if (block.getRegistryName().getPath().endsWith("_terracotta")) {
                stained_terracotta.add(block);
            }
            if (BlockTags.PLANKS.contains(block)) {
                planks.add(block);
            }
            if (block instanceof StainedGlassBlock) {
                stained_glass.add(block);
            }
            if (block instanceof SaplingBlock) {
                saplings.add(block);
            }
            if (BlockTags.FLOWER_POTS.contains(block)) {
                pots.add(block);
            }
        }
        PBEffects.registerEffectCreators();
    }
    public BlockPos getPosInFront(BlockPos pos, Direction direction) {
        switch (direction) {
            case WEST:
                return pos.west();
            case EAST:
                return pos.east();
            case NORTH:
                return pos.north();
            case SOUTH:
                return pos.south();
            default:
                return pos;
        }
    }
}
