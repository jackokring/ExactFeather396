package uk.co.kring.ef396;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import uk.co.kring.ef396.items.BedtimeBook;

@JeiPlugin
public class JEIManager implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExactFeather.MOD_ID, "jei");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        BedtimeBook.getCache().stream().forEach((sup) ->
                registration.useNbtForSubtypes(sup.get().getItem()) //applies items in books
                // just in case old load has different NBT
        );
        IModPlugin.super.registerItemSubtypes(registration);
    }

    @Override
    public void registerFluidSubtypes(ISubtypeRegistration registration) {
        IModPlugin.super.registerFluidSubtypes(registration);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        IModPlugin.super.registerIngredients(registration);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IModPlugin.super.registerVanillaCategoryExtensions(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IModPlugin.super.registerRecipeTransferHandlers(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        IModPlugin.super.registerAdvanced(registration);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IModPlugin.super.onRuntimeAvailable(jeiRuntime);
    }
}
