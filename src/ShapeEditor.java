import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ShapeEditor extends Application {
  public static String[] COLORS = new String[]{ "#334455", "#445566", "#556677", "#667788", "#778899", "#ffffff", "#666666", "#999999", "#000000" };
  public static ShapeEditor shapeEditor;
  private Stage primaryStage;
  private final FileChooser.ExtensionFilter EXTENSION_FILTER = new FileChooser.ExtensionFilter("csv", "*.csv", "*.*");
  private ShapesDataFile shapes = new ShapesDataFile();
  private Group shapesGroup = new Group(new Group(new Sphere(6)));
  private SubScene shapesSub = new SubScene(shapesGroup, 800, 600,true, SceneAntialiasing.DISABLED);
  private BorderPane sidebar = new BorderPane();
  private ShapeData currentShape;
  private Slider slider1, slider2, slider3, slider4;
  private ComboBox<Label> menuItems;
  private BorderPane pane = new BorderPane();
  {
    // just add the camera
    PerspectiveCamera pCamera = new PerspectiveCamera(true);
    pCamera.getTransforms().addAll(new Translate(0, 0, -100));
    shapesSub.setCamera(pCamera);
  }

  public static void main(String[] args) {
    launch(args);
  }

  /** The shape save file */
  public class ShapesDataFile {
    private String backgroundColor = "#999";
    private List<ShapeData> shapesData = new ArrayList<>();

    /** This method will add the shape to the virtual data file */
    public void addShape(ShapeData shape) {
      Objects.requireNonNull(shape, "Shape should not be null");
      shapesData.add(shape);
    }

    /** Given the file read the file and populate this class with the data from the file */
    public void readFile(File file) {
      shapesData.clear(); // clear out this instance
      Objects.requireNonNull(file, "The file is required for this method");
      try (FileReader fileReader = new FileReader(file)) {
        Scanner scanner = new Scanner(fileReader);
        // first line is the background color
        backgroundColor = scanner.nextLine();
        // input the shapes
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] elements = line.split(",");
          String shape = elements[0];
          //System.out.println(line + " : " + shape);
          if ("sphere".equals(shape)) {
            System.out.println("Found sphere");
            // sphere order, radius, height, other
            shapesData.add(ShapeData.sphere(
              Double.parseDouble(elements[1]),
              Double.parseDouble(elements[2]),
              Double.parseDouble(elements[3]),
              Double.parseDouble(elements[4]),
              Double.parseDouble(elements[5]),
              elements[6]
            ));
          }
          else if ("cylinder".equals(shape)) {
            System.out.println("Found cylinder");
            shapesData.add(ShapeData.cylinder(
              Double.parseDouble(elements[1]),
              Double.parseDouble(elements[2]),
              Double.parseDouble(elements[3]),
              Double.parseDouble(elements[4]),
              Double.parseDouble(elements[5]),
              Double.parseDouble(elements[6]),
              elements[7]
            ));
          }
          else if ("box".equals(shape)) {
            System.out.println("Found box");
            shapesData.add(ShapeData.box(
              Double.parseDouble(elements[1]),
              Double.parseDouble(elements[2]),
              Double.parseDouble(elements[3]),
              Double.parseDouble(elements[4]),
              Double.parseDouble(elements[5]),
              Double.parseDouble(elements[6]),
              Double.parseDouble(elements[7]),
              elements[8]
            ));
          }
        }
      } catch (IOException error) {
        System.out.println("There is something wrong with reading the file");
      }
    }

    /** Given the file object write the contents of the save file object to the file */
    public void writeFile(File file) {
      Objects.requireNonNull(file, "The file is required for this method");
      try (FileWriter fileWriter = new FileWriter(file)) {
        fileWriter.write(toString());
      } catch (IOException error) {
        System.out.println("There is something wrong with writing the file");
      }
    }

    /** This will create the entire csv file for the shapes */
    @Override
    public String toString() {
      return String.format("%s\n%s", backgroundColor, shapesData.stream().map(ShapeData::toString).collect(Collectors.joining("\n")));
    }
  }

  /** Contains the info for the shape */
  public static class ShapeData {
    private String shape;
    // sphere
    private double radius;
    // box
    private double depth;
    // cylinder
    private double width;
    private double height;
    // location
    // location
    private double x;
    private double y;
    // rotate
    private double rotate;
    // scale
    private double scale;
    // color
    private String color;

    /** Populate the shape data with the arbitrarily data stuff */
    private static ShapeData populate(ShapeData data, double x, double y, double rotate, double scale, String color) {
      data.x = x;
      data.y = y;
      data.rotate = rotate;
      data.scale = scale;
      data.color = color;
      return data;
    }

    /** Return the newley created sphere object */
    public static ShapeData sphere(double radius, double x, double y, double rotate, double scale, String color) {
      ShapeData data = new ShapeData();
      data.shape = "sphere";
      data.radius = radius;
      return populate(data, x, y, rotate, scale, color);
    }

    /** Return the newley created cylinder object */
    public static ShapeData cylinder(double radius, double height, double x, double y, double rotate, double scale, String color) {
      ShapeData data = new ShapeData();
      data.shape = "cylinder";
      data.radius = radius;
      data.height = height;
      return populate(data, x, y, rotate, scale, color);
    }

    /** Return the newley created box object */
    public static ShapeData box(double width, double height, double depth, double x, double y, double rotate, double scale, String color) {
      ShapeData data = new ShapeData();
      data.shape = "box";
      data.width = width;
      data.height = height;
      data.depth = depth;
      return populate(data, x, y, rotate, scale, color);
    }

    /** If this shape is a sphere return the new sphere */
    public Shape3D getShape() {
      // Return the sphere
      if ("sphere".equals(shape)) {
        Sphere sphere = new Sphere(radius);
        sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        sphere.setScaleX(scale);
        sphere.setScaleY(scale);
        sphere.setRotate(rotate);
        sphere.setMaterial(new PhongMaterial(Color.web(color)));
        return shapeEditor.attachShapeEventHandler(this, sphere);
      }
      else if ("cylinder".equals(shape)) {
        Cylinder cylinder = new Cylinder(radius, height);
        cylinder.setTranslateX(x);
        cylinder.setTranslateY(y);
        cylinder.setScaleX(scale);
        cylinder.setScaleY(scale);
        cylinder.setRotate(rotate);
        cylinder.setMaterial(new PhongMaterial(Color.web(color)));
        return shapeEditor.attachShapeEventHandler(this, cylinder);
      }
      else if ("box".equals(shape)) {
        Box box = new Box(width, height, depth);
        box.setTranslateX(x);
        box.setTranslateY(y);
        box.setScaleX(scale);
        box.setScaleY(scale);
        box.setRotate(rotate);
        box.setMaterial(new PhongMaterial(Color.web(color)));
        return shapeEditor.attachShapeEventHandler(this, box);
      }
      // fail safe
      return null;
    }

    /** Will return the string of the serlized version of the shape */
    @Override
    public String toString() {
      if ("sphere".equals(shape)) {
        return String.format("%s,%f,%f,%f,%f,%f,%s", shape, radius, x, y, rotate, scale, color);
      }
      else if ("cylinder".equals(shape)) {
        return String.format("%s,%f,%f,%f,%f,%f,%f,%s", shape, radius, height, x, y, rotate, scale, color);
      }
      else if ("box".equals(shape)) {
        return String.format("%s,%f,%f,%f,%f,%f,%f,%f,%s", shape, width, height, depth, x, y, rotate, scale, color);
      }
      return null;
    }
  }

  /** This will attach the shape */
  public <T extends Shape3D> T attachShapeEventHandler(final ShapeData data, T shape) {
    shape.setOnMouseClicked(event -> {
      currentShape = data;
      sidebar.setDisable(false);
      System.out.println("Selected the shape");
      slider1.setValue(currentShape.x);
      slider2.setValue(currentShape.y);
      slider3.setValue(currentShape.scale);
      slider4.setValue(currentShape.rotate);
      Label foundLabel = new Label("#fff");
      for (Label label : menuItems.getItems()) {
        if (label.getText().equals(currentShape.color)) {
          foundLabel = label;
        }
      }
      menuItems.setValue(foundLabel);
    });
    return shape;
  }

  /** This will clear the shapes group and add the shapes and background color the the sub sceen */
  public void addShapesAndBackground() {
    shapesGroup.getChildren().clear();
    shapesSub.setFill(Paint.valueOf(shapes.backgroundColor));
    for (ShapeData shapeData : shapes.shapesData) {
      Shape3D shape = shapeData.getShape();
      if (shape != null) {
        shapesGroup.getChildren().add(shape);
      }
    }
  }

  /** Create save button that will save the sceen to a file */
  public MenuItem newMenuItem() {
    MenuItem item = new MenuItem("New");
    item.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
    item.setOnAction(event -> {
      shapes.backgroundColor = "#999";
      shapes.shapesData.clear();
      addShapesAndBackground();
    });
    return item;
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
      if (file != null) {
        System.out.println("Writing to the file: " + file);
        shapes.writeFile(file);
      }
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
      if (file != null) {
        try {
          System.out.println("Reading from the file: " + file);
          shapes.readFile(file);
          addShapesAndBackground();
        } catch (Exception error) {
          Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage());
          alert.showAndWait();
        }
      }
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

  /** Create a menu that lets you change the background color */
  public MenuItem[] colorsMenuItems() {
    MenuItem[] menuItems = new MenuItem[COLORS.length];
    for (int i = 0; i < COLORS.length ; i++) {
      String color = COLORS[i];
      MenuItem tmp = menuItems[i] = new MenuItem(color);
      tmp.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #00000000");
      tmp.setOnAction(event -> {
        shapesSub.setFill(Paint.valueOf(color));
        shapes.backgroundColor = color;
      });
    }
    return menuItems;
  }

  /** This is the sidebar that will edit the currently selected shape on the screen */
  public Node sidebar() {
    // Display Labels
    Label xPos = new Label("X: ");
    Label xLabel = new Label("0.0");
    xLabel.setStyle( "-fx-border-style: solid; -fx-alignment: center");
    xLabel.setPrefWidth(50.0);
    HBox xHbox = new HBox(10, xPos, xLabel);
    xHbox.setAlignment(Pos.CENTER);
    //
    Label yPos = new Label("Y: ");
    Label yLabel = new Label("0.0");
    yLabel.setStyle("-fx-border-style: solid; -fx-alignment: center");
    yLabel.setPrefWidth(50.0);
    HBox yHbox = new HBox(10, yPos, yLabel);
    yHbox.setAlignment(Pos.CENTER);
    //
    Label zPos = new Label("Scale: ");
    Label zLabel = new Label("0.0");
    zLabel.setStyle("-fx-border-style: solid; -fx-alignment: center");
    zLabel.setPrefWidth(50.0);
    HBox zHbox = new HBox(10, zPos, zLabel);
    zHbox.setAlignment(Pos.CENTER);
    //
    Label rPos = new Label("Rotate: ");
    Label rLabel = new Label("0.0");
    zLabel.setStyle("-fx-border-style: solid; -fx-alignment: center");
    zLabel.setPrefWidth(50.0);
    HBox rHbox = new HBox(10, rPos, rLabel);
    rHbox.setAlignment(Pos.CENTER);
    // editor controls
    slider1 = new Slider(-100, 100, 0);
    slider1.setShowTickMarks(true);
    slider1.setShowTickLabels(true);
    //
    slider2 = new Slider(-100, 100, 0);
    slider2.setShowTickMarks(true);
    slider2.setShowTickLabels(true);
    //
    slider3 = new Slider(0, 10, 0);
    slider3.setShowTickMarks(true);
    slider3.setShowTickLabels(true);
    //
    slider4 = new Slider(0, 360, 0);
    slider4.setShowTickMarks(true);
    slider4.setShowTickLabels(true);
    //
    menuItems = new ComboBox<>();
    menuItems.setCellFactory(listView -> new ListCell<Label>() {
      @Override
      public void updateItem(Label item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          this.setStyle("-fx-background-color: " + item.getText() + "; -fx-text-fill: #00000000");
        }
      }
    });
    for (int i = 0; i < COLORS.length ; i++) {
      String color = COLORS[i];
      Label colorLabel = new Label(color);
      colorLabel.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #00000000");
      menuItems.getItems().add(colorLabel);
    }
    menuItems.setValue(menuItems.getItems().get(menuItems.getItems().size() - 1));
    //
    // Add the listeners
    slider1.valueProperty().addListener((observable, oldvalue, newvalue) -> {
      double x = slider1.getValue();
      xLabel.setText(String. format("%.1f", x));
      currentShape.x = x;
      addShapesAndBackground();
    });
    slider2.valueProperty().addListener((observable, oldvalue, newvalue) -> {
      double y = slider2.getValue();
      yLabel.setText(String. format("%.1f", y));
      currentShape.y = y;
      addShapesAndBackground();
    });
    slider3.valueProperty().addListener((observable, oldvalue, newvalue) -> {
      double s = slider3.getValue();
      zLabel.setText(String. format("%.1f", s));
      currentShape.scale = s;
      addShapesAndBackground();
    });
    slider4.valueProperty().addListener((observable, oldvalue, newvalue) -> {
      double r = slider4.getValue();
      rLabel.setText(String. format("%.1f", r));
      currentShape.rotate = r;
      addShapesAndBackground();
    });
    menuItems.valueProperty().addListener((observable, oldvalue, newvalue) -> {
      currentShape.color = menuItems.getValue().getText();
      addShapesAndBackground();
    });
    // Add it to the main vbox
    VBox vbox = new VBox(20, menuItems, xHbox, slider1, yHbox, slider2, zHbox, slider3, rHbox, slider4);
    vbox.setPadding(new Insets(20));
    // toolbox
    Label toolbox = new Label("Toolbox");
    toolbox.setAlignment(Pos.CENTER);
    toolbox.setPadding(new Insets(20));
    // borderpane
    sidebar.setMinWidth(300);
    sidebar.setDisable(true);
    sidebar.setTop(toolbox);
    sidebar.setCenter(vbox);
    return sidebar;
  }

  /** The button that will add the shape to the subsceen */
  public MenuItem[] addShapeMenuItems() {
    MenuItem[] menuItems = new MenuItem[3];
    MenuItem sphere = menuItems[0] = new MenuItem("Sphere");
    sphere.setOnAction(event -> {
      System.out.println("Adding sphere");
      pane.setBottom(addSphereNode());
    });
    MenuItem cylinder = menuItems[1] = new MenuItem("Cylinder");
    cylinder.setOnAction(event -> {
      System.out.println("Adding cylinder");
      pane.setBottom(addCylinderNode());
    });
    MenuItem box = menuItems[2] = new MenuItem("Box");
    box.setOnAction(event -> {
      System.out.println("Adding box");
      pane.setBottom(addBoxNode());
    });
    return menuItems;
  }

  /** Add the sphere node that will add a default shape to the scene */
  public Node addSphereNode() {
    TextField field = new TextField("5");
    Label radiusLabel = new Label("Radius: ");
    radiusLabel.setPadding(new Insets(5, 0, 0, 0));
    radiusLabel.setLabelFor(field);
    Button add = new Button("Add Sphere");
    add.setOnAction(event -> {
      try {
        shapes.addShape(ShapeData.sphere(Double.parseDouble(field.getText()), 0, 0, 0, 1, COLORS[COLORS.length - 1]));
        addShapesAndBackground();
      } catch (Exception error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage());
        alert.showAndWait();
      }
    });
    HBox hbox = new HBox(20, radiusLabel, field, add);
    hbox.setPadding(new Insets(20));
    hbox.setAlignment(Pos.CENTER);
    return hbox;
  }

  /** Add the box node that will add a default shape to the scene */
  public Node addBoxNode() {
    TextField heightField = new TextField("1");
    Label heightLabel = new Label("Height: ");
    heightLabel.setPadding(new Insets(5, 0, 0, 0));
    heightLabel.setLabelFor(heightField);
    TextField widthField = new TextField("1");
    Label widthLabel = new Label("Width: ");
    widthLabel.setPadding(new Insets(5, 0, 0, 0));
    widthLabel.setLabelFor(widthField);
    TextField depthField = new TextField("1");
    Label depthLabel = new Label("Depth: ");
    depthLabel.setPadding(new Insets(5, 0, 0, 0));
    depthLabel.setLabelFor(depthField);
    Button add = new Button("Add Box");
    add.setOnAction(event -> {
      try {
        shapes.addShape(ShapeData.box(Double.parseDouble(widthField.getText()), Double.parseDouble(heightField.getText()), Double.parseDouble(depthField.getText()), 0, 0, 0, 1, COLORS[COLORS.length - 1]));
        addShapesAndBackground();
      } catch (Exception error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage());
        alert.showAndWait();
      }
    });
    HBox hbox = new HBox(20, heightLabel, heightField, widthLabel, widthField, depthLabel, depthField, add);
    hbox.setAlignment(Pos.CENTER);
    hbox.setPadding(new Insets(20));
    return hbox;
  }

  /** Add the cylinder node that will add a default shape to the scene */
  public Node addCylinderNode() {
    TextField heightField = new TextField("20");
    Label heightLabel = new Label("Height: ");
    heightLabel.setPadding(new Insets(5, 0, 0, 0));
    heightLabel.setLabelFor(heightField);
    TextField widthField = new TextField("5");
    Label widthLabel = new Label("Width: ");
    widthLabel.setPadding(new Insets(5, 0, 0, 0));
    widthLabel.setLabelFor(widthField);
    Button add = new Button("Add Cylinder");
    add.setOnAction(event -> {
      try {
        shapes.addShape(ShapeData.cylinder(Double.parseDouble(widthField.getText()), Double.parseDouble(heightField.getText()), 0, 0, 0, 1, COLORS[COLORS.length - 1]));
        addShapesAndBackground();
      } catch (Exception error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, error.getMessage());
        alert.showAndWait();
      }
    });
    HBox hbox = new HBox(20, heightLabel, heightField, widthLabel, widthField, add);
    hbox.setAlignment(Pos.CENTER);
    hbox.setPadding(new Insets(20));
    return hbox;
  }

  /** This is the meu bar that contains stuff */
  public Node header() {
    // File Menu
    Menu file = new Menu("File");
    file.getItems().addAll(newMenuItem(), saveMenuItem(), openMenuItem(), new SeparatorMenuItem(), closeMenuItem());
    // background
    Menu background = new Menu("Background");
    background.getItems().addAll(colorsMenuItems());
    // add shape
    Menu addShape = new Menu("Add Shape");
    addShape.getItems().addAll(addShapeMenuItems());
    // menu bar
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(file, background, addShape);
    return menuBar;
  }

  /** This is the footer that will let you add a shape */
  public Node footer() {
    VBox vbox = new VBox(new Label("Add a shape from the menu bar"));
    vbox.setPadding(new Insets(25));
    vbox.setAlignment(Pos.CENTER);
    return vbox;
  }

  public Node editor() {
    return shapesSub;
  }

  public Scene scene() {
    pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#efefef"), new CornerRadii(0), new Insets(0))));
    pane.setTop(header());
    pane.setCenter(editor());
    pane.setRight(sidebar());
    pane.setBottom(footer());
    return new Scene(pane);
  }

  @Override
  public void start(Stage primaryStage) {
    shapeEditor = this;
    this.primaryStage = primaryStage;
    primaryStage.setScene(scene());
    primaryStage.setTitle("Shape Editor");
    primaryStage.show();
    // Only set the window to grow not shrink
    primaryStage.setMinWidth(primaryStage.getWidth());
    primaryStage.setMinHeight(primaryStage.getHeight());
    // Use the default save file as a template this is just the objects that are in memory
    addShapesAndBackground();
  }
}
