package ivorius.pandorasbox.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.init.Init;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PBEffectArgument implements ArgumentType<PBEffectCreator> {
    private static final Collection<String> EXAMPLES = Arrays.asList("cityscape", "minecraft:in_the_end");

    public static PBEffectArgument effect() {
        return new PBEffectArgument();
    }

    public static PBEffectCreator getEffect(final CommandContext<?> context, String name) {
        return context.getArgument(name, PBEffectCreator.class);
    }
    @Override
    public PBEffectCreator parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ResourceLocation.read(reader);
        return Init.EFFECT_HOLDER_REGISTRY.get(resourcelocation).effectCreator;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(Init.EFFECT_HOLDER_REGISTRY.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
