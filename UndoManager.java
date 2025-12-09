// UndoManager.java
// Manages undo/redo (DSA: Stack for LIFO operations).

import java.util.Stack;

public class UndoManager {
    private final Stack<int[]> undoStack = new Stack<>();
    private final Stack<int[]> redoStack = new Stack<>();

    public void recordMove(int row, int col, int oldValue, int newValue) {
        undoStack.push(new int[]{row, col, oldValue, newValue});
        redoStack.clear(); // Clear redo on new action
    }

    public void undo(int[][] puzzle) {
        if (undoStack.isEmpty()) return;
        int[] move = undoStack.pop();
        int row = move[0], col = move[1], oldValue = move[2], newValue = move[3];
        puzzle[row][col] = oldValue;
        redoStack.push(new int[]{row, col, newValue, oldValue});
    }

    public void redo(int[][] puzzle) {
        if (redoStack.isEmpty()) return;
        int[] move = redoStack.pop();
        int row = move[0], col = move[1], oldValue = move[2], newValue = move[3];
        puzzle[row][col] = oldValue;
        undoStack.push(new int[]{row, col, newValue, oldValue});
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}