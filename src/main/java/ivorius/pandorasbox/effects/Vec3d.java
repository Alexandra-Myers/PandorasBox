package ivorius.pandorasbox.effects;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Vec3d extends Vec3 {
    public Vec3d(double p_i47092_1_, double p_i47092_3_, double p_i47092_5_) {
        super(p_i47092_1_, p_i47092_3_, p_i47092_5_);
    }
    public Vec3d(Vector3f vector3f) {
        super(vector3f);
    }
    public static Vec3d fromVector3d(Vector3d base) {
        return new Vec3d(base.x, base.y, base.z);
    }
}
