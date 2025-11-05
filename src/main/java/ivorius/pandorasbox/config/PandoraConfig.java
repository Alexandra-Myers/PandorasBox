package ivorius.pandorasbox.config;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import net.atlas.atlascore.AtlasCore;
import net.atlas.atlascore.config.AtlasConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PandoraConfig extends AtlasConfig {
	public static Map<Identifier, List<Identifier>> defaultTables;
	public static final Codec<Map<Identifier, List<Identifier>>> tableMapCodec = Codec.unboundedMap(Identifier.CODEC, Codec.withAlternative(Codec.list(Identifier.CODEC), Identifier.CODEC, Collections::singletonList));
	public TagHolder<Map<Identifier, List<Identifier>>> tables;
	public DoubleHolder boxLongevity;
	public DoubleHolder boxIntensity;
	public DoubleHolder goodEffectChance;
	public IntegerHolder maxEffectsPerBox;
	private Category balancing;
	public PandoraConfig() {
		super(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandoras-box"));
		declareDefaultForMod(PandorasBox.MOD_ID);
	}

	@Override
	public void loadExtra(JsonObject configJsonObject) {

	}

	@Override
	public void handleExtraSync(AtlasCore.AtlasConfigPacket atlasConfigPacket, ClientPlayNetworking.Context context) {

	}

	@Override
	public void handleConfigInformation(AtlasCore.ClientInformPacket clientInformPacket, ServerPlayer serverPlayer, PacketSender packetSender) {

	}

	@Override
	public Screen createScreen(Screen screen) {
		return null;
	}

	@Override
	public void defineConfigHolders() {
		tables = createCodecBacked("tables", defaultTables, tableMapCodec);
		tables.tieToCategory(balancing);
		tables.setupTooltip(1);
		boxLongevity = createInRange("box_longevity", 0.2, 0, 1);
		boxLongevity.tieToCategory(balancing);
		boxLongevity.setupTooltip(2);
		boxIntensity = createInRange("box_intensity", 1.0, 0, 10);
		boxIntensity.tieToCategory(balancing);
		boxIntensity.setupTooltip(1);
		goodEffectChance = createInRange("good_effect_chance", 0.49, 0, 1);
		goodEffectChance.tieToCategory(balancing);
		goodEffectChance.setupTooltip(1);
		maxEffectsPerBox = createInRange("max_effects_per_box", 3, 1, 100, true);
		maxEffectsPerBox.tieToCategory(balancing);
		maxEffectsPerBox.setupTooltip(1);
	}

	@Override
	public @NotNull List<Category> createCategories() {
		List<Category> baseCategories = super.createCategories();
		balancing = new Category(this, "balancing", new ArrayList<>());
		baseCategories.add(balancing);
		return baseCategories;
	}

	@Override
	public void resetExtraHolders() {

	}

	@Override
	public <T> void alertChange(ConfigValue<T> tConfigValue, T newValue) {

	}

	@Override
	public <T> void alertClientValue(ConfigValue<T> configValue, T t, T t1) {

	}
	static {
		defaultTables = new HashMap<>();
		defaultTables.put(Identifier.parse("chests/pandora_inject"), List.of(
				Identifier.withDefaultNamespace("chests/abandoned_mineshaft"),
				Identifier.withDefaultNamespace("chests/jungle_temple"),
				Identifier.withDefaultNamespace("chests/simple_dungeon"),
				Identifier.withDefaultNamespace("chests/desert_pyramid"),
				Identifier.withDefaultNamespace("chests/stronghold_corridor"),
				Identifier.withDefaultNamespace("chests/stronghold_crossing"),
				Identifier.withDefaultNamespace("chests/stronghold_library"),
				Identifier.withDefaultNamespace("chests/bastion_bridge"),
				Identifier.withDefaultNamespace("chests/bastion_hoglin_stable"),
				Identifier.withDefaultNamespace("chests/bastion_other")
		));
		defaultTables.put(Identifier.parse("chests/pandora_inject_common"), List.of(
				Identifier.withDefaultNamespace("chests/ancient_city"),
				Identifier.withDefaultNamespace("chests/bastion_treasure"),
				Identifier.withDefaultNamespace("chests/end_city_treasure")
		));
	}
}
