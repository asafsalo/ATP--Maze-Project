package sample;

import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.awt.*;

public class Main extends Application {
    public static MyViewModel vm;
    Button b;
    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.StartServers();
        vm = new MyViewModel(model);
        Parent root = FXMLLoader.load(getClass().getResource("../View/Try.fxml"));
        primaryStage.setTitle("Kings Maze");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
