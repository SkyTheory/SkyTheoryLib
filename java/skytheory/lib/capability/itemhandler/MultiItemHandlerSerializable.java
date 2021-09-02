package skytheory.lib.capability.itemhandler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

public class MultiItemHandlerSerializable extends MultiItemHandler implements INBTSerializable<NBTBase> {

	public List<INBTSerializable<NBTTagCompound>> writers;

	@SuppressWarnings("unchecked")
	public MultiItemHandlerSerializable(IItemHandler... handlers) {
		super(handlers);
		this.writers = Arrays.asList(handlers).stream()
				.filter(INBTSerializable.class::isInstance)
				.map(data -> (INBTSerializable<NBTTagCompound>) data)
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

}
