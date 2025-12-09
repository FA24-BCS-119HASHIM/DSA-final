// CellPane.java
// Updated to color cells in a checkerboard pattern: lightgreen and darkgreen alternating.
// (DSA note: Uses modulo operation for alternating pattern, a simple algorithmic check.)
// Updated text color: black on lightgreen, white on darkgreen for better visibility.

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class CellPane extends StackPane {
    private final Button button = new Button();
    private final int row;
    private final int col;

    public CellPane(int row, int col) {
        this.row = row;
        this.col = col;
        setPrefSize(64, 64);
        // Set checkerboard color (DSA: (row + col) % 2 for alternation)
        String bgColor = ((row + col) % 2 == 0) ? "lightgreen" : "darkgreen";
        this.setStyle("-fx-background-color: " + bgColor + ";");
        // Set text color for contrast: black on light, white on dark
        String textColor = ((row + col) % 2 == 0) ? "black" : "white";
        button.setPrefSize(64, 64);
        // Make button transparent and set text color
        button.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: " + textColor + ";");
        setAlignment(Pos.CENTER);
        getChildren().add(button);
    }

    public Button getButton() { return button; }
    public int getRow() { return row; }
    public int getCol() { return col; }

    public void setValue(int v) {
        button.setText(v == 0 ? "" : Integer.toString(v));
        button.setDisable(v != 0); // Disable prefilled cells
    }
}
