public interface CookBookConstants {

    // Constants for application window
    String APPLICATION_NAME = "e-Cooking+";
    int FRAME_WIDTH = 1280;
    int FRAME_HEIGHT = 720;

    // Constants to save the user's input
    String RECIPE_FILE = "recipes.dat";
    String FRIDGE_FILE = "fridge.dat";
    String SHELF_FILE = "shelf.dat";

    // Constant to set panel width
    int PANEL_WIDTH = 350;

    // Constants for sound effects
    String COOK_SFX = "SoundEffects/Cook.wav";
    String RECIPE_SFX = "SoundEffects/Recipe.wav";
    String FRIDGE_SFX = "SoundEffects/Fridge.wav";
    String SHELF_SFX = "SoundEffects/Shelf.wav";

    // Constant for error handling
    String ERROR_WINDOW_TITLE = "ERROR";

    // Constants for recipe panel
    String ADD_RECIPE_TEXT = "Add Recipe";
    String REMOVE_RECIPE_TEXT = "Remove Recipe";
    String MODIFY_RECIPE_TEXT = "Modify Recipe";
    String RECIPE_PANEL_NAME = "                                                   Recipes";

    // Constants for main page panel
    String COOK_BUTTON_TEXT = "Cook";
    String MAIN_PANEL_NAME = "                                                                                  Main Page";

    // Constants for storage panel
    String ADD_INGREDIENT_TEXT = "Add";
    String REMOVE_INGREDIENT_TEXT = "Remove";
    String PLUS_SIGN = "+";
    String MINUS_SIGN = "-";
    String FRIDGE_PANEL_NAME = "                                                     Fridge";
    String SHELF_PANEL_NAME = "                                                     Shelf";
    int HALF_OF_PANEL = 340; // To split the panels equally

    // Constants used to separate fridge and shelf (Storage separator)
    boolean toFridge = true;
    boolean fromFridge = true;
    boolean toShelf = false;
    boolean fromShelf = false;

    // Constants for displaying recipe details
    String INGREDIENTS_TEXT = " Ingredients:\n";
    String INSTRUCTIONS_TEXT = "\n Instructions:\n";
    String BULLET_POINT = " - ";
    int MAIN_FONT_SIZE = 24;

    // Constants for cook button
    String COOKING_SUCCESSFUL = "Recipe cooked successfully!";
    String COOKING_FAILED = "Some ingredients are missing to cook this recipe.";
    String COOK_ERROR_MESSAGE = "Please select a recipe to cook.";
    String COOK_WINDOW_TITLE = "Cook";

    // Constants for addRecipe, removeRecipe, and modifyRecipe method
    int TEXT_AREA_ROWS = 5; // For the text area's 'width'
    int TEXT_AREA_COLUMNS = 20; // For the text area's 'height'
    String ADD_RECIPE_WINDOW_TITLE = "Add Recipe";
    String REMOVE_ERROR_MESSAGE = "Please select a recipe to remove.";
    String MODIFY_ERROR_MESSAGE = "Please select a recipe to modify.";
    String MODIFY_RECIPE_WINDOW_TITLE = "Modify Recipe";
    String FORMAT_ERROR_MESSAGE = "Invalid ingredient format. Please enter ingredients in the format 'name:quantity' per line and ensure it is a positive value.";
    String DUPLICATE_ERROR_MESSAGE = "Duplicate ingredients listed. Please enter unique ingredients.";

    // Constants for addIngredient, removeIngredient, setQuantityForIngredient
    String ADD_INGREDIENT_WINDOW_TITLE = "Add Ingredient";
    String FRIDGE_ERROR_MESSAGE = "Please select an ingredient from the fridge.";
    String SHELF_ERROR_MESSAGE = "Please select an ingredient from the shelf.";
    String SET_QUANTITY_TEXT = "Enter the quantity (1-100):";
    String SET_QUANTITY_WINDOW_TITLE = "Set Quantity";
    String QUANTITY_RANGE_ERROR_MESSAGE = "Invalid quantity. Please enter a number between 1 and 100.";
    String INTEGER_CHECK_ERROR_MESSAGE = "Invalid quantity. Please enter an Integer.";
    String TYPE_CHECK_ERROR_MESSAGE = "Invalid input. Please enter a number.";
    String EMPTY_ERROR_MESSAGE = "Ingredient name cannot be empty.";
    String EXISTING_INGREDIENT_ERROR_MESSAGE = "Ingredient already exists.";
    String NEGATIVE_ERROR_MESSAGE = "Ingredient quantity cannot be a negative value";

    // Constants for highlighting ingredients
    Boolean available = true;
    Boolean notAvailable = false;
    Boolean highlight = true;
    Boolean removeHighlight = false;

    // Constants for cell rendering
    int LEFT_MARGIN = 10;
    int RIGHT_MARGIN = 0;
    int TOP_MARGIN = 0;
    int BOTTOM_MARGIN = 0;
    int CELL_WIDTH = 330;
    int CELL_HEIGHT = 30;
    float PANE_FONT_SIZE = 14f;
}
