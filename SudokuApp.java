// SudokuApp.java
// Updated UI to include undo/redo buttons in menu.
// Simplified event handling, integrated with BoardModel, GameGenerator, and UndoManager.

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SudokuApp extends Application {
    private final BoardModel boardModel = new BoardModel();
    private final GameGenerator generator = new GameGenerator(new Solver());
    private final UndoManager undoManager = new UndoManager();
    private final CellPane[] cells = new CellPane[81];

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sudoku (JavaFX)");
        BorderPane root = new BorderPane();
        MenuBar menuBar = buildMenu(primaryStage);
        root.setTop(menuBar);

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(10));
        for (int i = 0; i < 81; i++) {
            int r = i / 9;
            int c = i % 9;
            CellPane cp = new CellPane(r, c);
            cells[i] = cp;
            final int idx = i;
            cp.getButton().setOnAction(e -> onCellClicked(idx));
            grid.add(cp, c, r);
        }

        root.setCenter(grid);
        Scene scene = new Scene(root, 9 * 66 + 40, 9 * 66 + 120);
        primaryStage.setScene(scene);

        generator.generateNew(boardModel);
        undoManager.clear();
        refreshUI();

        primaryStage.show();
    }

    private MenuBar buildMenu(Stage owner) {
        MenuBar mb = new MenuBar();
        Menu game = new Menu("Game");
        MenuItem newGame = new MenuItem("New Game");
        MenuItem showAnswer = new MenuItem("Show Answer");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        newGame.setOnAction(e -> {
            generator.generateNew(boardModel);
            undoManager.clear();
            refreshUI();
        });
        showAnswer.setOnAction(e -> {
            boardModel.revealSolution();
            refreshUI();
        });
        undo.setOnAction(e -> {
            undoManager.undo(boardModel.getPuzzle());
            refreshUI();
        });
        redo.setOnAction(e -> {
            undoManager.redo(boardModel.getPuzzle());
            refreshUI();
        });
        game.getItems().addAll(newGame, showAnswer, undo, redo);

        Menu settings = new Menu("Settings");
        MenuItem changeEmpty = new MenuItem("Change Number of Empty Cells");
        changeEmpty.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(Integer.toString(20));
            dialog.initOwner(owner);
            dialog.setHeaderText("Enter number of empty cells (1-81):");
            dialog.showAndWait().ifPresent(s -> {
                try {
                    int n = Integer.parseInt(s);
                    boardModel.setNumberOfEmptyCells(n);
                    generator.generateNew(boardModel);
                    undoManager.clear();
                    refreshUI();
                } catch (NumberFormatException ex) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Invalid number");
                    a.initOwner(owner);
                    a.showAndWait();
                }
            });
        });
        settings.getItems().add(changeEmpty);

        mb.getMenus().addAll(game, settings);
        return mb;
    }

    private void onCellClicked(int index) {
        int r = index / 9;
        int c = index % 9;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter number (1-9) or 0 to clear:");
        dialog.showAndWait().ifPresent(input -> {
            try {
                int v = Integer.parseInt(input);
                if (v < 0 || v > 9) {
                    showAlert("Enter 0-9");
                    return;
                }
                boolean ok = boardModel.placeNumber(r, c, v, undoManager);
                if (!ok) {
                    showAlert("Invalid placement: violates Sudoku rules.");
                } else {
                    refreshUI();
                    if (boardModel.isSolved()) {
                        Alert win = new Alert(Alert.AlertType.INFORMATION, "You win!");
                        win.showAndWait();
                    }
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid input");
            }
        });
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }

    private void refreshUI() {
        int[][] p = boardModel.getPuzzleCopy();
        Platform.runLater(() -> {
            for (int i = 0; i < 81; i++) {
                int r = i / 9;
                int c = i % 9;
                cells[i].setValue(p[r][c]);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}