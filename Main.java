import javax.swing.SwingUtilities;

// Main method
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                // Create an instance of CookbookGUI
                CookBookGUI cookbookGUI = new CookBookGUI();

                // Set the visibility of the frame to true
                cookbookGUI.showGUI();
            }
        });
    }
}