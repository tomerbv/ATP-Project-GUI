package View;

import Model.MyModel;
import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.search.ASearchingAlgorithm;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, Observer {
    public TextField textfield_numofthreads;
    public MenuButton PickAlgorithm;
    public MyViewModel viewModel;



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
    private void throwInfoAlert(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(text);
        alert.show();
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
        Configurations.setProp(Integer.valueOf(numofthreads),"MyMazeGenerator",SearchingAlgo);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Settings have been changes , returning to Game");
        alert.show();
        Stage stage = (Stage) this.PickAlgorithm.getScene().getWindow();
        stage.close();

    }

    public void SetConfigurations(String[] configuriations) {
        PickAlgorithm.setText(configuriations[1]);
        textfield_numofthreads.setText(configuriations[0]);

    }

    public void CancelButton(ActionEvent actionEvent) {
        throwInfoAlert("Settings have not been saved , returning to Game;");
        Stage stage = (Stage) this.PickAlgorithm.getScene().getWindow();
        stage.close();
    }


}
