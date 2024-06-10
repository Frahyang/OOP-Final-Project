import java.io.Serializable;

public class Ingredient implements Serializable {

    // Instance variables
    private String name;
    private int quantity;
    private String unit;
    private boolean available;
    private boolean highlighted;

    // Constructor
    public Ingredient(String name, int quantity, String unit) {
        this.name = name; // Ingredient name
        this.quantity = quantity; // Ingredient quantity
        this.unit = unit;
        this.available = false; // Ingredient status
        this.highlighted = false; // For setting a cell of an ingredient to be highlighted
    }

    // Getters
    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getUnit() {
        return unit;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Necessary booleans
    public boolean isHighlighted() { // Decides if the ingredient should be highlighted
        return highlighted;
    } 
    public boolean isAvailable() { // Decides if the highlighted ingredient should be green (enough quantity) or red (not enough)
        return available;
    }

    // Methods for highlighting
    public void setCell(boolean highlighted) {
        this.highlighted = highlighted;
    }
    public void setStatus(boolean available) {
        this.available = available;
    }

    // Method to convert into string
    public String toString() {
        return name + ": " + quantity + " " + unit;
    }
}
