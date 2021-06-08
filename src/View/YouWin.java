package View;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class YouWin implements Initializable {

    public ImageView CatDrummerGif;
    public MediaPlayer WinmediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            Image gif = new Image(this.getClass().getResourceAsStream("/images/catdrummer.gif"));
            CatDrummerGif.setImage(gif);



        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
