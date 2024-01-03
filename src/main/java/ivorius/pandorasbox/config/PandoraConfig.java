package ivorius.pandorasbox.config;

import com.google.gson.*;
import ivorius.pandorasbox.PandorasBox;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PandoraConfig extends AtlasConfig {
	public Map<ResourceLocation, ResourceLocation> configuredTables;
	public DoubleHolder boxLongevity;
	public DoubleHolder boxIntensity;
	public DoubleHolder goodEffectChance;
	public IntegerHolder maxEffectsPerBox;
	public PandoraConfig() {
		super(new ResourceLocation(PandorasBox.MOD_ID, "pandoras-box"));
	}

	@Override
	protected InputStream getDefaultedConfig() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name.getPath() + ".json");
	}

	@Override
	public void loadExtra() {
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
	public PandoraConfig loadFromNetwork(FriendlyByteBuf buf) {
		super.loadFromNetwork(buf);
		configuredTables = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readResourceLocation);
		return this;
	}

	@Override
	public void saveToNetwork(FriendlyByteBuf buf) {
		super.saveToNetwork(buf);
		buf.writeMap(configuredTables, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeResourceLocation);
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
		boxIntensity = createInRange("box_intensity", 1.0, 0, 10);
		goodEffectChance = createInRange("good_effect_chance", 0.49, 0, 10);
		maxEffectsPerBox = createInRange("max_effects_per_box", 3, 1, 100);
		configuredTables = new HashMap<>();

	}

	@Override
	public <T> void alertChange(ConfigValue<T> tConfigValue, T newValue) {

	}
}
