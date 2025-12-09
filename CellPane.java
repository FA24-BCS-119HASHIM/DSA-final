// CellPane.java
// Unchanged, as it's simple UI component.

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
        button.setPrefSize(64, 64);
        button.setStyle("-fx-font-size: 18px;");
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