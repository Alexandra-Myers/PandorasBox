package ivorius.pandorasbox.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.DataFixUtils;
import ivorius.pandorasbox.PBConfig;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PBBlocks;
import ivorius.pandorasbox.block.TileEntityPandorasBox;
import ivorius.pandorasbox.commands.CommandPandorasBox;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.items.ItemPandorasBox;
import ivorius.pandorasbox.worldgen.PBLoot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

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
    public void onConfigChanged(ModConfig.ModConfigEvent event)
    {
        if ((event instanceof ModConfig.Reloading || event instanceof ModConfig.Loading) && event.getConfig().getModId().equals(PandorasBox.MOD_ID))
        {
            PBConfig.loadConfig();
        }
    }

    @SubscribeEvent
    public void onLoadLootTable(LootTableLoadEvent event)
    {
        if (PBConfig.allowLootTableInjection)
            PBLoot.injectLoot(event.getTable(), event.getName());
    }
    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent evt) {
        new CommandPandorasBox(evt.getDispatcher());
    }
    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        PBEffects.registerEffects();

        PBEffects.registerEffectCreators();
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
