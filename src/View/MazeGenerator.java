package View;

import java.util.Arrays;

public class MazeGenerator {
    public static void main(String[] args) {
        MazeGenerator generator = new MazeGenerator();
        int[][] maze = generator.generateRandomMaze(5, 5);
        System.out.println(Arrays.deepToString(maze));
    }

    public int[][] generateRandomMaze(int rows, int cols){
        int[][] maze = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = (int) Math.round(Math.random());
            }
        }
        return maze;
    }
}
