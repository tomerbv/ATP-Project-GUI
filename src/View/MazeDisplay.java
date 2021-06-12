package View;

import Model.Direction;
import algorithms.search.AState;
import algorithms.mazeGenerators.Maze;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
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
    private double zoomDeviationX = 0;
    private double zoomDeviationY = 0;
    private int playerRow = 0;
    private int playerCol = 0;
    private Image playerImage;
    private volatile Object zoomLock = new Object();

    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameFloor = new SimpleStringProperty();
    StringProperty imageFileNameStart = new SimpleStringProperty();
    StringProperty imageFileNameFinish = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameLeft = new SimpleStringProperty();
    StringProperty imageFileNameRight = new SimpleStringProperty();
    StringProperty imageFileNameUp = new SimpleStringProperty();
    StringProperty imageFileNameDown = new SimpleStringProperty();
    

    public String getImageFileNameLeft() {
        return imageFileNameLeft.get();
    }



    public void setImageFileNameLeft(String imageFileNameLeft) {
        this.imageFileNameLeft.set(imageFileNameLeft);
    }

    public String getImageFileNameRight() {
        return imageFileNameRight.get();
    }



    public void setImageFileNameRight(String imageFileNameRight) {
        this.imageFileNameRight.set(imageFileNameRight);
    }

    public String getImageFileNameUp() {
        return imageFileNameUp.get();
    }



    public void setImageFileNameUp(String imageFileNameUp) {
        this.imageFileNameUp.set(imageFileNameUp);
    }

    public String getImageFileNameDown() {
        return imageFileNameDown.get();
    }



    public void setImageFileNameDown(String imageFileNameDown) {
        this.imageFileNameDown.set(imageFileNameDown);
    }

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
        UpdatePlayerImage(getImageFileNameDown());

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
                double x = j * cellWidth + zoomDeviationX;
                double y = i * cellHeight + zoomDeviationY;
                if(maze.getGoalPosition().getRowIndex() == i && maze.getGoalPosition().getColumnIndex() == j){
                    if(FinishImage == null ) {
                        graphicsContext.setFill(Color.RED);
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        graphicsContext.setFill(Color.DARKGRAY);
                    }
                    else
                        graphicsContext.drawImage(FloorImage, x, y, cellWidth, cellHeight);
                        graphicsContext.drawImage(FinishImage, x + cellWidth*0.1, y + cellHeight*0.1, cellWidth*0.8, cellHeight*0.8);

                }

                else if(maze.GetPositionVal(i,j) == 1){
                    //if it is a wall:
                    if(WallImage == null )
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(WallImage, x, y, cellWidth, cellHeight);

                }
                else{
                    if(FloorImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(FloorImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void UpdatePlayerImage(String path){
        try{
            this.playerImage = new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            System.out.println("An image file is missing");
        }
    }
    public void movePlayer(Direction direction) {
        switch (direction) {
            case UP ->        UpdatePlayerImage(getImageFileNameUp());
            case DOWN ->      UpdatePlayerImage(getImageFileNameDown());
            case LEFT ->      UpdatePlayerImage(getImageFileNameLeft());
            case RIGHT ->     UpdatePlayerImage(getImageFileNameRight());
            case UPRIGHT ->   UpdatePlayerImage(getImageFileNameUp());
            case DOWNRIGHT -> UpdatePlayerImage(getImageFileNameDown());
            case UPLEFT ->    UpdatePlayerImage(getImageFileNameUp());
            case DOWNLEFT ->  UpdatePlayerImage(getImageFileNameDown());
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth + zoomDeviationX;
        double y = getPlayerRow() * cellHeight + zoomDeviationY;
        graphicsContext.setFill(Color.GREEN);


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
                    double x = pathInd.getPosition().getColumnIndex() * cellWidth + zoomDeviationX;
                    double y = pathInd.getPosition().getRowIndex() * cellHeight + zoomDeviationY;
                    graphicsContext.fillRoundRect(x + cellWidth/4, y + cellHeight/4, cellWidth/2, cellHeight/2, 5,5);
                }
            }

        }
    }

    public void setNewPokemon() {
        String curr = imageFileNameFinish.getValue();
        if(curr.contains("Charizard"))
            this.imageFileNameFinish.set("./resources/images/Mewtwo.png");
        if(curr.contains("Mewtwo"))
            this.imageFileNameFinish.set("./resources/images/Pikachu.png");
    }

    public void resize() {
        draw();
    }

    public void zoomIn() {
        synchronized (zoomLock){
            if(zoomFactor > 0.1) {
                zoomFactor -= 0.05;
                zoomDeviationY = -((getHeight()/zoomFactor -(getHeight()))/2);
                zoomDeviationX = -((getWidth() /zoomFactor -(getWidth()))/2);

            }
        }
    }

    public void zoomOut() {
        synchronized (zoomLock){
            if(zoomFactor < 1) {
                zoomFactor += 0.05;
                zoomDeviationY = -((getHeight()/zoomFactor -(getHeight()))/2);
                zoomDeviationX = -((getWidth() /zoomFactor -(getWidth()))/2);
            }
        }
    }

    public double getZoomDeviationX() {
        return zoomDeviationX;
    }

    public double getZoomDeviationY() {
        return zoomDeviationY;
    }


    public void moveZoom(Direction direction) {
        synchronized (zoomLock){
            switch (direction) {
                case UP -> {
                    if(-zoomDeviationY > getCellHeight())
                        zoomDeviationY += getCellHeight();
                    else
                        zoomDeviationY = 0;
                }
                case DOWN -> {
                    if(zoomDeviationY - getCellHeight() > -((getHeight()/zoomFactor) -(getHeight())))
                        zoomDeviationY -= getCellHeight();
                    else
                        zoomDeviationY = -((getHeight()/zoomFactor) -(getHeight()));


                }
                case LEFT -> {
                    if(-zoomDeviationX > getCellWidth())
                        zoomDeviationX += getCellWidth();
                    else
                        zoomDeviationX = 0;

                }
                case RIGHT -> {
                    if(zoomDeviationX - getCellWidth() > -((getWidth()/zoomFactor) -(getWidth())))
                        zoomDeviationX -= getCellWidth();
                    else
                        zoomDeviationX = -((getWidth()/zoomFactor) -(getWidth()));

                }
                case UPRIGHT -> {
                    if(-zoomDeviationY > getCellHeight())
                        zoomDeviationY += getCellHeight();
                    else
                        zoomDeviationY = 0;
                    if(zoomDeviationX - getCellWidth() > -((getWidth()/zoomFactor) -(getWidth())))
                        zoomDeviationX -= getCellWidth();
                    else
                        zoomDeviationX = -((getWidth()/zoomFactor) -(getWidth()));
                }
                case UPLEFT -> {
                    if(-zoomDeviationY > getCellHeight())
                        zoomDeviationY += getCellHeight();
                    else
                        zoomDeviationY = 0;
                    if(-zoomDeviationX > getCellWidth())
                        zoomDeviationX += getCellWidth();
                    else
                        zoomDeviationX = 0;

                }
                case DOWNLEFT -> {
                    if(zoomDeviationY - getCellHeight() > -((getHeight()/zoomFactor) -(getHeight())))
                        zoomDeviationY -= getCellHeight();
                    else
                        zoomDeviationY = -((getHeight()/zoomFactor) -(getHeight()));
                    if(-zoomDeviationX > getCellWidth())
                        zoomDeviationX += getCellWidth();
                    else
                        zoomDeviationX = 0;
                }
                case DOWNRIGHT -> {
                    if(zoomDeviationY - getCellHeight() > -((getHeight()/zoomFactor) -(getHeight())))
                        zoomDeviationY -= getCellHeight();
                    else
                        zoomDeviationY = -((getHeight()/zoomFactor) -(getHeight()));
                    if(zoomDeviationX - getCellWidth() > -((getWidth()/zoomFactor) -(getWidth())))
                        zoomDeviationX -= getCellWidth();
                    else
                        zoomDeviationX = -((getWidth()/zoomFactor) -(getWidth()));
                }
                case PLUS -> zoomIn();
                case MINUS -> zoomOut();
            }
            draw();
        }

    }

}
