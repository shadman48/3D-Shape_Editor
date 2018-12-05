import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ShapeEditor extends Application {
  private Stage primaryStage;
  private final FileChooser.ExtensionFilter EXTENSION_FILTER = new FileChooser.ExtensionFilter("csv", "*.csv", "*.*");

  public static void main(String[] args) {
    launch(args);
  }

  /** Create save button that will save the sceen to a file */
  public MenuItem saveMenuItem() {
    MenuItem item = new MenuItem("Save");
    item.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
    item.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialFileName("shapes_editor.csv");
      fileChooser.setSelectedExtensionFilter(EXTENSION_FILTER);
      File file = fileChooser.showSaveDialog(primaryStage);
      // todo get the file and write to it
      System.out.println(file);
    });
    return item;
  }

  /** Will open the file and generate the shapes on the screen */
  public MenuItem openMenuItem() {
    MenuItem item = new MenuItem("Open");
    item.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    item.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setSelectedExtensionFilter(EXTENSION_FILTER);
      File file = fileChooser.showOpenDialog(primaryStage);
      // todo read the file and add the shapes
      System.out.println(file);
    });
    return item;
  }

  /** Will open the file and generate the shapes on the screen */
  public MenuItem closeMenuItem() {
    MenuItem item = new MenuItem("Quit");
    item.setOnAction(event -> {
      System.exit(0);
    });
    return item;
  }

  public MenuBar header() {
    MenuBar menuBar = new MenuBar();
    // File Menu
    Menu file = new Menu("File");
    file.getItems().addAll(saveMenuItem(), openMenuItem(), new SeparatorMenuItem(), closeMenuItem());
    menuBar.getMenus().add(file);

    return menuBar;
  }

  public VBox footer() {
    VBox vbox = new VBox(new Button("Add Shape"));
    vbox.setPadding(new Insets(16));
    vbox.setAlignment(Pos.CENTER);
    return vbox;
  }

  public SubScene editor() {
    Group shapesGroup = new Group();
    SubScene shapesSub = new SubScene(shapesGroup, 340, 340,true, SceneAntialiasing.DISABLED);
    return shapesSub;
  }

  public Scene scene() {
    BorderPane pane = new BorderPane();
    pane.setTop(header());
    pane.setCenter(editor());
    pane.setBottom(footer());
    return new Scene(pane);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setScene(scene());
    primaryStage.setTitle("Shape Editor");
    primaryStage.show();
    // Only set the window to grow not shrink
    primaryStage.setMinWidth(primaryStage.getWidth());
    primaryStage.setMinHeight(primaryStage.getHeight());

  }
}
