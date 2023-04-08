package skytheory.lib.recipe;

import java.util.Optional;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface ProcessingRecipe<T, R> extends Recipe<Container> {


	@Override
	default boolean matches(Container pContainer, Level pLevel) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ItemStack getResultItem(RegistryAccess access) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		throw new UnsupportedOperationException();
	}

	boolean matches(T ingredients);

	R getResult(T ingredients);

	public static <T, U extends RecipeType<ProcessingRecipe<T, R>>, R> Optional<ProcessingRecipe<T, R>> getRecipe(Level level, U type, T ingredients) {
		return level.getRecipeManager().getAllRecipesFor(type).stream()
				.filter(t -> t.matches(ingredients))
				.findFirst();
	}

}
