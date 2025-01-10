package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.CategoryClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;


@Controller
public class AdminCategoriesController {

    private final StageInitializer stageInitializer;

    private final BackendConfig backendConfig;

    private final AdminEditCategoryController adminEditCategoryController;

    private ObservableList<CategoryDto> categoryDtos;

    @Autowired
    private CategoryClientAPI categoryClientAPI;


    @FXML
    public AnchorPane rootPane;
    @FXML
    public Rectangle backgroundRectangle;
    @FXML
    public ListView<CategoryDto> categoriesListView;
    @FXML
    public Button editButton;
    @FXML
    public Button deleteButton;


    public AdminCategoriesController(StageInitializer stageInitializer,
                                     BackendConfig backendConfig,
                                     AdminEditCategoryController adminEditCategoryController) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
        this.adminEditCategoryController = adminEditCategoryController;
    }

    @FXML
    private void initialize(){
        initializeCategoryListView();
        initializeButtons();
        initializeResponsiveness();
        loadCategories();
    }

    private void initializeCategoryListView(){
        categoryDtos = FXCollections.observableArrayList();

        categoriesListView.setItems(categoryDtos);

        categoriesListView.setCellFactory(list -> new ListCell<CategoryDto>() {
            @Override
            protected void updateItem(CategoryDto categoryDto, boolean empty) {
                super.updateItem(categoryDto, empty);
                if (empty || categoryDto == null){
                    setText(null);
                } else{
                    setText(categoryDto.categoryName());
                }
            }
        });
    }

    private void initializeButtons() {
        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
                () -> categoriesListView.getSelectionModel().getSelectedItems().size() != 1,
                categoriesListView.getSelectionModel().getSelectedItems()
        );

        deleteButton.disableProperty().bind(isSingleCellSelected);
        editButton.disableProperty().bind(isSingleCellSelected);
    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    private void loadCategories(){
        categoryClientAPI.loadCategories()
                .thenAccept(categoryDtos::addAll);
    }

    @FXML
    public void handleAdd(ActionEvent actionEvent) {
        try{
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_CATEGORY);
            adminEditCategoryController.resetCategoryDto();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void handleEdit(ActionEvent actionEvent) {
        try{
            CategoryDto toEdit = categoriesListView.getSelectionModel().getSelectedItem();
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_CATEGORY);
            adminEditCategoryController.setCategoryDto(toEdit);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void handleDelete(ActionEvent actionEvent) {
        CategoryDto toDelete = categoriesListView.getSelectionModel().getSelectedItem();
        categoryClientAPI.deleteOneCategory(toDelete.id())
                .thenAccept(responseResult -> {
                    if (responseResult.statusCode() != 200){
                        System.err.println("Failed to delete the category, status code = " + responseResult.statusCode());
                        return;
                    }
                    Platform.runLater(() -> categoryDtos.remove(toDelete));
                });
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
