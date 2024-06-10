import java.io.Serializable;

import java.util.List;

public class Recipe implements Serializable {
    private String name;
    private List<Ingredient> ingredients;
    private List<String> instructions;

    // Constructor
    public Recipe(String name, List<Ingredient> ingredients, List<String> instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    // Getters
    public String getName() {
        return name;
    }
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    public List<String> getInstructions() {
        return instructions;
    }

    // Method to convert into string
    public String toString() {
        return name;
    }

    // Method to convert recipe ingredient details into string
    public String ingredientsToString(List<Ingredient> ingredients) {
        StringBuilder ingredientText = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            if (ingredientText.length() > 0) {
                ingredientText.append("\n"); // To separate the ingredients in the ingredients text part when modifying the recipe
            }
            ingredientText.append(ingredient.getName() + ":" + ingredient.getQuantity() + ":" + ingredient.getUnit());
        }
        return ingredientText.toString();
    }
}