import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;

public class IngredientListCellRenderer extends DefaultListCellRenderer implements CookBookConstants {
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT - 10));
        label.setFont(label.getFont().deriveFont(PANE_FONT_SIZE));
        label.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN, BOTTOM_MARGIN, RIGHT_MARGIN)); // Apply left margin

        // Check if object referenced by value is an instance of the ingredient class
        if (value instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) value;
            if (isSelected) {
                setBackground(new Color(210, 210, 210));
            } else {
                setBackground(Color.WHITE);
            }
            if (ingredient.isHighlighted()) {
                if (ingredient.isAvailable()) {
                    label.setForeground(new Color(51, 204, 51)); // When ingredient is available set foreground to green colour
                } else {
                    label.setForeground(Color.RED); // Else set foreground to red colour
                }
            } else {
                label.setForeground(Color.BLACK); // Set foreground black if ingredient is not needed
            }
        }
        return label;
    }
}