/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBEffectArgument;
import net.atlas.atlascore.command.argument.Argument;
import net.atlas.atlascore.command.argument.OptsArgument;
import net.atlas.atlascore.util.MapUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class PandoraCommand {

    @SuppressWarnings("unchecked")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        String[] args = new String[] {"first", "second", "third"};
        Map<String, ArgumentType<?>> realArgs = MapUtils.buildHashMapFromAlignedArrays(new String[]{"player", "effect", "invisible"}, new ArgumentType[]{EntityArgument.player(), PBEffectArgument.effect(), BoolArgumentType.bool()});
        Command<CommandSourceStack> cmd = context -> createBox(context, Argument.argumentMap(context, args));
        dispatcher.register(Commands.literal("pandora")
                .requires(cs -> cs.hasPermission(2))
                .executes(PandoraCommand::createBox)
                .then(Commands.argument("first", OptsArgument.fromMap(realArgs)).executes(cmd)
                        .then(Commands.argument("second", OptsArgument.fromMap(realArgs)).executes(cmd)
                                .then(Commands.argument("third", OptsArgument.fromMap(realArgs)).executes(cmd)))));
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
    public static int createBox(CommandContext<CommandSourceStack> commandContext, Argument.Arguments args) throws CommandSyntaxException {
        EntitySelector entitySelector = args.getArgumentOrElseGet("player", EntitySelector.class, () -> null);
        ServerPlayer player = entitySelector == null ? commandContext.getSource().getPlayerOrException() : entitySelector.findSinglePlayer(commandContext.getSource());
        PBEffectCreator effectCreator = args.getArgumentOrElseGet("effect", PBEffectCreator.class, () -> null);
        boolean invisible = args.getArgumentOrDefault("invisible", false);
        return createBox(player, effectCreator, invisible);
    }

}
