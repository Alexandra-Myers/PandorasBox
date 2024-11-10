package ivorius.pandorasbox.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.init.Init;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PBEffectArgument implements ArgumentType<PBEffectCreator> {
    static final DynamicCommandExceptionType ERROR_UNKNOWN_EFFECT_HOLDER = new DynamicCommandExceptionType((object) -> Component.translatableEscape("argument.pandora_effect.id.invalid", object));
    private static final Collection<String> EXAMPLES = Arrays.asList("cityscape", "minecraft:in_the_end");

    public static PBEffectArgument effect() {
        return new PBEffectArgument();
    }

    public static PBEffectCreator getEffect(final CommandContext<?> context, String name) {
        return context.getArgument(name, PBEffectCreator.class);
    }
    @Override
    public PBEffectCreator parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        ResourceLocation resourcelocation = ResourceLocation.read(reader);
        Optional<EffectHolder> effectHolder = Init.EFFECT_HOLDER_REGISTRY.getOptional(resourcelocation);
        if (effectHolder.isEmpty()) {
            reader.setCursor(cursor);
            throw ERROR_UNKNOWN_EFFECT_HOLDER.create(resourcelocation);
        }
        return effectHolder.get().effectCreator;
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
