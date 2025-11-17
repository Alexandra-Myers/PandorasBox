package ivorius.pandorasbox.utils;

import ivorius.pandorasbox.PandorasBoxClient;
import net.minecraft.core.RegistryAccess;

import java.util.Optional;

public class SidedUtil {
    public static Optional<RegistryAccess> getAccessOnClient() {
        return Optional.ofNullable(PandorasBoxClient.tryGetClientRegistryAccess());
    }
}
