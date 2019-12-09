// Created by Radhe Bangad and Jasmine Philip

package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.Controller;

public class SongLib extends Application {
	@Override
	public void start(Stage primaryStage) 
	throws IOException {
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation(
				getClass().getResource("/view/SongLib.fxml"));
		AnchorPane root = (AnchorPane)loader.load();


		Controller controller = loader.getController();
		controller.start(primaryStage);

		Scene scene = new Scene(root, 550, 586); //width, length
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);
		primaryStage.show(); 
	}

	public static void main(String[] args) {
		launch(args);
	}

}

