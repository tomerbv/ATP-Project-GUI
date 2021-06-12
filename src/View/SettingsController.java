package View;

import Model.IModel;
import Model.MyModel;
import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.search.ASearchingAlgorithm;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, Observer {
    public TextField textfield_numofthreads;
    public MenuButton PickAlgorithm;
    private MyViewModel viewModel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public void ChangeToBestFirst(ActionEvent actionEvent) {
        PickAlgorithm.setText("BestFirstSearch");
    }

    public void ChangeToBreadthFirst(ActionEvent actionEvent) {
        PickAlgorithm.setText("BreadthFirstSearch");
    }

    public void ChangeToDepthFirst(ActionEvent actionEvent) {
        PickAlgorithm.setText("DepthFirstSearch");
    }

    public void SaveSettings(ActionEvent actionEvent) {
        String numofthreads = textfield_numofthreads.getText();
        String SearchingAlgo = PickAlgorithm.getText();
        int threads;
        if(!numofthreads.isEmpty()) {
            try {
                threads = Integer.parseInt(numofthreads);
                if (threads < 1) {
                    throwInfoAlert("number of threads must be at least 1");
                }


            } catch (Exception e) {
                throwInfoAlert("only numbers are accepted");
            }
        }
        viewModel.SetConfigurations(numofthreads,SearchingAlgo);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Settings have been changes , returning to Game");
        alert.show();
        Stage stage = (Stage) this.PickAlgorithm.getScene().getWindow();
        stage.close();


    }


    private void Exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.Exit();
            System.exit(0);
        }
    }


    public void SetConfigurations(String[] configuriations) {
        PickAlgorithm.setText(configuriations[0]);
        textfield_numofthreads.setText(configuriations[1]);
    }
    public void throwInfoAlert(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(text);
        alert.show();
    }

    public void CancelAndExit(ActionEvent actionEvent) {
        throwInfoAlert("Settings have not been saved , returning to Game;");
        Stage stage = (Stage) this.PickAlgorithm.getScene().getWindow();
        stage.close();


    }
}
