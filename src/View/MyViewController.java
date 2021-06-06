package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    double WidthRatio = 820;
    double HeightRatio = 770;

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
        if(viewModel.getMaze() == null){
            throwInfoAlert("Only a donkey jumps in the head.. Initialize a maze first");
        }
        else{
            throwInfoAlert("Finding a route for you");
            viewModel.solveMaze();
        }
    }


    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplay.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
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

    }


    public void Rules(ActionEvent actionEvent) {
    }
}
