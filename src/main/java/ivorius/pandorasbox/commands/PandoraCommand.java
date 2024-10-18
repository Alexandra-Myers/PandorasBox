/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBEffectArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class PandoraCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pandora")
                .requires(cs->cs.hasPermission(2)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), null, false))
                        .then(Commands.argument("effect", PBEffectArgument.effect())
                                .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), PBEffectArgument.getEffect(ctx, "effect"), false)).then(Commands.argument("invisible", BoolArgumentType.bool())
                                        .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), PBEffectArgument.getEffect(ctx, "effect"), BoolArgumentType.getBool(ctx, "invisible")))))
                        .then(Commands.argument("invisible", BoolArgumentType.bool())
                                .executes(ctx -> createBox(EntityArgument.getPlayer(ctx, "player"), null, BoolArgumentType.getBool(ctx, "invisible"))))
                )
                .then(Commands.argument("effect", PBEffectArgument.effect())
                        .executes(ctx -> createBox(ctx.getSource().getPlayerOrException(), PBEffectArgument.getEffect(ctx, "effect"), false)).then(Commands.argument("invisible", BoolArgumentType.bool())
                                .executes(ctx -> createBox(ctx.getSource().getPlayerOrException(), PBEffectArgument.getEffect(ctx, "effect"), BoolArgumentType.getBool(ctx, "invisible")))))
                .then(Commands.argument("invisible", BoolArgumentType.bool())
                        .executes(ctx -> createBox(ctx.getSource().getPlayerOrException(), null, BoolArgumentType.getBool(ctx, "invisible"))))
                .executes(PandoraCommand::createBox));
    }
    public static int createBox(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return createBox(commandContext.getSource().getPlayerOrException(), null, false);
    }
    public static int createBox(ServerPlayer player, PBEffectCreator effectCreator, boolean invisible) {
        PandorasBoxEntity box;

        if (effectCreator != null) {
            box = PBECRegistry.spawnPandorasBox(player.level(), player.getCommandSenderWorld().random, effectCreator, player);
        } else
            box = PBECRegistry.spawnPandorasBox(player.level(), player.getCommandSenderWorld().random, true, player);

        if (box != null) {
            if (invisible) {
                box.setInvisible(true);
                box.stopFloating();
            }
        }
        return 1;

    }
}
