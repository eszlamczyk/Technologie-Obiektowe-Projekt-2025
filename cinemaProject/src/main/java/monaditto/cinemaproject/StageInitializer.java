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
    private final Resource loginFxml;

    private final Resource registrationFxml;

    private final Resource adminPanelFxml;

    private final String applicationTitle;

    private final ApplicationContext applicationContext;

    private Stage stage;

    public StageInitializer(@Value("classpath:/fxml/Login.fxml") Resource loginFxml,
                            @Value("classpath:/fxml/Registration.fxml") Resource registrationFxml,
                            @Value("classpath:/fxml/AdminPanel.fxml") Resource adminPanelFxml,
                            @Value("${spring.application.ui.title}") String applicationTitle,
                            ApplicationContext applicationContext) {
        this.loginFxml = loginFxml;
        this.registrationFxml = registrationFxml;
        this.adminPanelFxml = adminPanelFxml;
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
        this.stage = null;
    }

    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try{
            this.stage = event.getStage();
            loadLoginScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadLoginScene() throws IOException {
        loadScene(this.loginFxml);
    }

    public void loadRegistrationScene() throws IOException {
        loadScene(this.registrationFxml);
    }

    public void loadAdminPanelScene() throws IOException {
        loadScene(this.adminPanelFxml);
    }

    private void loadScene(Resource fxml) throws IOException {
        URL url = fxml.getURL();
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.setTitle(this.applicationTitle);
        this.stage.sizeToScene();
        this.stage.show();
    }
}
