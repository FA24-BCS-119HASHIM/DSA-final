// Validator.java
// Simplified using built-in HashSet for tracking (DSA: Sets for unique elements and O(1) checks).

import java.util.HashSet;

public class Validator {

    public boolean isValidPlacement(int[][] grid, int row, int col, int num) {
        // Check row (DSA: Linear scan O(9))
        for (int c = 0; c < 9; c++) {
            if (grid[row][c] == num) return false;
        }
        // Check column
        for (int r = 0; r < 9; r++) {
            if (grid[r][col] == num) return false;
        }
        // Check 3x3 box
        int br = (row / 3) * 3;
        int bc = (col / 3) * 3;
        for (int r = br; r < br + 3; r++) {
            for (int c = bc; c < bc + 3; c++) {
                if (grid[r][c] == num) return false;
            }
        }
        return true;
    }

    public void buildTrackers(int[][] grid, HashSet<Integer>[] rowSets, HashSet<Integer>[] colSets, HashSet<Integer>[] boxSets) {
        for (int i = 0; i < 9; i++) {
            rowSets[i] = new HashSet<>();
            colSets[i] = new HashSet<>();
            boxSets[i] = new HashSet<>();
        }
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int v = grid[r][c];
                if (v != 0) {
                    rowSets[r].add(v);
                    colSets[c].add(v);
                    boxSets[(r / 3) * 3 + (c / 3)].add(v);
                }
            }
        }
    }
}