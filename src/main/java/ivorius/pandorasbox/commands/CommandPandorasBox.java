/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.PBEffectArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
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
                .then(Commands.argument("player", EntityArgument.player()).executes(context -> createBox(context, null, false)).then(Commands.argument("effect", PBEffectArgument.effect()).executes(ctx -> createBox(ctx, PBEffectArgument.getEffect(ctx, "effect"), false)).then(Commands.argument("invisible", BoolArgumentType.bool()))
                        .executes(ctx -> createBox(ctx, PBEffectArgument.getEffect(ctx, "effect"), BoolArgumentType.getBool(ctx, "invisible"))
                        ))
                ));
    }
    public static int createBox(CommandContext<CommandSource> ctx, PBEffectCreator effectCreator, boolean bool) throws CommandSyntaxException {
        Entity player = EntityArgument.getPlayer(ctx, "player");

        EntityPandorasBox box;

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
