// BoardModel.java
// Core data and basic operations (DSA: 2D Arrays for grids).

public class BoardModel {
    private final int[][] puzzle = new int[9][9];
    private final int[][] solution = new int[9][9];
    private final Validator validator = new Validator();
    private int numberOfEmptyCells = 20;

    public int[][] getPuzzleCopy() {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) System.arraycopy(puzzle[r], 0, copy[r], 0, 9);
        return copy;
    }

    public int[][] getSolutionCopy() {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) System.arraycopy(solution[r], 0, copy[r], 0, 9);
        return copy;
    }

    public void setNumberOfEmptyCells(int n) {
        numberOfEmptyCells = Math.max(1, Math.min(81, n));
    }

    public boolean placeNumber(int row, int col, int num, UndoManager undoManager) {
        if (num < 0 || num > 9) return false;
        int oldValue = puzzle[row][col];
        if (num == oldValue) return true;

        if (num == 0 || validator.isValidPlacement(puzzle, row, col, num)) {
            undoManager.recordMove(row, col, oldValue, num);
            puzzle[row][col] = num;
            return true;
        }
        return false;
    }

    public boolean isSolved() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (puzzle[r][c] == 0 || puzzle[r][c] != solution[r][c]) return false;
            }
        }
        return true;
    }

    public void revealSolution() {
        for (int r = 0; r < 9; r++) System.arraycopy(solution[r], 0, puzzle[r], 0, 9);
    }

    // Getters for internal use
    int[][] getPuzzle() { return puzzle; }
    int[][] getSolution() { return solution; }
    int getNumberOfEmptyCells() { return numberOfEmptyCells; }
}