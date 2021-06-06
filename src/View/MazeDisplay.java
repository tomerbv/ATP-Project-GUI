package View;

import algorithms.search.AState;
import algorithms.mazeGenerators.Maze;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplay extends Canvas {
    private Maze maze;
    private Solution solution;
    private int height = 0;
    private int width = 0;
    private double zoomFactor = 1;
    private int playerRow = 0;
    private int playerCol = 0;


    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameFloor = new SimpleStringProperty();
    StringProperty imageFileNameStart = new SimpleStringProperty();
    StringProperty imageFileNameFinish = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();




    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameFloor() {
        return imageFileNameFloor.get();
    }

    public void setImageFileNameFloor(String imageFileNameFloor) {
        this.imageFileNameFloor.set(imageFileNameFloor);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public String getImageFileNameFinish() {
        return imageFileNameFinish.get();
    }

    public void setImageFileNameFinish(String imageFileNameFinish) {
        this.imageFileNameFinish.set(imageFileNameFinish);
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void drawMaze(Maze maze) {
        this.maze = maze;
        this.playerRow = maze.getStartPosition().getRowIndex();
        this.playerCol = maze.getStartPosition().getColumnIndex();
        this.solution = null;
        draw();
    }

    double getCellHeight(){
        return (getHeight() / maze.getRows())/zoomFactor;
    }

    double getCellWidth(){
        return (getWidth() / maze.getColumns())/zoomFactor;
    }

    private void draw() {
        if(maze != null){
            double cellHeight = getCellHeight();
            double cellWidth = getCellWidth();

            GraphicsContext graphicsContext = getGraphicsContext2D();

            graphicsContext.clearRect(0, 0, getWidth(), getHeight());

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, maze.getRows(), maze.getColumns());
            drawSolution(solution, graphicsContext, cellHeight, cellWidth);
            drawPlayer(graphicsContext, cellHeight, cellWidth);

        }
    }

    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.DARKGRAY);
        Image FinishImage = null;
        Image WallImage = null;
        Image FloorImage = null;
        try{
            FinishImage = new Image(new FileInputStream(getImageFileNameFinish()));
            WallImage = new Image(new FileInputStream(getImageFileNameWall()));
            FloorImage = new Image(new FileInputStream(getImageFileNameFloor()));
        } catch (FileNotFoundException e) {
            System.out.println("An image file is missing");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(maze.getStartPosition().getRowIndex() == i && maze.getStartPosition().getColumnIndex() == j){
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    graphicsContext.setFill(Color.GREEN);
                    graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    graphicsContext.setFill(Color.DARKGRAY);
                }

                else if(maze.getGoalPosition().getRowIndex() == i && maze.getGoalPosition().getColumnIndex() == j){
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(FinishImage == null ) {
                        graphicsContext.setFill(Color.RED);
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        graphicsContext.setFill(Color.DARKGRAY);
                    }
                    else
                        graphicsContext.drawImage(FinishImage, x, y, cellWidth, cellHeight);

                }

                else if(maze.GetPositionVal(i,j) == 1){
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(WallImage == null )
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(WallImage, x, y, cellWidth, cellHeight);

                }
                else{
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(FloorImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(FloorImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    public void updateSolution(Solution solution) {
        if(solution == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Could Not Solve Maze");
            alert.show();
        }

        else {
            this.solution = solution;
            draw();
        }
    }

    public void drawSolution(Solution solution, GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        if(solution != null){
            graphicsContext.setFill(Color.LIGHTBLUE);
            ArrayList<AState> path = solution.getSolutionPath();
            for (int i = 0; i < path.size(); i++) {
                MazeState pathInd = (MazeState) path.get(i);
                if(!(maze.getGoalPosition().getRowIndex() == pathInd.getPosition().getRowIndex() && maze.getGoalPosition().getColumnIndex() == pathInd.getPosition().getColumnIndex())){
                    double x = pathInd.getPosition().getColumnIndex() * cellWidth;
                    double y = pathInd.getPosition().getRowIndex() * cellHeight;
                    graphicsContext.fillRoundRect(x + cellWidth/4, y + cellHeight/4, cellWidth/2, cellHeight/2, 5,5);
                }
            }

        }
    }

    public void resize() {
        draw();
    }

    public void zoomIn() {
        if(zoomFactor > 0.1)
            zoomFactor -= 0.05;
    }

    public void zoomOut() {
        if(zoomFactor < 1)
            zoomFactor += 0.05;
    }


}
