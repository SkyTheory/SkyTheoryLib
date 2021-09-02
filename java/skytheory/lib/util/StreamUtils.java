package skytheory.lib.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class StreamUtils {

	/**
	 * stream.filterの中にこれを渡すことで、ItemStackのdistinctを行う
	 * @return
	 */
	public static Predicate<ItemStack> distinctItems() {
		return distinctBy(isSameItem);
	}

	/**
	 * stream.filterの中にこれを渡すことで、FluidStackのdistinctを行う
	 * @return
	 */
	public static Predicate<FluidStack> distinctFluids() {
		return distinctBy(isSameFluid);
	}

	// 覚書：二者の引数が同じであるかを評価するBiPredicateを引数に渡すこと
	public static <T> Predicate<T> distinctBy(BiPredicate<T, T> condition) {
		Set<T> set = new HashSet<>();
		return (obj -> {
			if (set.stream().anyMatch(item -> condition.test(obj, item))) {
				return false;
			}
			set.add(obj);
			return true;
		});
	}

	private static BiPredicate<ItemStack, ItemStack> isSameItem = ((stack1, stack2) -> {
		return ItemStack.areItemsEqual(stack1, stack2);
	});

	private static BiPredicate<FluidStack, FluidStack> isSameFluid = ((fluid1, fluid2) -> {
		if (fluid1 == null) return fluid2 == null;
		return fluid1.isFluidEqual(fluid2);
	});

}
