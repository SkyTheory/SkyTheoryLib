package skytheory.lib.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.Vec3;

public class SkyTheoryDataSerializers {

	public static final EntityDataSerializer<Vec3> SERIALIZER_VEC3 = EntityDataSerializer.simple(
			SkyTheoryDataSerializers::writeVector,
			SkyTheoryDataSerializers::readVector);

	public static final EntityDataSerializer<BlockRotation> SERIALIZER_ROTATION = EntityDataSerializer.simpleEnum(BlockRotation.class);
	
	static {
		EntityDataSerializers.registerSerializer(SERIALIZER_VEC3);
		EntityDataSerializers.registerSerializer(SERIALIZER_ROTATION);
	}
	
	private static void writeVector(FriendlyByteBuf buf, Vec3 vector) {
		buf.writeDouble(vector.x);
		buf.writeDouble(vector.y);
		buf.writeDouble(vector.z);
	}
	
	private static Vec3 readVector(FriendlyByteBuf buf) {
		return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
}
