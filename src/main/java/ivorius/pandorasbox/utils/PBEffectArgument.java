package ivorius.pandorasbox.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PBEffectArgument implements ArgumentType<PBEffectCreator> {
    private static final Collection<String> EXAMPLES = Arrays.asList("cityscape", "minecraft:in_the_end");

    public static PBEffectArgument effect() {
        return new PBEffectArgument();
    }

    public static PBEffectCreator getEffect(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, PBEffectCreator.class);
    }
    @Override
    public PBEffectCreator parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ResourceLocation.read(reader);
        return Objects.requireNonNull(Registry.EFFECT_HOLDER_REGISTRY.get().getValue(resourcelocation)).effectCreator;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(Registry.EFFECT_HOLDER_REGISTRY.get().getKeys(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
