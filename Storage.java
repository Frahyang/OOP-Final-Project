import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

public class Storage implements Serializable {

    // Instance variables
    private static final long serialVersionUID = 1L; // Ensure compatibility between different versions
    private List<Ingredient> contents;

    // Constructor
    public Storage() {
        this.contents = new ArrayList<>();
    }

    // Getter
    public List<Ingredient> getContents() {
        return contents;
    }

    // Methods to modify ingredients in storage
    public void adding(Ingredient ingredient) {
        contents.add(ingredient);
    }
    public void removing(Ingredient ingredient) {
        contents.remove(ingredient);
    }

    // Method to search for a specific ingredient in the storage
    public Ingredient findIngredient(String name) {
        for (Ingredient ingredient : contents) {
            if (ingredient.getName().equalsIgnoreCase(name)) {
                return ingredient;
            }
        }
        return null; // If the ingredient is not found
    }

    // Method to store the storage contents in a file
    public void saveContentsToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load the contents of the file 
    @SuppressWarnings("unchecked")
    public void loadContentsFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            contents = (List<Ingredient>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}