package monaditto.cinemaproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import monaditto.cinemaproject.JavafxApplication.StageReadyEvent;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    private final Resource fxml;
    private final String applicationTitle;
    private final ApplicationContext applicationContext;

    public StageInitializer(@Value("classpath:/fxml/Login.fxml") Resource fxml,
                            @Value("${spring.application.ui.title}") String applicationTitle,
                            ApplicationContext applicationContext) {
        this.fxml = fxml;
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try{
            Stage stage = event.getStage();
            URL url = this.fxml.getURL();
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.setTitle(this.applicationTitle);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
