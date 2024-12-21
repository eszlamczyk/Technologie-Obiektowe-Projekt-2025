package monaditto.cinemafront;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import monaditto.cinemafront.JavafxApplication.StageReadyEvent;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;


@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final String applicationTitle;

    private final ApplicationContext applicationContext;

    private Image icon;

    private Stage stage;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            ApplicationContext applicationContext) {
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
            loadStage(ControllerResource.LOGIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadStage(ControllerResource controllerResource) throws IOException {
        Parent root = getRoot(controllerResource.getResource());
        Scene scene = new Scene(root);

        loadStageIcon();
        configureScene(scene);
        this.stage.show();
    }

    private Parent getRoot(Resource fxml) throws IOException {
        URL url = fxml.getURL();
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(applicationContext::getBean);
        return loader.load();
    }

    private void configureScene(Scene scene) {
        this.stage.setScene(scene);
        double prefHeight = scene.getRoot().prefHeight(-1);
        double prefWidth = scene.getRoot().prefWidth(-1);
        this.stage.sizeToScene();
        this.stage.setMinHeight(prefHeight);
        this.stage.setHeight(prefHeight);
        this.stage.setMinWidth(prefWidth);
        this.stage.setWidth(prefWidth);
        this.stage.setTitle(this.applicationTitle);
    }

    private void loadStageIcon() {
        if (icon == null) {
            try {
                icon = new Image("agh_icon.png");
                this.stage.getIcons().add(icon);
            } catch(IllegalArgumentException e) {
                System.out.println("Cannot load the given icon");
            }
        } else {
            this.stage.getIcons().add(icon);
        }
    }

    public ApplicationContext getContext() {
        return applicationContext;
    }
}
