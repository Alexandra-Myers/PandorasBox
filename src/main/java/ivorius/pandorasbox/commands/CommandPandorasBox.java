/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ivorius.brigadieropts.commands.ArgumentName;
import ivorius.brigadieropts.commands.ExpectingCommand;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBEffectArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class CommandPandorasBox extends ExpectingCommand {

    public CommandPandorasBox(CommandDispatcher<CommandSource> dispatcher) {
        super(dispatcher);
    }
    @Override
    public int execute(CommandSource source, Map<String, Object> args) {
        Entity player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        PBEffectCreator effectCreator = null;
        boolean bool = false;

        if (args.containsKey("player")) {
            player = (Entity) args.get("player");
        }
        if (args.containsKey("effect")) {
            effectCreator = (PBEffectCreator) args.get("effect");
        }
        if (args.containsKey("invisible")) {
            bool = (boolean) args.get("invisible");
        }
        assert player != null;
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
    @Override
    public int permissionLevel() {
        return 2;
    }
    @Override
    public String commandName() {
        return "pandora";
    }
    @Override
    public Map<ArgumentName, ArgumentType<?>> typeMap() {
        Map<ArgumentName, ArgumentType<?>> map = new HashMap<>();
        map.put(new ArgumentName("player", false, false, ((commandSourceCommandContext, s) -> {
            try {
                return EntityArgument.getPlayer(commandSourceCommandContext, s);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        })), EntityArgument.player());
        map.put(new ArgumentName("effect", false, false, PBEffectArgument::getEffect), PBEffectArgument.effect());
        map.put(new ArgumentName("invisible", false, false, BoolArgumentType::getBool), BoolArgumentType.bool());
        return map;
    }
}
