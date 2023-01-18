package ivorius.pandorasbox.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ivorius.pandorasbox.effectcreators.PBECDuplicateBox;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.random.DConstant;
import ivorius.pandorasbox.random.IConstant;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class PBEffectArgument implements ArgumentType<PBEffectCreator> {

    public static PBEffectArgument effect() {
        return new PBEffectArgument();
    }

    public static PBEffectCreator getEffect(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, PBEffectCreator.class);
    }
    @Override
    public PBEffectCreator parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ResourceLocation.read(reader);
        return PBECRegistry.getEffectCreators().getOrDefault(resourcelocation, new PBECDuplicateBox(new IConstant(PBEffectDuplicateBox.MODE_BOX_IN_BOX), new DConstant(0.5)));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(PBECRegistry.getAllIDsAsRL().asIterable(), builder);
    }
}
