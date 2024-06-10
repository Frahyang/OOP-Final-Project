import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import java.io.File;

public class CookBookGUI implements CookBookConstants {
    // Instance variables

    // Instance to make the window for the java application
    private JFrame frame;

    // Instances for handling recipes
    private RecipeManager recipeManager;
    private JList<Recipe> recipeList;
    private JTextArea recipeDetailsArea;

    // Instances for handling the fridge
    private Storage fridge;
    private JList<Ingredient> fridgeList;
    private DefaultListModel<Ingredient> fridgeModel;

    // Instances for handling the shelf
    private Storage shelf;
    private JList<Ingredient> shelfList;
    private DefaultListModel<Ingredient> shelfModel;

    // These are the default values to add and decrement the ingredients from fridge and shelf
    private int fridgeIngredientQuantity = 1;
    private int shelfIngredientQuantity = 1;

    // Used to update the displayed set quantity
    private JButton setFridgeQuantityButton;
    private JButton setShelfQuantityButton;
    
    // Constructor for the current class
    public CookBookGUI() {
        frame = new JFrame(APPLICATION_NAME);
        recipeManager = new RecipeManager();
        fridge = new Storage();
        shelf = new Storage();

        // Sets the attributes for the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Sets the location of each respective panels
        frame.setLayout(new BorderLayout());
        frame.add(createRecipePanel(), BorderLayout.WEST); // The recipe panel set the left side of the app
        frame.add(createCentralPanel(), BorderLayout.EAST); // The fridge and shelf panel to the right
        frame.add(createRecipeDetailPanel(), BorderLayout.CENTER); // The recipe details at the center

        // Object instances for files for saving purposes
        File recipeFile = new File(RECIPE_FILE); // Stores recipes
        File fridgeFile = new File(FRIDGE_FILE); // Stores ingredients placed in the fridge
        File shelfFile = new File(SHELF_FILE); // Stores ingredients placed on the shelf

        // Load necessary data when application opens
        if (recipeFile.exists()) {
            recipeManager.loadRecipesFromFile(RECIPE_FILE);
        }
        if (fridgeFile.exists()) {
            fridge.loadContentsFromFile(FRIDGE_FILE);
        }
        if (shelfFile.exists()) {
            shelf.loadContentsFromFile(SHELF_FILE);
        }

        // Ensures that whenever the app is loaded, all ingredient foreground colour is reset to black
        resetIngredientStates();

        // Updating methods to make changes to the screen everytime the user does something
        updateRecipeList();
        updateFridgeList();
        updateShelfList();
    }

    // Method to create the recipe panel
    private JPanel createRecipePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setPreferredSize(new Dimension(PANEL_WIDTH, frame.getHeight()));

        // Sets a label for the recipe panel and places it at the top
        JLabel titleLabel = new JLabel(RECIPE_PANEL_NAME);
        panel.add(titleLabel, BorderLayout.NORTH);

        recipeList = new JList<>(new DefaultListModel<>());

        // Sets a custom cell renderer for the recipes to enlarge and give left margins to each cell
        recipeList.setCellRenderer(new RecipeListCellRenderer());

        // Adds listeners to detect mouse clicks when cell is selected
        recipeList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Recipe selectedRecipe = recipeList.getSelectedValue();
                    if (selectedRecipe != null) {
                        displayRecipeDetails(selectedRecipe);
                    }
                }
            }
        });

        // Creates new buttons to the panel
        JButton addRecipeButton = new JButton(ADD_RECIPE_TEXT);
        JButton removeRecipeButton = new JButton(REMOVE_RECIPE_TEXT);
        JButton modifyRecipeButton = new JButton(MODIFY_RECIPE_TEXT);

        // Buttons added are placed in one panel and added and placed at the bottom of the recipe panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addRecipeButton);
        buttonsPanel.add(removeRecipeButton);
        buttonsPanel.add(modifyRecipeButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        // Allows vertical and horizontal scroll in case recipes go over the area of the panel
        panel.add(new JScrollPane(recipeList), BorderLayout.CENTER);

        // Action listeners added to each button to detect mouse clicks
        addRecipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent addingRecipe) {
                addRecipe();
            }
        });
        removeRecipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent removingRecipe) {
                removeRecipe();
            }
        });
        modifyRecipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent modifyingRecipe) {
                modifyRecipe();
            }
        });
        return panel;
    }

    // Method to create the panel to display details of a recipe
    private JPanel createRecipeDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Sets a label for the recipe details panel and places it at the top relative to the position of the panel
        JLabel titleLabel = new JLabel(MAIN_PANEL_NAME);
        panel.add(titleLabel, BorderLayout.NORTH);

        recipeDetailsArea = new JTextArea();
        recipeDetailsArea.setEditable(false);

        // Creates a new button to the panel
        JButton cookButton = new JButton(COOK_BUTTON_TEXT);

        // Since theres only 1 button, it is directly placed without putting it in a JPanel
        panel.add(cookButton, BorderLayout.SOUTH);

        panel.add(new JScrollPane(recipeDetailsArea), BorderLayout.CENTER);

        // Allows vertical and horizontal scroll in case details go over the area of the panel 
        cookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent cook) {
                Recipe selectedRecipe = recipeList.getSelectedValue();
                if (selectedRecipe != null) { // If the user already selected a recipe execute the cook method
                    cookRecipe(selectedRecipe);
                } else { // Else display a message
                    JOptionPane.showMessageDialog(frame, COOK_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        return panel;
    }

    // Method to create the fridge panel alone
    private JPanel createFridgePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Sets a label for the fridge panel and places it at the top relative to the position of the fridge panel
        JLabel titleLabel = new JLabel(FRIDGE_PANEL_NAME);
        panel.add(titleLabel, BorderLayout.NORTH);

        fridgeModel = new DefaultListModel<>();
        fridgeList = new JList<>(fridgeModel);

        // Sets a custom cell renderer for the fridge to enlarge and give left margins to each cell
        fridgeList.setCellRenderer(new IngredientListCellRenderer());
    
        // Creates new buttons to the panel
        JButton addIngredientButton = new JButton(ADD_INGREDIENT_TEXT);
        JButton removeIngredientButton = new JButton(REMOVE_INGREDIENT_TEXT);
        setFridgeQuantityButton = new JButton("Quantity: " + fridgeIngredientQuantity);
        JButton addQuantityButton = new JButton(PLUS_SIGN);
        JButton subtractQuantityButton = new JButton(MINUS_SIGN);

        // Buttons added are placed and grouped in one panel, that panel is added and placed at the bottom of the fridge panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addIngredientButton);
        buttonsPanel.add(removeIngredientButton);
        buttonsPanel.add(setFridgeQuantityButton);
        buttonsPanel.add(addQuantityButton); // Button responsible to increase the quantity of an ingredient
        buttonsPanel.add(subtractQuantityButton); // This one decreases the quantity of an ingredient
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.repaint();
        
        // Allows vertical and horizontal scroll in case ingredients go over the area of the panel
        panel.add(new JScrollPane(fridgeList), BorderLayout.CENTER);

        // Action listeners added to each button to detect mouse clicks
        addIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent addingIngredient) {
                addIngredient(toFridge); // The toFridge and fromFridge boolean values indicates that it is for the fridge
            }
        });
        removeIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent removingIngredient) {
                removeIngredient(fromFridge);
            }
        });
        setFridgeQuantityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent setting) {
                setQuantityForIngredient(toFridge);
            }
        });
        addQuantityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent adding) {
                Ingredient selectedIngredient = fridgeList.getSelectedValue();
                if (selectedIngredient != null) {
                    addQuantityToIngredient(selectedIngredient, toFridge, fridgeIngredientQuantity); // selectedIngridient here checks the fridgeList only
                } else {
                    JOptionPane.showMessageDialog(frame, FRIDGE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        subtractQuantityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent subtracting) {
                Ingredient selectedIngredient = fridgeList.getSelectedValue();
                if (selectedIngredient != null) {
                    subtractQuantityFromIngredient(selectedIngredient, fromFridge, fridgeIngredientQuantity);
                } else {
                    JOptionPane.showMessageDialog(frame, FRIDGE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        return panel;
    }

    // Method to create the shelf panel alone
    private JPanel createShelfPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Sets a label for the shelf panel and places it at the top relative to the position of the shelf panel
        JLabel titleLabel = new JLabel(SHELF_PANEL_NAME);
        panel.add(titleLabel, BorderLayout.NORTH);


        shelfModel = new DefaultListModel<>();
        shelfList = new JList<>(shelfModel);

        // // Sets a custom cell renderer for the shelf to enlarge and give left margins to each cell, uses the same one as fridge
        shelfList.setCellRenderer(new IngredientListCellRenderer());

        // Creates new buttons to the panel
        JButton addIngredientButton = new JButton(ADD_INGREDIENT_TEXT);
        JButton removeIngredientButton = new JButton(REMOVE_INGREDIENT_TEXT);
        setShelfQuantityButton = new JButton("Quantity: " + shelfIngredientQuantity);
        JButton addQuantityButton = new JButton(PLUS_SIGN);
        JButton subtractQuantityButton = new JButton(MINUS_SIGN);

        // Buttons added are placed and grouped in one panel, that panel is added and placed at the bottom of the shelf panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addIngredientButton);
        buttonsPanel.add(removeIngredientButton);
        buttonsPanel.add(setShelfQuantityButton);
        buttonsPanel.add(addQuantityButton);
        buttonsPanel.add(subtractQuantityButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        // Allows vertical and horizontal scroll in case ingredients go over the area of the panel
        panel.add(new JScrollPane(shelfList), BorderLayout.CENTER);

        // Action listeners added to each button to detect mouse clicks
        addIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent addingIngredient) {
                addIngredient(toShelf); // The boolean values of toShelf and fromShelf are false hence, it is for the shelf
            }
        });
        removeIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent removingIngredient) {
                removeIngredient(fromShelf);
            }
        });
        setShelfQuantityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent setting) {
                setQuantityForIngredient(toShelf);
            }
        });
        addQuantityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent adding) {
                Ingredient selectedIngredient = shelfList.getSelectedValue();
                if (selectedIngredient != null) {
                    addQuantityToIngredient(selectedIngredient, toShelf, shelfIngredientQuantity); // selectedIngredient here checks the shelfList only
                } else {
                    JOptionPane.showMessageDialog(frame, SHELF_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        subtractQuantityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent subtracting) {
            Ingredient selectedIngredient = shelfList.getSelectedValue();
                if (selectedIngredient != null) {
                    subtractQuantityFromIngredient(selectedIngredient, fromShelf, shelfIngredientQuantity);
                } else {
                    JOptionPane.showMessageDialog(frame, SHELF_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        return panel;
    }

    // Method to split the fridge and shelf panel
    private JSplitPane createCentralPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createFridgePanel(), createShelfPanel());
        splitPane.setPreferredSize(new Dimension(PANEL_WIDTH, frame.getHeight()));
        splitPane.setTopComponent(createFridgePanel()); // Fridge will be at the top of the panel
        splitPane.setBottomComponent(createShelfPanel()); // Shelf will be below it
        splitPane.setDividerLocation(HALF_OF_PANEL);
        splitPane.setBorder(null); // Removes the border created from the split
        return splitPane;
    }

    // Method to display the texts of the details of a selected recipe
    private void displayRecipeDetails(Recipe recipe) {
        String RECIPE_NAME = "\t\t" + recipe.getName() + "\n\n";

        // String builder to store the information temporarily
        StringBuilder recipeDetails = new StringBuilder();

        recipeDetails.append(RECIPE_NAME);

        // Iterates all the ingredients from a selected recipe and then appends it into the string builder
        recipeDetails.append(INGREDIENTS_TEXT);
        for (Ingredient ingredient : recipe.getIngredients()) {
            recipeDetails.append(BULLET_POINT).append(ingredient.getName() + ": " + ingredient.getQuantity() + "\n");
        }

        // Iterates all the instructions from a selected recipe and then appends it into the string builder
        recipeDetails.append(INSTRUCTIONS_TEXT);
        for (String instruction : recipe.getInstructions()) {
            recipeDetails.append(BULLET_POINT).append(instruction + "\n");
        }

        // Setting the styling and size of the font
        Font textFont = new Font(Font.MONOSPACED, Font.BOLD, MAIN_FONT_SIZE);
        recipeDetailsArea.setFont(textFont);

        // Convert the stringbuilder to string and set it as the text to be displayed on the main page
        recipeDetailsArea.setText(recipeDetails.toString());

        // Update colours for ingredient panels
        highlightIngredients(recipe);
    }

    // Method to cook recipe
    private void cookRecipe(Recipe recipe) {
        List<Ingredient> requiredIngredients = recipe.getIngredients(); // Gets the ingredients from the recipe details
        List<Ingredient> combinedContents = new ArrayList<>(); // Gets the ingredients from the ingredient panels
        
        // Combine ingredients from fridge and shelf
        combinedContents.addAll(fridge.getContents());
        combinedContents.addAll(shelf.getContents());
    
        boolean hasAllIngredients = true;
    
        // Check if all required ingredients are available in the combined contents
        for (Ingredient requiredIngredient : requiredIngredients) {
            Ingredient combinedIngredient = findIngredientInCombinedContents(combinedContents, requiredIngredient.getName());
            if (combinedIngredient == null || combinedIngredient.getQuantity() < requiredIngredient.getQuantity()) {
                hasAllIngredients = false;
                break;
            }
        }

        if (hasAllIngredients) {
            playSound(COOK_SFX);
            // If all required ingredients are available, decrease their quantities from their respective panels
            for (Ingredient requiredIngredient : requiredIngredients) {

                // Gets the ingredient quantity from the recipe
                int quantityToDeduct = requiredIngredient.getQuantity();

                // Used to check each panel one by one
                Ingredient fridgeIngredient = fridge.findIngredient(requiredIngredient.getName());
                Ingredient shelfIngredient = shelf.findIngredient(requiredIngredient.getName());
                
                // Decrease from the fridge first
                if (fridgeIngredient != null) {
                    fridgeIngredient.setQuantity(fridgeIngredient.getQuantity() - quantityToDeduct);
                    continue;
                }
                
                // Then decrease from the shelf if needed
                if (shelfIngredient != null) {
                    shelfIngredient.setQuantity(shelfIngredient.getQuantity() - quantityToDeduct);
                    continue;
                }
            }
            
            // Update colours for ingredient panels
            highlightIngredients(recipe);

            // Save the updated information
            fridge.saveContentsToFile(FRIDGE_FILE);
            shelf.saveContentsToFile(SHELF_FILE);
    
            // Inform the user that the recipe has been cooked successfully
            JOptionPane.showMessageDialog(frame, COOKING_SUCCESSFUL, COOK_WINDOW_TITLE,JOptionPane.PLAIN_MESSAGE);
        } else {

            // Update colours for ingredient panels
            highlightIngredients(recipe);

            // Inform the user that some ingredients are missing
            JOptionPane.showMessageDialog(frame, COOKING_FAILED, COOK_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
        }

    }

    // Method to add a new recipe
    private void addRecipe() {

        // Used to save the user's previous input
        String recipeNameInput = "";
        String ingredientsInput = "";
        String instructionsInput = "";

        while (true) {
            JTextField recipeNameField = new JTextField(recipeNameInput);
            JTextArea ingredientsField = new JTextArea(ingredientsInput, TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);
            JTextArea instructionsArea = new JTextArea(instructionsInput, TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);

            Object[] message = {
                "Recipe Name:", recipeNameField,
                "Ingredients (name:quantity per line):", new JScrollPane(ingredientsField),
                "Instructions (line-separated):", new JScrollPane(instructionsArea)
            };

            // Displays the input in a window
            int option = JOptionPane.showConfirmDialog(frame, message, ADD_RECIPE_WINDOW_TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // When the user clicks OK
            if (option == JOptionPane.OK_OPTION) {
                String recipeName = recipeNameField.getText();
                String ingredients = ingredientsField.getText();
                String instructions = instructionsArea.getText();

                // Save the current input to reuse it in case of error
                recipeNameInput = recipeName;
                ingredientsInput = ingredients;
                instructionsInput = instructions;

                // Check format
                if (!validIngredientFormat(ingredients)) {
                    JOptionPane.showMessageDialog(frame, FORMAT_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }

                // Check if the recipe name already exists
                if (recipeManager.isDuplicateRecipe(recipeName)) {
                    JOptionPane.showMessageDialog(frame, DUPLICATE_RECIPE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }

                List<Ingredient> ingredientList = recipeManager.parseIngredients(ingredients);
                if (containsDuplicateIngredients(ingredientList)) {
                    JOptionPane.showMessageDialog(frame, DUPLICATE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                } else {
                    playSound(RECIPE_SFX);
                    List<String> instructionsList = Arrays.asList(instructions.split("\\n"));

                    Recipe newRecipe = new Recipe(recipeName, ingredientList, instructionsList);
                    recipeManager.addRecipe(newRecipe);

                    DefaultListModel<Recipe> recipeModel = (DefaultListModel<Recipe>) recipeList.getModel();
                    recipeModel.addElement(newRecipe);

                    recipeManager.saveRecipesToFile(RECIPE_FILE);
                    break;
                }
            } else {
                break;
            }
        }
    }

    // Method to remove a selected recipe
    private void removeRecipe() {
        Recipe selectedRecipe = recipeList.getSelectedValue();
        if (selectedRecipe != null) {
            playSound(RECIPE_SFX);
            recipeManager.removeRecipe(selectedRecipe.getName());

            DefaultListModel<Recipe> model = (DefaultListModel<Recipe>) recipeList.getModel();
            model.removeElement(selectedRecipe);

            recipeDetailsArea.setText(""); // Removes what was currently displayed in the main page

            // Save recipes to file
            recipeManager.saveRecipesToFile(RECIPE_FILE);
        } else {
            JOptionPane.showMessageDialog(frame, REMOVE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
        }
    }

    // Method to modify a selected recipe
    private void modifyRecipe() {
        Recipe selectedRecipe = recipeList.getSelectedValue();

        String recipeNameInput = "";
        String ingredientsInput = "";
        String instructionsInput = "";
        Boolean recipeIsSelected;

        // Used to save the user's previous input
        if (selectedRecipe != null) {
            recipeNameInput = selectedRecipe.getName();
            ingredientsInput = selectedRecipe.ingredientsToString(selectedRecipe.getIngredients()); // Converts the ingredients of the recipe to string
            instructionsInput = String.join("\n", selectedRecipe.getInstructions());
            recipeIsSelected = true;
        } else {
            JOptionPane.showMessageDialog(frame, MODIFY_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
            recipeIsSelected = false;
        }

        while (recipeIsSelected) {

            JTextField recipeNameField = new JTextField(recipeNameInput);
            JTextArea ingredientsField = new JTextArea(ingredientsInput, TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);
            JTextArea instructionsArea = new JTextArea(instructionsInput, TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);

            Object[] message = {
                "Recipe Name:", recipeNameField,
                "Ingredients (name:quantity per line):", new JScrollPane(ingredientsField),
                "Instructions (line-separated):", new JScrollPane(instructionsArea)
            };

            int option = JOptionPane.showConfirmDialog(frame, message, MODIFY_RECIPE_WINDOW_TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                String recipeName = recipeNameField.getText();
                String ingredients = ingredientsField.getText();
                String instructions = instructionsArea.getText();

                // Save the current input to reuse it in case of error
                recipeNameInput = recipeName;
                ingredientsInput = ingredients;
                instructionsInput = instructions;

                // Check format
                if (!validIngredientFormat(ingredients)) {
                    JOptionPane.showMessageDialog(frame, FORMAT_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }

                // Check if the recipe name already exists
                if (recipeManager.isDuplicateRecipe(recipeName)) {
                    JOptionPane.showMessageDialog(frame, DUPLICATE_RECIPE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }

                List<Ingredient> ingredientList = recipeManager.parseIngredients(ingredients);
                if (containsDuplicateIngredients(ingredientList)) {
                    JOptionPane.showMessageDialog(frame, DUPLICATE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                } else {
                    playSound(RECIPE_SFX);
                    List<String> instructionsList = Arrays.asList(instructions.split("\\n"));

                    Recipe updatedRecipe = new Recipe(recipeName, ingredientList, instructionsList);
                    recipeManager.modifyRecipe(selectedRecipe.getName(), updatedRecipe);

                    DefaultListModel<Recipe> model = (DefaultListModel<Recipe>) recipeList.getModel();
                    int index = model.indexOf(selectedRecipe);
                    model.set(index, updatedRecipe);
                    updateRecipeDetails();

                    // Save recipes to file
                    recipeManager.saveRecipesToFile(RECIPE_FILE);
                    break;
                }
            } else {
                break;
            }
            
        }
    }
    

    private boolean validIngredientFormat(String ingredients) {
        String[] ingredientArray = ingredients.split("\n");
        for (String ingredient : ingredientArray) {
            if (!ingredient.matches("\\s*[\\w\\s]+\\s*:\\s*\\d+\\s*")) {
                return false;
            }
        }
        return true;
    }

    // Method to check for duplicate ingredients
    private boolean containsDuplicateIngredients(List<Ingredient> ingredientList) {
        Set<String> ingredientNames = new HashSet<>();
        for (Ingredient ingredient : ingredientList) {
            if (!ingredientNames.add(ingredient.getName().toLowerCase())) {
                return true; // Duplicate found
            }
        }
        return false; // No duplicates
    }

    // Method to add a completely new ingredients
    private void addIngredient(boolean toFridgePanel) {

        // Used to save the user's previous input
        String ingredientNameInput = "";
        String quantityInput = "";

        // Used to receive user input
        while (true) {
            JTextField ingredientNameField = new JTextField(ingredientNameInput);
            JTextField quantityField = new JTextField(quantityInput);

            Object[] message = {
                "Ingredient Name:", ingredientNameField,
                "Quantity:", quantityField
            };

            // Displays the input in a window
            int option = JOptionPane.showConfirmDialog(frame, message, ADD_INGREDIENT_WINDOW_TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // When the user clicks OK
            if (option == JOptionPane.OK_OPTION) {
                String ingredientName = ingredientNameField.getText();
                int quantity;

                // Save the current input to reuse it in case of error
                ingredientNameInput = ingredientName;
                quantityInput = quantityField.getText().trim();

                // Checks if the ingredient text section isn't empty
                if (ingredientName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, EMPTY_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }

                // Check if the ingredient already exists in the fridge or shelf
                if (isDuplicateIngredient(ingredientName)) {
                    JOptionPane.showMessageDialog(frame, EXISTING_INGREDIENT_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }

                // Checks if the quantity is an integer
                try {
                    quantity = Integer.parseInt(quantityInput);
                } catch (NumberFormatException onlyIntegersAllowed) {
                    JOptionPane.showMessageDialog(frame, INTEGER_CHECK_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }
                
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(frame, NEGATIVE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                    continue;
                }
                
                Ingredient newIngredient = new Ingredient(ingredientName, quantity);

                // It checks if the added ingredient is to the fridge or the shelf and then it adds
                if (toFridgePanel) {
                    playSound(FRIDGE_SFX);
                    fridge.adding(newIngredient);
                    fridgeModel.addElement(newIngredient);
                    fridge.saveContentsToFile(FRIDGE_FILE);
                    break;
                } else {
                    playSound(SHELF_SFX);
                    shelf.adding(newIngredient);
                    shelfModel.addElement(newIngredient);
                    shelf.saveContentsToFile(SHELF_FILE);
                    break;
                }
            } else {
                break;
            }
        }
    }

    // Method to check if an ingredient with the same name already exists in the fridge or shelf
    private boolean isDuplicateIngredient(String ingredientName) {

        // Combine contents of fridge and shelf
        List<Ingredient> combinedContents = new ArrayList<>();
        combinedContents.addAll(fridge.getContents());
        combinedContents.addAll(shelf.getContents());

        // Check combined contents for duplicate ingredient
        for (Ingredient ingredient : combinedContents) {
            if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
                return true;
            }
        }
        return false;
    }

    // Method to remove a selected ingredient
    private void removeIngredient(boolean fromFridgePanel) {

        // First checks if its from fridge or shelf
        if (fromFridgePanel) { // For fridge
            Ingredient selectedIngredient = fridgeList.getSelectedValue(); // selectedIngredient only for the fridge

            // Then checks if the user actually have selected an ingredient
            if (selectedIngredient != null) {
                playSound(FRIDGE_SFX);
                // Then removes the ingredient from the fridge and shelf objects and removes the cell
                fridge.removing(selectedIngredient);
                DefaultListModel<Ingredient> fridgeModel = (DefaultListModel<Ingredient>) fridgeList.getModel();
                fridgeModel.removeElement(selectedIngredient);
                fridge.saveContentsToFile(FRIDGE_FILE);
            } else {
                JOptionPane.showMessageDialog(frame, FRIDGE_ERROR_MESSAGE);
            }
        } else { // For shelf
            Ingredient selectedIngredient = shelfList.getSelectedValue(); // selectedIngredient only for the shelf

            if (selectedIngredient != null) {
                playSound(SHELF_SFX);
                shelf.removing(selectedIngredient);
                DefaultListModel<Ingredient> shelfModel = (DefaultListModel<Ingredient>) shelfList.getModel();
                shelfModel.removeElement(selectedIngredient);
                shelf.saveContentsToFile(SHELF_FILE);
            } else {
                JOptionPane.showMessageDialog(frame, SHELF_ERROR_MESSAGE);
            }
        }
    }

    // Method to set the quantity to be used to make changes to the ingredient's quantity
    private void setQuantityForIngredient(boolean toFridgePanel) {
        while (true) {
            // Prompt the user to enter the quantity
            String quantityString = JOptionPane.showInputDialog(frame, SET_QUANTITY_TEXT, SET_QUANTITY_WINDOW_TITLE,JOptionPane.PLAIN_MESSAGE);

            // Check if the user clicked "Cancel"
            if (quantityString == null) {
                return; // Exit if clicked
            }
            if (!quantityString.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityString);
    
                    // Check if the quantity is within the allowed range
                    if (!(quantity > 0 && quantity <= 100)) {
                        JOptionPane.showMessageDialog(frame, QUANTITY_RANGE_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                        continue; // Prompt the user again
                    }
    
                    // Set the quantity for the appropriate ingredient
                    if (toFridgePanel) {
                        fridgeIngredientQuantity = quantity;
                        updateQuantityButtonText(toFridge);
                    } else {
                        shelfIngredientQuantity = quantity;
                        updateQuantityButtonText(toShelf);
                    }
                    break; // Exit the loop if a valid input is provided
    
                } catch (NumberFormatException onlyIntegersAllowed) {
                    JOptionPane.showMessageDialog(frame, INTEGER_CHECK_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, TYPE_CHECK_ERROR_MESSAGE, ERROR_WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    // Method to increase the quantity of a selected ingredient
    // Parameters: ingredient to accept any ingredient, toFridgePanel to decide the panel chosen (either shelf or fridge), customQuantity to determine how much to add per button click
    private void addQuantityToIngredient(Ingredient ingredient, Boolean toFridgePanel, int customQuantity) { 

        // Tracks the current quantity of the selected ingredient and then adds 1 to the quantity
        int currentQuantity = ingredient.getQuantity();
        ingredient.setQuantity(currentQuantity + customQuantity);

        // Then checks if the ingredient quantity to be added/decremented is in the fridge or not
        if (toFridgePanel) { // If it is then update only the fridge list and store it in the fridge file
            playSound(FRIDGE_SFX);
            fridgeList.repaint();
            fridge.saveContentsToFile(FRIDGE_FILE);
        } else { // Else
            playSound(SHELF_SFX);
            shelfList.repaint();
            shelf.saveContentsToFile(SHELF_FILE);
        }
    }
    
    // Method to decrease the quantity of a selected ingredient
    private void subtractQuantityFromIngredient(Ingredient ingredient, Boolean fromFridgePanel, int customQuantity) {

        // Tracks the current quantity of the selected ingredient
        int currentQuantity = ingredient.getQuantity();

        // Before decrementing, the quantity is checked first if it is more than 0, to prevent negative values
        if (currentQuantity > 0) {
            if (currentQuantity >= customQuantity) {

                // Subtract the ingredient's current quantity with the set quantity
                ingredient.setQuantity(currentQuantity - customQuantity);
            } else {

                // Subtract the ingredient's current quantity with itself
                ingredient.setQuantity(currentQuantity - currentQuantity);
            }

            if (fromFridgePanel) { // The same logic applies as with the adding quantity method (in deciding the panels)
                playSound(FRIDGE_SFX);
                fridgeList.repaint();
                fridge.saveContentsToFile(FRIDGE_FILE);
            } else {
                playSound(SHELF_SFX);
                shelfList.repaint();
                shelf.saveContentsToFile(SHELF_FILE);
            }
        }
    }

    // Method to iterate and find all ingredients from fridge and shelf
    private Ingredient findIngredientInCombinedContents(List<Ingredient> combinedContents, String name) {
        for (Ingredient ingredient : combinedContents) {
            if (ingredient.getName().equalsIgnoreCase(name)) {
                return ingredient;
            }
        }
        return null; // If the ingredient is not found
    }

    

    // Method to update recipe list
    private void updateRecipeList() {

        // First it clears the not updated recipe list
        DefaultListModel<Recipe> recipeModel = (DefaultListModel<Recipe>) recipeList.getModel();
        recipeModel.clear();

        // Then iterates through the recipes once again, but this time its the newest one
        for (Recipe recipe : recipeManager.getAllRecipes()) {
            recipeModel.addElement(recipe);
        }
        // Essentially whenever this method is executed the recipe list is completely removed and added again, but the newest one is updated
    }

    // Method to update recipe details
    private void updateRecipeDetails() {
        Recipe selectedRecipe = recipeList.getSelectedValue();
        if (selectedRecipe != null) {
            displayRecipeDetails(selectedRecipe);
        }
    }

    // Method to update fridge list
    private void updateFridgeList() {
        DefaultListModel<Ingredient> model = (DefaultListModel<Ingredient>) fridgeList.getModel();
        model.clear();
        for (Ingredient ingredient : fridge.getContents()) {
            model.addElement(ingredient);
        }
        fridgeList.repaint();
    }

    // Method to update shelf list
    private void updateShelfList() {
        DefaultListModel<Ingredient> shelfModel = (DefaultListModel<Ingredient>) shelfList.getModel();
        shelfModel.clear();
        for (Ingredient ingredient : shelf.getContents()) {
            shelfModel.addElement(ingredient);
        }
        shelfList.repaint();
    }

    // Method to update the text displayed on Quantity button
    private void updateQuantityButtonText(boolean toFridgePanel) {
        if (toFridgePanel) {
            setFridgeQuantityButton.setText("Quantity: " + fridgeIngredientQuantity);
        } else {
            setShelfQuantityButton.setText("Quantity: " + shelfIngredientQuantity);
        }
    }

    /* Method to 'filter' the needed ingredients
     * Whenever a recipe is selected it will change the colour of certain ingredients
     * Green when the ingredient is enough, red when it's not, and black if it's not needed
     * Highlight the whole selected cell gray when selected
     */
    private void highlightIngredients(Recipe recipe) {

        // A list containing the ingredients needed from the recipe is made to be matched with the ingredients from fridge and shelf
        List<Ingredient> requiredIngredients = recipe.getIngredients();
        List<Ingredient> combinedContents = new ArrayList<>(); // This list will be used to combine both the ingredients from fridge and shelf
        combinedContents.addAll(fridge.getContents());
        combinedContents.addAll(shelf.getContents());

        // Reset highlighting for all ingredients and reset its status to not available
        for (Ingredient ingredient : combinedContents) {
            ingredient.setCell(removeHighlight);
            ingredient.setStatus(notAvailable);
        }

        // Highlight required ingredients and set availability
        for (Ingredient requiredIngredient : requiredIngredients) {

            // Iterate all the ingredients from the fridge and shelf that match the same naming with the one from the recipe details
            Ingredient combinedIngredient = findIngredientInCombinedContents(combinedContents, requiredIngredient.getName());
            if (combinedIngredient != null) {
                combinedIngredient.setCell(highlight);

                // Checks if the ingredients from the fridge and shelf is equal or more than the one in the recipe
                if (combinedIngredient.getQuantity() >= requiredIngredient.getQuantity()) {
                    combinedIngredient.setStatus(available);
                } else {
                    combinedIngredient.setStatus(notAvailable);
                }
            }
        }

        // Update the fridge and shelf panels whenever changes are made
        updateFridgeList();
        updateShelfList();
    }

    private void resetIngredientStates() {
        List<Ingredient> combinedContents = new ArrayList<>(); // This list will be used to combine both the ingredients from fridge and shelf
        combinedContents.addAll(fridge.getContents());
        combinedContents.addAll(shelf.getContents());
        for (Ingredient ingredient : combinedContents) {
            ingredient.setStatus(notAvailable);
            ingredient.setCell(removeHighlight);
        }
    }

    // Method to play a sound effect
    private void playSound(String soundFileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to show the whole GUI
    public void showGUI() {
        frame.setVisible(true);
    }
}
