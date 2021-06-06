package View;
import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("The Maze");
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController myViewControl = fxmlLoader.getController();
        myViewControl.setViewModel(viewModel);
        myViewControl.listenToSceneSize(scene);
        myViewControl.listenToStageExit(primaryStage);
        
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
