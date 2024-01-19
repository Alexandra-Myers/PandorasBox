package ivorius.pandorasbox.networking;

import ivorius.pandorasbox.PandorasBox;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketRegistration {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel MAIN = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(PandorasBox.MOD_ID, "pandoras_box_network"),
		() -> PROTOCOL_VERSION,
		PROTOCOL_VERSION::equals,
		NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION::equals)
	);

	public void init() {
		MAIN.messageBuilder(AtlasConfigPacket.class, 0).encoder(AtlasConfigPacket::encode).decoder(AtlasConfigPacket::decode).consumerMainThread(AtlasConfigPacket::handle).add();
	}
}