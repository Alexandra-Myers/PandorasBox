package ivorius.pandorasbox.networking;

import ivorius.pandorasbox.config.AtlasConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public record AtlasConfigPacket(AtlasConfig config) {
	public static AtlasConfigPacket decode(FriendlyByteBuf buf) {
		return new AtlasConfigPacket(AtlasConfig.staticLoadFromNetwork(buf));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(config.name);
		config.saveToNetwork(buf);
	}

	public void handle(NetworkEvent.Context ctx) {
		config.handleExtraSync(ctx);
	}
}