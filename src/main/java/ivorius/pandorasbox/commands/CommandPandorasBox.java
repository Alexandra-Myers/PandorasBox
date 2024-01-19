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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class CommandPandorasBox {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pandora")
                .requires(cs->cs.hasPermission(2)) //permission
                .executes(ctx -> createBox(ctx.getSource().getPlayerOrException(), null, false))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), null, false)).then(Commands.argument("effect", PBEffectArgument.effect())
                                .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), PBEffectArgument.getEffect(ctx, "effect"), false)).then(Commands.argument("invisible", BoolArgumentType.bool())
                                        .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), PBEffectArgument.getEffect(ctx, "effect"), BoolArgumentType.getBool(ctx, "invisible")))))
                ));
    }
    public static int createBox(ServerPlayer player, PBEffectCreator effectCreator, boolean bool) {
        PandorasBoxEntity box;

        if (effectCreator != null) {
            box = PBECRegistry.spawnPandorasBox(player.level(), player.getCommandSenderWorld().random, effectCreator, player);

            if (box != null)
                box.setCanGenerateMoreEffectsAfterwards(false);
        } else
            box = PBECRegistry.spawnPandorasBox(player.level(), player.getCommandSenderWorld().random, true, player);

        if (box != null) {
            if (bool) {
                box.setInvisible(true);
                box.stopFloating();
            }
        }
        return 1;

    }
}
