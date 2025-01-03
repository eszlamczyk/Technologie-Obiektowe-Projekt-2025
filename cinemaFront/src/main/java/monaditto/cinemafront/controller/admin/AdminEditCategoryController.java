package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.CategoryClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;

@Controller
public class AdminEditCategoryController {

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    @FXML
    public Label mainLabel;
    @FXML
    public TextField nameField;

    @FXML
    public Label statusLabel;
    public Button cancelButton;
    public Button saveButton;

    @Autowired
    private CategoryClientAPI categoryClientAPI;

    private CategoryDto categoryDto;


    public AdminEditCategoryController(StageInitializer stageInitializer, BackendConfig backendConfig){
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
    }

    public void setCategoryDto(CategoryDto categoryDto){
        mainLabel.setText("Edit Category");
        this.categoryDto = categoryDto;
        nameField.setText(categoryDto.categoryName());
    }

    public void resetCategoryDto(){
        mainLabel.setText("Add Category");
        this.categoryDto = null;
        nameField.clear();
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        if (this.categoryDto == null){
            createCategory();
        } else {
            editCategory();
        }
    }

    private void createCategory(){
        Optional<CategoryDto> categoryDto = createCategoryDto();

        if (categoryDto.isEmpty()) return;

        categoryClientAPI.createCategory(categoryDto.get())
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200) {
                        saveButton.setDisable(true);
                        handleAddSuccess();
                    }
                });
    }

    private void handleAddSuccess() {
        Platform.runLater(() -> {
            mainLabel.requestFocus();
            cancelButton.setText("Return");
        });
    }

    private void setStatusLabelText(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    private Optional<CategoryDto> createCategoryDto() {

        if (nameField.getText().isEmpty()){
            statusLabel.setText("Please provide a name for category");
            return Optional.empty();
        }

        CategoryDto categoryDto = new CategoryDto(nameField.getText());

        return Optional.of(categoryDto);
    }

    private void editCategory(){
        Optional<CategoryDto> newCategoryDto = createCategoryDto();
        if (newCategoryDto.isEmpty()) return;

        categoryClientAPI.editCategory(categoryDto.id(), newCategoryDto.get())
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200){
                        handleAddSuccess();
                    }
                });
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_CATEGORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
