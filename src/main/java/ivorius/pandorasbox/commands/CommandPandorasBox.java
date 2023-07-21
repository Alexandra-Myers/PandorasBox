/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBEffectArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;

public class CommandPandorasBox
{

    public CommandPandorasBox(CommandDispatcher<CommandSource> dispatcher) {
        register(dispatcher);
    }
    static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("pandora")
                .requires(cs->cs.hasPermission(2)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), null, false)).then(Commands.argument("effect", PBEffectArgument.effect())
                                .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), PBEffectArgument.getEffect(ctx, "effect"), false)).then(Commands.argument("invisible", BoolArgumentType.bool())
                                        .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), PBEffectArgument.getEffect(ctx, "effect"), BoolArgumentType.getBool(ctx, "invisible")))))
                ));
    }
    public static int createBox(Entity player, PBEffectCreator effectCreator, boolean bool) {
        PandorasBoxEntity box;

        if (effectCreator != null)
        {
            box = PBECRegistry.spawnPandorasBox(player.level, player.getCommandSenderWorld().random, effectCreator, player);
            if (box != null)
                box.setCanGenerateMoreEffectsAfterwards(false);
        }
        else
            box = PBECRegistry.spawnPandorasBox(player.level, player.getCommandSenderWorld().random, true, player);
        if (box != null)
        {
            if (bool)
            {
                box.setInvisible(true);
                box.stopFloating();
            }
        }
        return 1;

    }
}