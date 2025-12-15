import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SudokuApp extends Application {
    // Game dependencies
    private final BoardModel boardModel = new BoardModel();
    private final GameGenerator generator = new GameGenerator(new Solver());
    private final UndoManager undoManager = new UndoManager();
    private final CellPane[] cells = new CellPane[81];
    private int invalidCount = 0;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sudoku Master");

        // Start the application by showing the main menu
        showStartMenu();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // =======================================================
    // I. START MENU AND UI UTILITIES
    // =======================================================

    /**
     * Creates and displays the beautiful welcome screen.
     */
    private void showStartMenu() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Styling: Gradient background
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);");

        Label titleLabel = new Label("SUDOKU");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 50));

        Button btnStart = new Button("START GAME");
        styleButton(btnStart);
        btnStart.setOnAction(e -> startGame());

        Button btnExit = new Button("END GAME");
        styleButton(btnExit);
        btnExit.setOnAction(e -> Platform.exit());

        root.getChildren().addAll(titleLabel, btnStart, btnExit);

        // Set the primary stage to show the menu
        Scene menuScene = new Scene(root, 600, 600);
        primaryStage.setScene(menuScene);
        primaryStage.sizeToScene();
    }

    private void styleButton(Button btn) {
        btn.setPrefWidth(200);
        btn.setPrefHeight(50);
        btn.setStyle(
                "-fx-background-color: #ecf0f1; " +
                        "-fx-text-fill: #2c3e50; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10;"
        );
        // Simple hover effect
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #2c3e50; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;"));
    }

    // =======================================================
    // II. GAME INITIALIZATION AND UI BUILDING
    // =======================================================

    /**
     * Switches from the Menu to the actual Sudoku game board.
     */
    private void startGame() {
        BorderPane root = new BorderPane();
        MenuBar menuBar = buildMenu(primaryStage);
        root.setTop(menuBar);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: #34495e; -fx-padding: 10;");

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

        // Calculate size based on 64x64 cell size + padding/gaps
        Scene gameScene = new Scene(root, 9 * 66 + 40, 9 * 66 + 120);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();

        generator.generateNew(boardModel);
        undoManager.clear();
        refreshUI();
    }

    private MenuBar buildMenu(Stage owner) {
        MenuBar mb = new MenuBar();
        Menu game = new Menu("Game");
        MenuItem newGame = new MenuItem("New Game");
        MenuItem showAnswer = new MenuItem("Show Answer");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem exitToMenu = new MenuItem("Exit to Main Menu");

        newGame.setOnAction(e -> {
            generator.generateNew(boardModel);
            undoManager.clear();
            invalidCount = 0;
            refreshUI();
        });
        showAnswer.setOnAction(e -> {
            int[][] oldPuzzle = boardModel.getPuzzleCopy();
            boardModel.revealSolution();
            undoManager.recordBulk(oldPuzzle, boardModel.getPuzzle());
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
        exitToMenu.setOnAction(e -> showStartMenu()); // Action to return to main menu

        game.getItems().addAll(newGame, showAnswer, undo, redo, new SeparatorMenuItem(), exitToMenu);

        Menu settings = new Menu("Settings");
        MenuItem changeEmpty = new MenuItem("Change Difficulty (Empty Cells)");
        changeEmpty.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(Integer.toString(boardModel.getNumberOfEmptyCells()));
            dialog.initOwner(owner);
            dialog.setHeaderText("Enter number of empty cells (1-81):");
            dialog.showAndWait().ifPresent(s -> {
                try {
                    int n = Integer.parseInt(s);
                    boardModel.setNumberOfEmptyCells(n);
                    generator.generateNew(boardModel);
                    undoManager.clear();
                    invalidCount = 0;
                    refreshUI();
                } catch (NumberFormatException ex) {
                    showAlert("Invalid number");
                }
            });
        });
        settings.getItems().add(changeEmpty);

        mb.getMenus().addAll(game, settings);
        return mb;
    }

    // =======================================================
    // III. GAMEPLAY AND STATUS UPDATES
    // =======================================================

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
                    invalidCount++;
                    showAlert("Invalid placement: violates Sudoku rules.");
                    if (invalidCount >= 4) {
                        Alert endAlert = new Alert(Alert.AlertType.INFORMATION, "Game Over! Too many mistakes.");
                        endAlert.showAndWait();
                        showStartMenu(); // Return to menu on loss
                    }
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
        // Use Platform.runLater to ensure UI updates are handled safely on the JavaFX thread
        Platform.runLater(() -> {
            for (int i = 0; i < 81; i++) {
                int r = i / 9;
                int c = i % 9;
                cells[i].setValue(p[r][c]);
            }
        });
    }
            }
