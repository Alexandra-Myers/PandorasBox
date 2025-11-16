package ivorius.pandorasbox.config;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.atlas.atlascore.AtlasCore;
import net.atlas.atlascore.config.AtlasConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PandoraConfig extends AtlasConfig {
	public static Map<ResourceLocation, List<ResourceLocation>> defaultTables;
	public static final Codec<Map<ResourceLocation, List<ResourceLocation>>> tableMapCodec = Codec.unboundedMap(ResourceLocation.CODEC, PBNBTHelper.withAlternative(Codec.list(ResourceLocation.CODEC), ResourceLocation.CODEC, Collections::singletonList));
	public TagHolder<Map<ResourceLocation, List<ResourceLocation>>> tables;
	public DoubleHolder boxLongevity;
	public DoubleHolder boxIntensity;
	public DoubleHolder goodEffectChance;
	public IntegerHolder maxEffectsPerBox;
	private Category balancing;
	public PandoraConfig() {
		super(new ResourceLocation(PandorasBox.MOD_ID, "pandoras-box"));
		declareDefaultForMod(PandorasBox.MOD_ID);
	}

	@Override
	public void loadExtra(JsonObject configJsonObject) {

	}

    @Override
    public void handleExtraSync(AtlasCore.AtlasConfigPacket atlasConfigPacket, LocalPlayer localPlayer, PacketSender packetSender) {
        
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
		defaultTables.put(new ResourceLocation("chests/pandora_inject"), List.of(
				new ResourceLocation("chests/abandoned_mineshaft"),
				new ResourceLocation("chests/jungle_temple"),
				new ResourceLocation("chests/simple_dungeon"),
				new ResourceLocation("chests/desert_pyramid"),
				new ResourceLocation("chests/stronghold_corridor"),
				new ResourceLocation("chests/stronghold_crossing"),
				new ResourceLocation("chests/stronghold_library"),
				new ResourceLocation("chests/bastion_bridge"),
				new ResourceLocation("chests/bastion_hoglin_stable"),
				new ResourceLocation("chests/bastion_other")
		));
		defaultTables.put(new ResourceLocation("chests/pandora_inject_common"), List.of(
				new ResourceLocation("chests/ancient_city"),
				new ResourceLocation("chests/bastion_treasure"),
				new ResourceLocation("chests/end_city_treasure")
		));
	}
}
