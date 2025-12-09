// GameGenerator.java
// Handles puzzle generation (DSA: ArrayList for shuffling and randomization).

import java.util.ArrayList;
import java.util.Collections;

public class GameGenerator {
    private final Solver solver;

    public GameGenerator(Solver solver) {
        this.solver = solver;
    }

    public void generateNew(BoardModel model) {
        int[][] solution = model.getSolution();
        clear(solution);
        fillDiagonalBlocks(solution);
        if (!solver.solve(solution)) {
            generateNew(model); // Retry if solve fails (rare)
            return;
        }
        // Copy to puzzle and remove cells
        int[][] puzzle = model.getPuzzle();
        for (int r = 0; r < 9; r++) System.arraycopy(solution[r], 0, puzzle[r], 0, 9);
        removeRandomCells(model.getNumberOfEmptyCells(), puzzle);
    }

    private void clear(int[][] grid) {
        for (int r = 0; r < 9; r++) for (int c = 0; c < 9; c++) grid[r][c] = 0;
    }

    private void fillDiagonalBlocks(int[][] grid) {
        for (int block = 0; block < 3; block++) {
            ArrayList<Integer> nums = new ArrayList<>();
            for (int i = 1; i <= 9; i++) nums.add(i);
            Collections.shuffle(nums);
            int br = block * 3;
            int bc = block * 3;
            int idx = 0;
            for (int r = br; r < br + 3; r++) {
                for (int c = bc; c < bc + 3; c++) {
                    grid[r][c] = nums.get(idx++);
                }
            }
        }
    }

    private void removeRandomCells(int count, int[][] puzzle) {
        ArrayList<Integer> positions = new ArrayList<>();
        for (int i = 0; i < 81; i++) positions.add(i);
        Collections.shuffle(positions);
        for (int i = 0; i < count && i < 81; i++) {
            int pos = positions.get(i);
            puzzle[pos / 9][pos % 9] = 0;
        }
    }
}