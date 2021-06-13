package View;

import Model.Direction;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.*;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyViewController implements Observer, Initializable, IView{
    public MyViewModel viewModel;
    public MazeDisplay mazeDisplay;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label playerRow;
    public Label playerCol;
    int goalRow;
    int goalColumn;
    public double WidthRatio = 820;
    public double HeightRatio = 770;
    private double mouseDragStartY;
    private double mouseDragStartX;

    private boolean menuLocker;
    public Thread musicThread;
    public volatile boolean stop;
    public MediaPlayer mediaPlayer;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();





    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void generateMaze(ActionEvent actionEvent) {
        if(menuLocker)
            return;
        String row = textField_mazeRows.getText();
        String col = textField_mazeColumns.getText();
        if(!((row.matches("[0-9]+" ) && row.length() > 0 && (col.matches("[0-9]+" ) && col.length() > 0))))
            throwInfoAlert("Maze row and column values have to be positive integers greater than 2");
        else{
            int rows = Integer.valueOf(row);
            int cols = Integer.valueOf(col);
            if(rows < 3 || cols < 3)
                throwInfoAlert("Maze row and column values have to be positive integers greater than 2");
            else{
                viewModel.generateMaze(rows, cols);
            }
        }
    }



    public void solveMaze(ActionEvent actionEvent) {
        if(menuLocker)
            return;
        if(viewModel.getMaze() == null){
            throwInfoAlert("Only a donkey jumps in the head.. Initialize a maze first");
        }
        else{
            if(viewModel.getSolution()!=null){
                throwInfoAlert("Maze already solved");

            }
            else {
                throwInfoAlert("Finding a route for you");
                viewModel.solveMaze();
            }
        }
    }


    public void keyPressed(KeyEvent keyEvent) {
        if(menuLocker)
            return;
        if(!(viewModel.getMaze() == null)) {
            Direction direction;
            switch (keyEvent.getCode()) {
                case UP, NUMPAD8 -> direction = Direction.UP;
                case DOWN, NUMPAD2 -> direction = Direction.DOWN;
                case LEFT, NUMPAD4 -> direction = Direction.LEFT;
                case RIGHT, NUMPAD6 -> direction = Direction.RIGHT;
                case NUMPAD9 -> direction = Direction.UPRIGHT;
                case NUMPAD7 -> direction = Direction.UPLEFT;
                case NUMPAD1 -> direction = Direction.DOWNLEFT;
                case NUMPAD3 -> direction = Direction.DOWNRIGHT;
                case ADD -> direction = Direction.PLUS;
                case SUBTRACT -> direction = Direction.MINUS;
                default -> {
                    // null direction value
                    return;
                }
            }
            if (keyEvent.isControlDown()) {
                mazeDisplay.moveZoom(direction);
            } else if (direction != null) {
                mazeDisplay.movePlayer(direction);
                viewModel.movePlayer(direction);
                playMoveSound();
            }
            keyEvent.consume();
        }


    }

    private void playMoveSound() {
        Media sound = new Media(this.getClass().getResource("/music/movesound.wav").toString());
        //Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer= new MediaPlayer(sound);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();

    }

    public void setPlayerPosition(int row, int col){
        if(goalRow == row && goalColumn == col) {
            stop = true;
            if(!(mediaPlayer == null))
                mediaPlayer.stop();
            Media sound = new Media(this.getClass().getResource("/music/CatVibing.mp3").toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(0.2);
            mediaPlayer.play();
            YouWin();
        }
        mazeDisplay.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);



    }

    private void YouWin() {
        try {
            Stage stage = new Stage();
            stage.setTitle("You Have Won!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("YouWin.fxml").openStream());
            Scene scene = new Scene(root, 750, 550);
            stage.setOnCloseRequest(WindowEvent -> {
                ExitYouWin(stage);
                WindowEvent.consume();});
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void ExitYouWin(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Play again or quit");
        alert.setHeaderText("Would you like to go for another or did you have enough?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            mazeDisplay.setNewPokemon();
            NewMaze(new ActionEvent());
            stage.close();
            mediaPlayer.stop();

        }
        else
            Exit();

    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if(menuLocker)
            return;
        if(!(viewModel.getMaze() == null)) {
            mazeDisplay.requestFocus();
        }
    }

    public void startDrag(MouseEvent mouseEvent) {
        if(menuLocker)
            return;
        if(!(viewModel.getMaze() == null)) {
            this.mouseDragStartX = mouseEvent.getX();
            this.mouseDragStartY = mouseEvent.getY();
        }
    }

    private boolean isDragOnPlayer(double cellWidth, double cellHeight){
        if(!(viewModel.getMaze() == null)){
            if(mouseDragStartX - mazeDisplay.getZoomDeviationX() >= (mazeDisplay.getPlayerCol()*cellWidth)  && mouseDragStartX <= (mazeDisplay.getPlayerCol()*cellWidth) + cellWidth)
                if(mouseDragStartY - mazeDisplay.getZoomDeviationY() >= (mazeDisplay.getPlayerRow()*cellHeight)  && mouseDragStartY <= (mazeDisplay.getPlayerRow()*cellHeight) + cellHeight)
                    return true;
        }
        return false;
    }

    public void Drag(MouseEvent mouseEvent) {
        if(menuLocker)
            return;
        if(!(viewModel.getMaze() == null)){
            double cellWidth = mazeDisplay.getCellWidth();
            double cellHeight = mazeDisplay.getCellHeight();
            if(isDragOnPlayer(cellWidth, cellHeight)){
                if (mouseEvent.getX() - mouseDragStartX >= cellWidth/2) {
                    mazeDisplay.movePlayer(Direction.RIGHT);
                    viewModel.movePlayer(Direction.RIGHT);
                    playMoveSound();
                    mouseDragStartX += cellWidth;
                }
                if (mouseEvent.getX() - mouseDragStartX <= -(cellWidth / 2)) {
                    mazeDisplay.movePlayer(Direction.LEFT);
                    viewModel.movePlayer(Direction.LEFT);
                    playMoveSound();
                    mouseDragStartX -= cellWidth;
                }
                if (mouseEvent.getY() - mouseDragStartY >= cellHeight/2) {
                    mazeDisplay.movePlayer(Direction.DOWN);
                    viewModel.movePlayer(Direction.DOWN);
                    playMoveSound();
                    mouseDragStartY += cellHeight;
                }
                if (mouseEvent.getY() - mouseDragStartY <= -(cellHeight / 2)) {
                    mazeDisplay.movePlayer(Direction.UP);
                    viewModel.movePlayer(Direction.UP);
                    playMoveSound();
                    mouseDragStartY -= cellHeight;
                }
            }
        }
    }

    public void Scrolled(ScrollEvent scrollEvent) {
        if(menuLocker)
            return;
        if(!(viewModel.getMaze() == null)){
            if(scrollEvent.isControlDown()){
                if(scrollEvent.getDeltaY() > 0)
                    this.mazeDisplay.zoomIn();
                else
                    this.mazeDisplay.zoomOut();
                AdjustSize();
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "Generated" ->{
                mazeGenerated();
                playerMoved();
            }
            case "Moved" -> playerMoved();
            case "Solved" -> mazeSolved();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void mazeSolved() {
        mazeDisplay.updateSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    private void mazeGenerated() {
        goalRow = viewModel.getMaze().getGoalPosition().getRowIndex();
        goalColumn = viewModel.getMaze().getGoalPosition().getColumnIndex();
        mazeDisplay.drawMaze(viewModel.getMaze());
    }

    public void AdjustSize(){
        this.mazeDisplay.setWidth(WidthRatio);
        this.mazeDisplay.setHeight(HeightRatio);
        this.mazeDisplay.resize();
    }

    public void listenToSceneSize(Scene scene) {
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            this.WidthRatio -= (oldVal.doubleValue() - newVal.doubleValue());
            AdjustSize();
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.HeightRatio -= (oldVal.doubleValue() - newVal.doubleValue());
            AdjustSize();
        });
    }


    public void NewMaze(ActionEvent actionEvent) {
        viewModel.generateMaze(Integer.valueOf(textField_mazeRows.getText()), Integer.valueOf(textField_mazeColumns.getText()));
    }

    private void throwInfoAlert(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(text);
        alert.show();
    }

    public void SaveMaze(ActionEvent actionEvent) {
        menuLocker = true;
        if(viewModel.getMaze() == null){
            throwInfoAlert("A Maze has yet to be initialized");
        }
        else{
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Maze");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze", "*.mz"));
                File file = fileChooser.showSaveDialog(null);
                if (file != null) {
                    File saveFile = new File(file.getPath());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile));
                    Object maze = new Object();
                    maze = viewModel.getMaze();

                    objectOutputStream.writeObject(maze);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
                else{
                    throwInfoAlert("Error Saving");
                }
        }
            catch (Exception e){
                throwInfoAlert("Could not save the specific file");
            }
        }
        menuLocker = false;
    }

    public void LoadMaze(ActionEvent actionEvent) {
        menuLocker = true;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Maze");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze", "*.mz"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                File loadFile = new File(file.getPath());
                FileInputStream inputStream = new FileInputStream(loadFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object maze = (Object)objectInputStream.readObject();
                Maze loadedMaze = (Maze)maze;

                viewModel.LoadMaze(loadedMaze);

                objectInputStream.close();
                inputStream.close();
            }
        }
        catch (Exception e){
            throwInfoAlert("Could not load file");
        }
        menuLocker = false;
    }


    public void OpenProperties(ActionEvent actionEvent) {
        menuLocker = true;
        try {
            Stage stage = new Stage();
            stage.setTitle("Porperties Settings");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Settings.fxml").openStream());

            Scene scene = new Scene(root, 400, 350);
            stage.setResizable(false);
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);
            if(mediaPlayer!=null)
                mediaPlayer.stop();
            String[] configuriations = new String[2];
            configuriations = viewModel.getConfigurations();
            SettingsController propertiescontroller = fxmlLoader.getController();
            propertiescontroller.setViewModel(this.viewModel);
            propertiescontroller.SetConfigurations(configuriations);


            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
            throwInfoAlert("could not open Settings scene");
        }
        menuLocker = false;
    }
    public void listenToStageExit(Stage primaryStage) {
        primaryStage.setOnCloseRequest(WindowEvent -> {
            Exit();
            WindowEvent.consume();});
    }


    public void ExitButton(ActionEvent actionEvent) {
        Exit();
    }

    private void Exit() {
        menuLocker = true;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            viewModel.stopServers();
            System.exit(0);
        }
        menuLocker = false;
    }

    public void About(ActionEvent actionEvent) {
        menuLocker = true;
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 450, 350);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        menuLocker = false;

    }


    public void Rules(ActionEvent actionEvent) {
        menuLocker = true;
        try {
            this.menuLocker = true;
            Stage stage = new Stage();
            stage.setTitle("Rules");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Rules.fxml").openStream());
            Scene scene = new Scene(root, 450, 350);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        menuLocker = false;
    }

    public void playAudio(ActionEvent actionEvent) {
        stop = false;
        if(!(mediaPlayer == null))
            mediaPlayer.stop();
        musicThread = new Thread(()-> {
            try {
                while (!stop) {
                    //String musicFile = "resources/Sounds/theme.mp3";
                    Media sound = new Media(this.getClass().getResource("/music/pokemon.mp3").toString());
                    //Media sound = new Media(new File(musicFile).toURI().toString());
                    mediaPlayer = new MediaPlayer(sound);
                    mediaPlayer.setVolume(0.2);
                    mediaPlayer.play();

                    int time = 200000;
                    Thread.sleep(time);
                }
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        });
        musicThread.start();
    }

    public void stopAudio(ActionEvent actionEvent) {
        stop = true;
        mediaPlayer.stop();
    }



}
