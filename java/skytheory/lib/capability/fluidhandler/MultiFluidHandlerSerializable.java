package skytheory.lib.capability.fluidhandler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * NBTの読み書きを可能とするMultiTankWrapper<br>
 * 初期化時に与える値は常に同じであることを要求する
 * @author SkyTheory
 *
 */
public class MultiFluidHandlerSerializable extends MultiFluidHandler implements INBTSerializable<NBTBase> {

	public final List<INBTSerializable<NBTTagCompound>> writers;

	public MultiFluidHandlerSerializable(IFluidHandler... handlers) {
		super(handlers);
		this.writers = Arrays.asList(handlers).stream()
				.map(MultiFluidHandlerSerializable::toSerializer)
				.collect(Collectors.toList());
	}

	@Override
	public NBTBase serializeNBT() {
		NBTTagList list = new NBTTagList();
		this.writers.stream().map(writer -> writer.serializeNBT()).forEach(list::appendTag);
		return list;
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagList) {
			NBTTagList list = (NBTTagList) nbt;
			if (list.getTagType() == Constants.NBT.TAG_COMPOUND) {
				Iterator<INBTSerializable<NBTTagCompound>> it1 = writers.iterator();
				Iterator<NBTBase> it2 = list.iterator();
				while(it1.hasNext() && it2.hasNext()) {
					it1.next().deserializeNBT((NBTTagCompound) it2.next());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static INBTSerializable<NBTTagCompound> toSerializer(IFluidHandler handler) {
		if (handler instanceof INBTSerializable) return (INBTSerializable<NBTTagCompound>) handler;
		if (handler instanceof FluidTank) return new FluidTankWrapper((FluidTank) handler);
		throw new IllegalArgumentException("Handler cannot serialize.");
	}
}
