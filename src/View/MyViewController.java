package View;

import Model.Direction;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    public double WidthRatio = 820;
    public double HeightRatio = 770;
    public Thread musicThread;
    public volatile boolean stop;
    public MediaPlayer mediaPlayer;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    public MediaPlayer WinmediaPlayer;

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
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        viewModel.generateMaze(rows, cols);
    }


    public void solveMaze(ActionEvent actionEvent) {
        if(viewModel.getMaze() == null){
            throwInfoAlert("Only a donkey jumps in the head.. Initialize a maze first");
        }
        else{
            throwInfoAlert("Finding a route for you");
            viewModel.solveMaze();
        }
    }


    public void keyPressed(KeyEvent keyEvent) {
        Direction direction;
        switch (keyEvent.getCode()){
            case UP, NUMPAD8 -> direction = Direction.UP;
            case DOWN, NUMPAD2 -> direction = Direction.DOWN;
            case LEFT, NUMPAD4 -> direction = Direction.LEFT;
            case RIGHT, NUMPAD6 -> direction = Direction.RIGHT;
            case NUMPAD9 -> direction = Direction.UPRIGHT;
            case NUMPAD7 -> direction = Direction.UPLEFT;
            case NUMPAD1 -> direction = Direction.DOWNLEFT;
            case NUMPAD3 -> direction = Direction.DOWNRIGHT;
            default -> {
                // no need to move the player...
                return;
            }
        }
        viewModel.movePlayer(direction);
        mazeDisplay.movePlayer(direction);
        keyEvent.consume();
        playMoveSound();
    }

    private void playMoveSound() {
        Media sound = new Media(this.getClass().getResource("/music/movesound.wav").toString());
        //Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer= new MediaPlayer(sound);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();

    }

    public void setPlayerPosition(int row, int col){
        mazeDisplay.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
        Maze maze = viewModel.getMaze();
        if(maze.getGoalPosition().getRowIndex() == row && maze.getGoalPosition().getColumnIndex() == col) {
            stop = true;
            if(!(mediaPlayer == null))
                mediaPlayer.stop();
            Media sound = new Media(this.getClass().getResource("/music/CatVibing.mp3").toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(0.2);
            mediaPlayer.play();
            YouWin();
        }

    }

    private void YouWin() {
        try {
            Stage stage = new Stage();
            stage.setTitle("You Have Won!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("YouWin.fxml").openStream());
            Scene scene = new Scene(root, 700, 400);
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
            NewMaze(new ActionEvent());
            stage.close();
            mediaPlayer.stop();
        }
        else
            Exit();

    }



    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplay.requestFocus();
        viewModel.movePlayer(mouseEvent);
    }

    public void Scrolled(ScrollEvent scrollEvent) {
        if(scrollEvent.isControlDown()){
            if(scrollEvent.getDeltaY() > 0)
                this.mazeDisplay.zoomIn();
            else
                this.mazeDisplay.zoomOut();
            AdjustSize();
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "Generated" -> mazeGenerated();
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
        if(viewModel.getMaze() == null){
            throwInfoAlert("A Maze has yet to be initialized");
        }
        else{
            try {
                DirectoryChooser DirChooser = new DirectoryChooser();
                DirChooser.setTitle("Open maze");
                DirChooser.setInitialDirectory(new File("./resources"));
                File chosen = DirChooser.showDialog(null);
                if (chosen != null) {
                    File saveDir = new File(chosen.getPath());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveDir));
                    Object object = new Object();
                    object = viewModel.getMaze();
                    objectOutputStream.writeObject(object);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
                else{
                    throwInfoAlert("Error choosing directory");
                }
            }
            catch (Exception e){
                throwInfoAlert("Could not save the specific file");
            }
        }

    }

    public void LoadMaze(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Open maze");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
            fc.setInitialDirectory(new File("./resources"));
            File chosen = fc.showOpenDialog(null);
            if (chosen != null) {
                FileInputStream inputStream = new FileInputStream(chosen);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                viewModel.LoadMaze((Maze) objectInputStream.readObject());
                objectInputStream.close();
                inputStream.close();
            }
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Could not load the specific file");
            alert.show();
        }
    }


    public void OpenProperties(ActionEvent actionEvent) {

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            viewModel.Exit();
            System.exit(0);
        }
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void Rules(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Rules");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Rules.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
