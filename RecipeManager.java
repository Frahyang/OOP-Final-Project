import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    // Instance variable
    private List<Recipe> recipes;

    // Constructor
    public RecipeManager() {
        this.recipes = new ArrayList<>();
    }

    // Getter
    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    // Methods to give changes to recipes
    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }
    public void removeRecipe(String recipeName) {
        recipes.removeIf(recipe -> recipe.getName().equals(recipeName));
    }
    public void modifyRecipe(String recipeName, Recipe updatedRecipe) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getName().equals(recipeName)) {
                recipes.set(i, updatedRecipe);
                break;
            }
        }
    }

    // Method to separate the ingredient name and quantity
    public List<Ingredient> parseIngredients(String ingredients) {
        String[] ingredientPairs = ingredients.split("\n");
        List<Ingredient> ingredientList = new ArrayList<>();
        for (String ingredientPair : ingredientPairs) {
            String[] parts = ingredientPair.split(":");
            if (parts.length == 2) { // Check if the length of the string is 2
                String name = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].trim());
                ingredientList.add(new Ingredient(name, quantity));
            }
        }
        return ingredientList;
    }

    // Method to store the recipe contents in a file
    public void saveRecipesToFile(String filePath) {
        /*
         * FileOutputStream opens a file for writing and creates a new one if file doesn't exist
         * ObjectOutputStream allows writing java objects
         */
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(recipes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load the contents of the file 
    @SuppressWarnings("unchecked")
    public void loadRecipesFromFile(String filePath) {
        /*
         * FileInputStream creates a file input stream to read from the specified file
         * ObjectInputStream wraps the file which allows it to read java objects from the file
         */
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            recipes = (List<Recipe>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}