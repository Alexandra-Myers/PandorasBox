package ivorius.pandorasbox.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import ivorius.pandorasbox.PandorasBox;
import net.atlas.atlascore.AtlasCore;
import net.atlas.atlascore.config.AtlasConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

public class PandoraConfig extends AtlasConfig {
	public Map<ResourceLocation, ResourceLocation> configuredTables;
	public DoubleHolder boxLongevity;
	public DoubleHolder boxIntensity;
	public DoubleHolder goodEffectChance;
	public IntegerHolder maxEffectsPerBox;
	private Category balancing;
	public PandoraConfig() {
		super(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandoras-box"));
		declareDefaultForMod(PandorasBox.MOD_ID);
	}

	@Override
	protected InputStream getDefaultedConfig() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name.getPath() + ".json");
	}

	@Override
	public void loadExtra(JsonObject configJsonObject) {
		if (!configJsonObject.has("tables"))
			configJsonObject.add("tables", new JsonArray());
		JsonElement tables = configJsonObject.get("tables");
		if (tables instanceof JsonArray tableArray) {
			tableArray.asList().forEach(jsonElement -> {
				if (jsonElement instanceof JsonObject jsonObject) {
					if (jsonObject.get("original_table") instanceof JsonArray tablesWithConfig) {
						tablesWithConfig.asList().forEach(
								tableName -> parseConfiguredTable(tableName, jsonObject)
						);
					} else
						parseConfiguredTable(jsonObject.get("original_table"), jsonObject);
				} else
					throw new ReportedException(CrashReport.forThrowable(new IllegalStateException("Not a JSON Object: " + jsonElement + " this may be due to an incorrectly written config file."), "Configuring Items"));
			});
		}
	}

	@Override
	public PandoraConfig loadFromNetwork(RegistryFriendlyByteBuf buf) {
		super.loadFromNetwork(buf);
		configuredTables = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readResourceLocation);
		return this;
	}

	@Override
	public void saveToNetwork(RegistryFriendlyByteBuf buf) {
		super.saveToNetwork(buf);
		buf.writeMap(configuredTables, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeResourceLocation);
	}

	@Override
	public void saveExtra(JsonWriter jsonWriter, PrintWriter printWriter) {
		BiMap<ResourceLocation[], ResourceLocation> arraysOfTables = HashBiMap.create();
		configuredTables.forEach((key, value) -> {
			if (arraysOfTables.containsValue(value)) {
				ResourceLocation[] originalArray = arraysOfTables.inverse().get(value);
				originalArray = Arrays.copyOf(originalArray, originalArray.length + 1);
				originalArray[originalArray.length - 1] = key;
				arraysOfTables.put(originalArray, value);
			} else {
				arraysOfTables.put(new ResourceLocation[] {key}, value);
			}
		});
        try {
			jsonWriter.name("tables");
            jsonWriter.beginArray();
			arraysOfTables.forEach((resourceLocations, resourceLocation) -> {
                try {
                    jsonWriter.beginObject();
					jsonWriter.name("original_table");
					if (resourceLocations.length == 1) jsonWriter.value(resourceLocations[0].toString());
					else {
						jsonWriter.beginArray();
						for (ResourceLocation key : resourceLocations) jsonWriter.value(key.toString());
						jsonWriter.endArray();
					}
					jsonWriter.name("appended_table");
					jsonWriter.value(resourceLocation.toString());
					jsonWriter.endObject();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
			jsonWriter.endArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	public void handleExtraSync(AtlasCore.AtlasConfigPacket atlasConfigPacket, LocalPlayer localPlayer, PacketSender packetSender) {

	}

	@Override
	public Screen createScreen(Screen screen) {
		return null;
	}

	public void parseConfiguredTable(JsonElement jsonElement, JsonObject jsonObject) {
		ResourceLocation originalTable = ResourceLocation.tryParse(jsonElement.getAsString());
		ResourceLocation appendedTable;
		if (!jsonObject.has("appended_table"))
			return;
		appendedTable = ResourceLocation.tryParse(getString(jsonObject, "appended_table"));
		configuredTables.put(originalTable, appendedTable);
	}

	@Override
	public void defineConfigHolders() {
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
		configuredTables = new HashMap<>();
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
		configuredTables = new HashMap<>();
	}

	@Override
	public <T> void alertChange(ConfigValue<T> tConfigValue, T newValue) {

	}
}
