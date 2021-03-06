package su.sirus.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SirusLauncherApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent root;
    private Stage primaryStage;

    @Override
    public void init() throws Exception
    {
        springContext = SpringApplication.run(SirusLauncherApplication.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(springContext.getEnvironment().getProperty("app.name"));

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("app.css");

        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    @Override
    public void stop() throws Exception
    {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(SirusLauncherApplication.class, args);
    }
}
