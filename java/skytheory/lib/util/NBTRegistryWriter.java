package skytheory.lib.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * レジストリを扱うためのヘルパー<br>
 * NBTTagCompoundからレジストリに登録された値を取り出したり、値からNBTに書き込んだりする<br>
 * 存在しないレジストリのクラスを指定すると例外を返すので注意<br>
 * @author SkyTheory
 *
 */
public class NBTRegistryWriter {

	/**
	 * レジストリの要素をcompoundに書き込む
	 * @param compound
	 * @param key
	 * @param element
	 */
	public static <T extends IForgeRegistryEntry<T>> void writeToNBT(NBTTagCompound compound, String key, T element) {
		compound.setTag(key, getElementTag(element));
	}

	/**
	 * レジストリの要素をcompoundに書き込む
	 * @param compound
	 * @param key
	 * @param list
	 */
	public static <T extends IForgeRegistryEntry<T>> void writeListToNBT(NBTTagCompound compound, String key, List<T> set) {
		compound.setTag(key, getElementsTag(set));
	}

	/**
	 * レジストリの要素をcompoundから取り出す
	 * @param compound
	 * @param key
	 * @param type
	 * @return element
	 */
	public static <T extends IForgeRegistryEntry<T>> T readFromNBT(NBTTagCompound compound, String key, Class<T> type) {
		ResourceLocation location = new ResourceLocation(compound.getString(key));
		return getElement(type, location);
	}

	/**
	 * レジストリの要素をcompoundから取り出す
	 * @param compound
	 * @param key
	 * @param type
	 * @return elements
	 */
	public static <T extends IForgeRegistryEntry<T>> List<T> readListFromNBT(NBTTagCompound compound, String key, Class<T> type) {
		NBTTagList list = compound.getTagList(key, Constants.NBT.TAG_STRING);
		return getElements(type, list);
	}


	/**
	 * NBTTagListからレジストリの要素を保持したセットを取得する
	 * @param type
	 * @param tagList
	 * @return arrayList
	 */
	public static <T extends IForgeRegistryEntry<T>> List<T> getElements(Class<T> type, NBTTagList tagList){
		List<T> list = new ArrayList<T>();
		tagList.forEach((NBTBase tag) -> {
			T element = getElement(type, new ResourceLocation(NBTTagString.class.cast(tag).getString()));
			if (element != null) {
				list.add(element);
			}
		});
		return list;
	}

	/**
	 * ResourceLocationからレジストリの要素を取得する
	 * @param type
	 * @param location
	 * @return element
	 */
	@Nullable
	public static <T extends IForgeRegistryEntry<T>> T getElement(Class<T> type, ResourceLocation location) {
		return GameRegistry.findRegistry(type).getValue(location);
	}

	/**
	 * レジストリの要素のリストからNBTTagListを作成する
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T extends IForgeRegistryEntry<T>> NBTTagList getElementsTag(List<T> list) {
		NBTTagList tagList = new NBTTagList();
		list.forEach(element -> {
			tagList.appendTag(getElementTag(element));
		});
		return tagList;
	}

	/**
	 * レジストリの要素からNBTTagStringを作成する
	 * @param <T>
	 * @param element
	 * @return
	 */
	public static <T extends IForgeRegistryEntry<T>> NBTTagString getElementTag(@Nonnull T element) {
		return new NBTTagString(element.getRegistryName().toString());
	}

}
