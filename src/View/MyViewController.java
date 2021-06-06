package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements Observer, Initializable, IView {
    private Thread musicThread;
    private MediaPlayer MediaPlayer;
    public MyViewModel viewModel;
    public MazeDisplay mazeDisplay;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label playerRow;
    public Label playerCol;
    double WidthRatio = 820;
    double HeightRatio = 770;
    public boolean stop;




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
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        viewModel.generateMaze(rows, cols);

    }

    public void solveMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
        viewModel.solveMaze();
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        //...
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col) {
        mazeDisplay.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplay.requestFocus();
        viewModel.movePlayer(mouseEvent);
    }

    public void Scrolled(ScrollEvent scrollEvent) {
        if (scrollEvent.isControlDown()) {
            if (scrollEvent.getDeltaY() > 0)
                this.mazeDisplay.zoomIn();
            else
                this.mazeDisplay.zoomOut();
            AdjustSize();
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change) {
            case "Generated" -> mazeGenerated();
            case "Moved" -> playerMoved();
            case "Solved" -> mazeSolved();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void mazeSolved() {
        mazeDisplay.drawSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    private void mazeGenerated() {
        mazeDisplay.drawMaze(viewModel.getMaze());
    }

    public void AdjustSize() {
        this.mazeDisplay.setWidth(WidthRatio);
        this.mazeDisplay.setHeight(HeightRatio);
        this.mazeDisplay.resize();
    }

    public void listenToScene(Scene scene) {
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            this.WidthRatio -= (oldVal.doubleValue() - newVal.doubleValue());
            AdjustSize();
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.HeightRatio -= (oldVal.doubleValue() - newVal.doubleValue());
            AdjustSize();
        });
    }




    public void playAudio(ActionEvent actionEvent) {

        stop=false;
        musicThread = new Thread(()->{
                try {
                    while(!stop) {
                        //String musicFile = "resources/Sounds/theme.mp3";
                        Media sound = new Media(this.getClass().getResource("/music/pokemon.mp3").toString());
                        //Media sound = new Media(new File(musicFile).toURI().toString());
                        MediaPlayer mediaPlayer= new MediaPlayer(sound);
                        mediaPlayer.setVolume(0.1);
                        mediaPlayer.play();
                        int time = 220000;
                        Thread.sleep(time);


                    }
                }
                catch (Exception e) {
                    System.out.println("end of thread");

                }
            });
            musicThread.start();
        }

}

