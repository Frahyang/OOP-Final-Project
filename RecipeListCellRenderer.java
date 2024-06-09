import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import java.awt.Component;
import java.awt.Dimension;

public class RecipeListCellRenderer extends DefaultListCellRenderer implements CookBookConstants {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN, BOTTOM_MARGIN, RIGHT_MARGIN)); // Apply left margin
        label.setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
        label.setFont(label.getFont().deriveFont(PANE_FONT_SIZE));

        return label;
    }
}
