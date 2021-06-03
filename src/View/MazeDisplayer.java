package View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {
    StringProperty charcterfilename = new SimpleStringProperty();
    StringProperty wallfilename = new SimpleStringProperty();
    StringProperty villianfilename = new SimpleStringProperty();

    public String getCharcterfilename() {
        return charcterfilename.get();
    }

    public void setCharcterfilename(String charcterfilename) {
        this.charcterfilename.set(charcterfilename);
    }

    public String getWallfilename() {
        return wallfilename.get();
    }

    public void setWallfilename(String wallfilename) {
        this.wallfilename.set(wallfilename);
    }

    public String getVillianfilename() {
        return villianfilename.get();
    }

    public void setVillianfilename(String villianfilename) {
        this.villianfilename.set(villianfilename);
    }

    private int[][] maze;

    public void drawMaze(int[][] maze) {
        this.maze = maze;
        draw();
    }

    private void draw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, rows, cols, canvasWidth, canvasHeight);

        }
    }


    private void drawMazeWalls(GraphicsContext graphicsContext, int rows, int cols, double cellWidth, double cellHeight) {
        Image wallimage = null;
        try {
            wallimage = new Image(new FileInputStream(getWallfilename()));
        } catch (FileNotFoundException e) {
            System.out.println("no such image");
        }
        graphicsContext.setFill(Color.RED);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 1) {
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if (wallimage == null) {
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    }
                    else {
                        graphicsContext.drawImage(wallimage,x,y,cellWidth,cellHeight);

                    }
                }
            }
        }
    }
}