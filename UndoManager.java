// UndoManager.java
// Manages undo/redo (DSA: Stack for LIFO operations).
// Updated to handle bulk operations (e.g., reveal solution) by storing full grid snapshots (int[9][9]).
// Uses Object in Stack for polymorphism: int[] for single moves, int[][] for bulk.
// (DSA note: instanceof for type checking, array copies for state restoration.)

import java.util.Stack;

public class UndoManager {
    private final Stack<Object> undoStack = new Stack<>();
    private final Stack<Object> redoStack = new Stack<>();

    public void recordMove(int row, int col, int oldValue, int newValue) {
        undoStack.push(new int[]{row, col, oldValue, newValue});
        redoStack.clear(); // Clear redo on new action
    }

    public void recordBulk(int[][] oldGrid, int[][] newGrid) {
        // Push oldGrid for undo (bulk)
        undoStack.push(copyGrid(oldGrid));
        redoStack.clear();
    }

    public void undo(int[][] puzzle) {
        if (undoStack.isEmpty()) return;
        Object move = undoStack.pop();
        if (move instanceof int[]) { // Single cell
            int[] single = (int[]) move;
            int row = single[0], col = single[1], oldValue = single[2], newValue = single[3];
            int currentValue = puzzle[row][col];
            puzzle[row][col] = oldValue;
            redoStack.push(new int[]{row, col, currentValue, oldValue}); // Note: for redo, old is newValue, but since reverting, adjust
        } else if (move instanceof int[][]) { // Bulk
            int[][] oldGrid = (int[][]) move;
            int[][] currentGrid = copyGrid(puzzle);
            copyGridTo(oldGrid, puzzle);
            redoStack.push(currentGrid);
        }
    }

    public void redo(int[][] puzzle) {
        if (redoStack.isEmpty()) return;
        Object move = redoStack.pop();
        if (move instanceof int[]) { // Single cell
            int[] single = (int[]) move;
            int row = single[0], col = single[1], oldValue = single[2], newValue = single[3];
            int currentValue = puzzle[row][col];
            puzzle[row][col] = oldValue; // For redo, "oldValue" is actually the value to set (the "new" from original)
            undoStack.push(new int[]{row, col, currentValue, oldValue});
        } else if (move instanceof int[][]) { // Bulk
            int[][] redoGrid = (int[][]) move;
            int[][] currentGrid = copyGrid(puzzle);
            copyGridTo(redoGrid, puzzle);
            undoStack.push(currentGrid);
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    private int[][] copyGrid(int[][] source) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(source[r], 0, copy[r], 0, 9);
        }
        return copy;
    }

    private void copyGridTo(int[][] source, int[][] target) {
        for (int r = 0; r < 9; r++) {
            System.arraycopy(source[r], 0, target[r], 0, 9);
        }
    }
}
