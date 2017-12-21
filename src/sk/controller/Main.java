package sk.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Properties;

//
//Used to initialize scenes and controllers, launches ship placing stage
public class Main extends Application {

    private final Properties prop = new Properties();

    @Override
    public void start(Stage primaryStage) throws Exception {

        InputStream in = getClass().getResourceAsStream("/resources/values.properties");
        prop.load(in);
        in.close();

        FXMLLoader pickerLoader = new FXMLLoader(getClass().getClassLoader()
                .getResource(prop.getProperty("fxmlPickerPath")));
        FXMLLoader fieldLoader = new FXMLLoader(getClass().getClassLoader()
                .getResource(prop.getProperty("fxmlFieldPath")));

        PickerController pickerController = new PickerController();
        FieldController fieldController = new FieldController();

        pickerLoader.setController(pickerController);
        fieldLoader.setController(fieldController);

        Scene pickerScene = new Scene(pickerLoader.load(), Integer.parseInt(prop.getProperty("windowHeight")),
                Integer.parseInt(prop.getProperty("windowWidth")));
        Scene fieldScene = new Scene(fieldLoader.load(), Integer.parseInt(prop.getProperty("windowHeight")),
                Integer.parseInt(prop.getProperty("windowWidth")));

        pickerController.setMainStage(primaryStage, fieldScene);
        fieldController.setMainStage(primaryStage, pickerScene);

        pickerController.init();

        fieldController.setPickerController(pickerController);
        pickerController.setFieldController(fieldController);

        primaryStage.setScene(pickerScene);
        primaryStage.setTitle(prop.getProperty("windowTitle"));
        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
