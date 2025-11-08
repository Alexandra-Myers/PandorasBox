/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.atlas.atlascore.command.argument.Argument;
import net.atlas.atlascore.command.argument.OptsArgument;
import net.atlas.atlascore.util.MapUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;

public class PandoraCommand {

    @SuppressWarnings("unchecked")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        Map<String, ArgumentType<?>> realArgs = MapUtils.buildHashMapFromAlignedArrays(new String[]{"player", "effect", "render_item", "invisible"}, new ArgumentType[]{EntityArgument.player(), ResourceArgument.resource(commandBuildContext, Init.EFFECT_HOLDER_REGISTRY_KEY), ItemArgument.item(commandBuildContext), BoolArgumentType.bool()});
        OptsArgument optsArgument = OptsArgument.fromMap(realArgs);
        Command<CommandSourceStack> cmd = context -> createBox(context, Argument.argumentMap(optsArgument, context, "arguments"));
        dispatcher.register(Commands.literal("pandora")
                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(PandoraCommand::createBox)
                .then(Commands.argument("arguments", StringArgumentType.greedyString()).suggests(optsArgument::suggestions).executes(cmd)));
    }
    public static int createBox(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return createBox(commandContext.getSource().getPlayerOrException(), null, Optional.empty(), false);
    }
    public static int createBox(ServerPlayer player, PBEffectCreator effectCreator, Optional<ItemStack> renderItem, boolean invisible) {
        PandorasBoxEntity box;

        if (effectCreator != null)
            box = PBECRegistry.spawnPandorasBox(player.level(), player.level().random, renderItem, effectCreator, player);
        else
            box = PBECRegistry.spawnPandorasBox(player.level(), player.level().random, renderItem, true, player);

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
        @SuppressWarnings("unchecked") Holder.Reference<EffectHolder> effectHolderReference = args.getArgumentOrElseGet("effect", Holder.Reference.class, () -> null);
        PBEffectCreator effectCreator = null;
        if (effectHolderReference != null) effectCreator = effectHolderReference.value().effectCreator();
        ItemInput input = args.getArgumentOrElseGet("render_item", ItemInput.class, () -> null);
        Optional<ItemStack> renderItem = Optional.empty();
        if (input != null) renderItem = Optional.of(input.createItemStack(1, false));
        boolean invisible = args.getArgumentOrDefault("invisible", false);
        return createBox(player, effectCreator, renderItem, invisible);
    }

}
