package skytheory.lib.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockRotationProperty extends EnumProperty<BlockRotation> {

	public static final BlockRotationProperty ALL_ROTATION = new BlockRotationProperty("rotation");
	
	public BlockRotationProperty(String pName) {
		super(pName, BlockRotation.class, List.of(BlockRotation.values()));
	}

	public BlockRotationProperty(String pName, BlockRotation... pValues) {
		super(pName, BlockRotation.class, List.of(pValues));
	}

	public BlockRotationProperty(String pName, Collection<BlockRotation> pValues) {
		super(pName, BlockRotation.class, pValues);
	}
	
	public static final Optional<BlockRotationProperty> getProperty(BlockState state) {
		return state.getProperties().stream()
				.filter(prop -> prop.getName() == "rotation")
				.filter(BlockRotationProperty.class::isInstance)
				.map(BlockRotationProperty.class::cast)
				.findFirst();
	}
	
}