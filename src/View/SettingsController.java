package View;

import Model.MyModel;
import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.search.ASearchingAlgorithm;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, Observer {
    public TextField textfield_numofthreads;
    public MenuButton PickAlgorithm;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void update(Observable o, Object arg) {

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
    }

    public void SetConfigurations(String[] configuriations) {
        PickAlgorithm.setText(configuriations[0]);
        textfield_numofthreads.setText(configuriations[1]);
    }
}
