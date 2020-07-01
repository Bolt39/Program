//Oskar Berggren osbe8976

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.*;

public class Program extends Application {
    private Stage primaryStage;
    private ImageView imageView = new ImageView();
    private Button newButton;
    private TextField searchField;
    private Button searchButton;
    private Button hideButton;
    private Button coordinatesButton;
    private Button removeButton;
    private Button hideCategoryButton;
    private RadioButton nameButton;
    private RadioButton describeButton;
    private Pane imagePane;
    private boolean changed;
    private ListView<Category> categoryListView;
    private Map<Position, Triangle> positionTriangleMap = new HashMap<>();
    private HashSet<Triangle> triangleHashSet = new HashSet<>();
    private Map<Category, Set<Triangle>> categorySetMap = new HashMap<>();
    private ArrayList<Triangle> markedTriangles = new ArrayList<>();
    private Map<String, Set<Triangle>> nameTriangleMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane root = new BorderPane();
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        createMenuItems(vbox);
        setUpHBoxTop(hbox);

        vbox.getChildren().addAll(hbox);
        root.setTop(vbox);

        imagePane = new Pane();
        root.setLeft(imagePane);

        VBox rightBox = new VBox();
        setUpListViewRight(rightBox);
        root.setRight(rightBox);

        imagePane.setOnMousePressed(new ImagePaneHandler());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new ExitHandler());
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void createMenuItems(VBox vbox) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadMap = new MenuItem("Load Picture");

        MenuItem loadPlaces = new MenuItem("Load Triangles");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        loadMap.setOnAction(new LoadHandler());
        save.setOnAction(new SaveHandler());
        loadPlaces.setOnAction(new LoadPlaceHandler());
        exit.setOnAction(new ExitItemHandler());

        vbox.getChildren().addAll(menuBar, imageView);
        menuBar.getMenus().add(fileMenu);
        fileMenu.getItems().addAll(loadMap, loadPlaces, save, exit);
    }

    private void setUpListViewRight(VBox right) {
        ObservableList<Category> categories = FXCollections.observableArrayList(new Category("Red", Color.RED), new Category("Blue", Color.BLUE), new Category("Green", Color.GREEN));
        categoryListView = new ListView(categories);
        Label header = new Label("Colors");
        hideCategoryButton = new Button("Hide Color");

        hideCategoryButton.setOnAction(new HideCategoryHandler());
        categoryListView.getSelectionModel().selectedItemProperty().addListener(new ListHandler());

        categoryListView.setPrefHeight(75);
        right.setAlignment(Pos.CENTER);
        right.getChildren().addAll(header, categoryListView, hideCategoryButton);
    }

    private void setUpHBoxTop(HBox top) {
        newButton = new Button("New");
        VBox place = new VBox();
        nameButton = new RadioButton("Named");
        describeButton = new RadioButton("Described");
        place.getChildren().addAll(nameButton, describeButton);
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(nameButton, describeButton);
        nameButton.setSelected(true);
        searchField = new TextField();
        searchField.setPromptText("Search");
        searchButton = new Button("Search");
        hideButton = new Button("Hide");
        removeButton = new Button("Remove");
        coordinatesButton = new Button("Coordinates");

        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setSpacing(5);
        top.getChildren().addAll(newButton, place, searchField, searchButton, hideButton, removeButton, coordinatesButton);

        searchButton.setOnAction(new SearchHandler());
        newButton.setOnAction(new NewHandler());
        hideButton.setOnAction(new HideHandler());
        coordinatesButton.setOnAction(new CoordinatesHandler());
        removeButton.setOnAction(new RemoveHandler());
    }

    private void unmarkPlaces() {
        for (Triangle p : markedTriangles) {
            p.setMarked(false);
        }
        markedTriangles.clear();
    }

    private void placeTriangle(Position pos, Category category, Triangle triangle) {
        Set<Triangle> nameTriangles = nameTriangleMap.get(triangle.getName());
        if (!nameTriangleMap.containsKey(triangle.getName())){
            nameTriangles = new HashSet<>();
        }
        nameTriangles.add(triangle);
        nameTriangleMap.put(triangle.getName(), nameTriangles);

        positionTriangleMap.put(pos, triangle);
        triangleHashSet.add(triangle);
        if (category != null) {
            Set<Triangle> triangles = categorySetMap.get(category);
            if (!categorySetMap.containsKey(category)) {
                triangles = new HashSet<>();
            }
            triangles.add(triangle);
            categorySetMap.put(category, triangles);
            triangle.setFill(category.getColor());

        }
        imagePane.getChildren().add(triangle);
    }

    private Category parseCategory(String str) {
        if (str.equals("Red"))
            return categoryListView.getItems().get(0);
        else if (str.equals("Blue"))
            return categoryListView.getItems().get(1);
        else if (str.equals("Green"))
            return categoryListView.getItems().get(2);
        else
            return null;
    }

    private void showAlert(String s1, String s2, String s3, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(s1);
        alert.setHeaderText(s2);
        alert.setContentText(s3);
        alert.showAndWait();
    }

    private void removeEverything(){
        imagePane.getChildren().removeAll(triangleHashSet);
        positionTriangleMap.clear();
        triangleHashSet.clear();
        categorySetMap.clear();
        markedTriangles.clear();
    }

    private class LoadHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changed){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved changes, load new picture anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL)
                    return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pictures (*.PNG, *.JPG, *.BMP)","*.PNG", "*.JPG", "*.BMP"));
            fileChooser.setTitle("Open Picture File");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file == null)
                return;
            String fileName = file.getAbsolutePath();
            Image image = new Image("File:" + fileName);
            imageView.setImage(image);
            if (imagePane != null){
                removeEverything();
                imagePane.getChildren().clear();
            }
            imagePane.getChildren().add(imageView);
            primaryStage.sizeToScene();
            changed = false;
        }
    }

    private class ListHandler implements ChangeListener<Category> {

        @Override
        public void changed(ObservableValue observableValue, Category old, Category next) {
            if (categorySetMap.get(next) == null)
                return;
            for (Triangle triangle : categorySetMap.get(next)) {
                triangle.setVisible(true);
            }
        }
    }

    private class NewHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            imageView.setOnMouseClicked(new ClickHandler());
            imageView.setCursor(Cursor.CROSSHAIR);
        }
    }

    private class HideHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            for (Triangle p : markedTriangles) {
                p.setVisible(false);
                p.setMarked(false);
            }
            markedTriangles.clear();
        }
    }

    private class HideCategoryHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Category category = categoryListView.getSelectionModel().getSelectedItem();
            for (Triangle triangle : categorySetMap.get(category)) {
                triangle.setVisible(false);
            }
        }
    }

    private class CoordinatesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                CoordinatesDialog dialog = new CoordinatesDialog();
                Optional<ButtonType> answer = dialog.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    Position position = new Position(dialog.getXfield(), dialog.getYfield());
                    if (positionTriangleMap.containsKey(position)){
                        unmarkPlaces();
                        Triangle triangle = positionTriangleMap.get(position);
                        triangle.setMarked(true);
                        triangle.setVisible(true);
                        markedTriangles.add(triangle);
                        return;
                    }
                    showAlert("Information", null, "Cannot find any place on that position", Alert.AlertType.INFORMATION);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", null, "Numberformat Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private class RemoveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            for (int i = 0; i < markedTriangles.size(); i++) {
                Triangle p = markedTriangles.get(i);
                Category category = p.getCategory();
                if (category != null) {
                    categorySetMap.get(category).remove(p);
                }
                positionTriangleMap.remove(p.getPosition());
                triangleHashSet.remove(p);
                p.setMarked(false);
                imagePane.getChildren().remove(p);
            }
            markedTriangles.clear();
            changed = true;
        }
    }


    private class SearchHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            String name = searchField.getText();
            unmarkPlaces();
            if (nameTriangleMap.containsKey(name)){
                for (Triangle p : nameTriangleMap.get(name)){
                    p.setVisible(true);
                    p.setMarked(true);
                    markedTriangles.add(p);
                }
            }
            else
                showAlert("Not found!", null, "Cannot find any place with that name", Alert.AlertType.INFORMATION);
        }
    }

    private class LoadPlaceHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                if (changed){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved changes, load new triangles anyway?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.CANCEL)
                        return;
                }
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text files (*.TXT)","*.TXT"));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file == null)
                    return;
                removeEverything();
                String fileName = file.getAbsolutePath();
                FileReader inFile = new FileReader(fileName);
                BufferedReader in = new BufferedReader(inFile);
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split(",");
                    String place = tokens[0];
                    Category category = parseCategory(tokens[1]);
                    Position position = new Position(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
                    String name = tokens[4];
                    if (place.equals("Named")) {
                        Triangle p = new NamedTriangle(name, category, position);
                        placeTriangle(position, category, p);
                    } else if (place.equals("Described")) {
                        Triangle p = new DescribedTriangle(name, category, position, tokens[5]);
                        placeTriangle(position, category, p);
                    }
                }
                changed = false;
            } catch (FileNotFoundException e) {
                showAlert("Error", null, "File cannot be opened: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (IOException e) {
                showAlert("Error", null, "IO-Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();
            Position pos = new Position(x, y);
            if (positionTriangleMap.containsKey(pos)){
                showAlert("Error", null, "Position already exists", Alert.AlertType.ERROR);
                return;
            }
            Category category = categoryListView.getSelectionModel().getSelectedItem();
            if (nameButton.isSelected()) {
                NamedTriangleDialog dialog1 = new NamedTriangleDialog();
                Optional<ButtonType> answer = dialog1.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    String name = dialog1.getNameField();
                    if (name.isEmpty()) {
                        showAlert("Error", null, "Name cannot be empty", Alert.AlertType.ERROR);
                    } else {
                        Triangle triangle = new NamedTriangle(name, category, pos);
                        placeTriangle(pos, category, triangle);
                    }
                }
            } else {
                DescribedTriangleDialog dialog2 = new DescribedTriangleDialog();
                Optional<ButtonType> answer = dialog2.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    String name = dialog2.getNameField();
                    String description = dialog2.getDescribedField();
                    if (name.isEmpty() || description.isEmpty()) {
                        showAlert("Error", null, "Name and description must be filled out", Alert.AlertType.ERROR);
                    } else {
                        Triangle triangle = new DescribedTriangle(name, category, pos, description);
                        placeTriangle(pos, category, triangle);
                    }
                }
            }
            imageView.setOnMouseClicked(null);
            categoryListView.getSelectionModel().clearSelection();
            imageView.setCursor(Cursor.DEFAULT);
            changed = true;
        }
    }

    private class ImagePaneHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                for (Triangle p : positionTriangleMap.values()) {
                    p.setOnMouseClicked(new MarkHandler());
                }
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                for (Triangle p : positionTriangleMap.values()) {
                    p.setOnMouseClicked(new InfoHandler());
                }

            }
        }
    }

    private class InfoHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            Triangle p = (Triangle) mouseEvent.getSource();
            if (p instanceof NamedTriangle) {
                InfoNamedDialog dialog = new InfoNamedDialog(p.getName(), p.getPosition());
                dialog.showAndWait();
            } else if (p instanceof DescribedTriangle) {
                InfoDescDialog dialog = new InfoDescDialog(p.getName(), p.getPosition(), ((DescribedTriangle) p).getDescription());
                dialog.showAndWait();
            }
        }
    }

    private class MarkHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Triangle p = (Triangle) event.getSource();
            if (p.getMarked()) {
                p.setMarked(false);
                return;
            }
            p.setMarked(true);
            markedTriangles.add(p);
        }
    }

    private class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose filename:");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Textfiles (*.TXT)","*.TXT"));
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file == null)
                    return;
                String fileName = file.getAbsolutePath();

                FileWriter outFile = new FileWriter(fileName);
                PrintWriter out = new PrintWriter(outFile);
                for (Triangle p : positionTriangleMap.values()) {
                    out.println(p);
                }
                out.close();
                outFile.close();
                changed = false;
            } catch (FileNotFoundException e) {
                showAlert("Error", null, "File cannot be saved: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (IOException e) {
                showAlert("Error", null, "IO-Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private class ExitItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    private class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved changes, do you still want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL)
                    event.consume();
            }
        }
    }
}
