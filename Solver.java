// Solver.java
// Uses backtracking (DSA: Recursion with pruning) and HashSets for fast checks.

import java.util.HashSet;

public class Solver {
    private final Validator validator = new Validator();

    public boolean solve(int[][] grid) {
        HashSet<Integer>[] rowSets = new HashSet[9];
        HashSet<Integer>[] colSets = new HashSet[9];
        HashSet<Integer>[] boxSets = new HashSet[9];
        validator.buildTrackers(grid, rowSets, colSets, boxSets);
        return backtrack(grid, 0, 0, rowSets, colSets, boxSets);
    }

    private boolean backtrack(int[][] grid, int row, int col,
                              HashSet<Integer>[] rowSets, HashSet<Integer>[] colSets, HashSet<Integer>[] boxSets) {
        if (row == 9) return true; // Base case: grid filled

        int nextRow = (col == 8) ? row + 1 : row;
        int nextCol = (col == 8) ? 0 : col + 1;

        if (grid[row][col] != 0) {
            return backtrack(grid, nextRow, nextCol, rowSets, colSets, boxSets);
        }

        int boxIndex = (row / 3) * 3 + (col / 3);
        for (int val = 1; val <= 9; val++) {
            if (!rowSets[row].contains(val) && !colSets[col].contains(val) && !boxSets[boxIndex].contains(val)) {
                // Place value
                grid[row][col] = val;
                rowSets[row].add(val);
                colSets[col].add(val);
                boxSets[boxIndex].add(val);

                if (backtrack(grid, nextRow, nextCol, rowSets, colSets, boxSets)) return true;

                // Backtrack (undo)
                grid[row][col] = 0;
                rowSets[row].remove(val);
                colSets[col].remove(val);
                boxSets[boxIndex].remove(val);
            }
        }
        return false;
    }
}