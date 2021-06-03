package View;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MazeDisplayer extends Canvas {
    private int[][] maze;
    StringProperty WallImageFile;
    StringProperty CharacterImageFile;

    public void drawMaze(int[][] maze) {
        this.maze = maze;
        draw();
    }

    public String getWallImageFile() {
        return WallImageFile.get();
    }

    public void setWallImageFile(String wallImageFile) {
        this.WallImageFile.set(wallImageFile);
    }

    public String getCharacterImageFile() {
        return CharacterImageFile.get();
    }

    public void setCharacterImageFile(String characterImageFile) {
        this.CharacterImageFile.set(characterImageFile);
    }

    public void updateMaze(double height, double width) {
        this.setHeight(height);
        this.setWidth(width);
        if(maze != null){
            draw();
        }
    }

    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            graphicsContext.setFill(Color.RED);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(maze[i][j] == 1){
                        //if it is a wall:
                        double x = j * cellWidth;
                        double y = i * cellHeight;
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }
}