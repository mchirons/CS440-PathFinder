package application;

import java.io.File;

import controller.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private MainViewController mvc;

	@Override
	public void start(Stage primaryStage) {
		try {

			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("PathFinder");
			initRootLayout();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void initRootLayout(){
		try {
			FXMLLoader loader = new FXMLLoader();
	         loader.setLocation(Main.class.getResource("/view/RootView.fxml"));
	         rootLayout = (BorderPane) loader.load();

	         Scene scene = new Scene(rootLayout);
	         mvc = loader.getController();
	         primaryStage.setScene(scene);
	         primaryStage.setResizable(false);
	         //primaryStage.sizeToScene();
	        // primaryStage.show();
	         mvc.start(primaryStage);
	         mvc.setMain(this);
	         primaryStage.show();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public void handleClose(){
		primaryStage.close();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
