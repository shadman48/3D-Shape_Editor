import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage)
    {
        Label xPos = new Label("X: ");
        Label xLabel = new Label("0.0");
        xLabel.setStyle( "-fx-border-style: solid; -fx-alignment: center");
        xLabel.setPrefWidth( 50.0);
        HBox xHbox = new HBox(10, xPos, xLabel);
        xHbox.setAlignment(Pos. CENTER);

        Label yPos = new Label("Y: ");
        Label yLabel = new Label("0.0");
        yLabel.setStyle( "-fx-border-style: solid; -fx-alignment: center");
        yLabel.setPrefWidth( 50.0);
        HBox yHbox = new HBox(10, yPos, yLabel);
        yHbox.setAlignment(Pos. CENTER);

        Label zPos = new Label("Z: ");
        Label zLabel = new Label("0.0");
        zLabel.setStyle( "-fx-border-style: solid; -fx-alignment: center");
        zLabel.setPrefWidth( 50.0);
        HBox zHbox = new HBox(10, zPos, zLabel);
        zHbox.setAlignment(Pos. CENTER);




        Slider slider1 = new Slider(-100, 100, 0.0);
        slider1.setShowgitTickMarks( true);
        slider1.setShowTickLabels( true);
        slider1.setPrefWidth( 300.0);

        Slider slider2 = new Slider(-100, 100, 0.0);
        slider2.setShowTickMarks( true);
        slider2.setShowTickLabels( true);
        slider2.setPrefWidth( 300.0);

        Slider slider3 = new Slider(-100, 100, 0.0);
        slider3.setShowTickMarks( true);
        slider3.setShowTickLabels( true);
        slider3.setPrefWidth( 300.0);




        //Sub Scene goes here
        //"add shape" button here
        Group shapesGroup = new Group(new Cylinder(5, 20));
        SubScene shapesSub = new SubScene(shapesGroup, 340, 340,true, SceneAntialiasing.DISABLED);
        shapesSub.setFill(Color.CYAN);

        PerspectiveCamera pCamera = new PerspectiveCamera(true);
        pCamera.getTransforms().addAll(new Translate(0, 0, -100));
        shapesSub.setCamera(pCamera);



        VBox vBox = new VBox(10, shapesSub, xHbox, yHbox, zHbox, slider1, slider2, slider3);
        vBox.setPadding( new Insets(15));
        primaryStage.setScene( new Scene(vBox));
        primaryStage.show();


        ChangeListener<Number> bob = (observable, oldvalue, newvalue) -> {
            double x = slider1.getValue() / 10;
            double y = slider2.getValue() / 10;
            double z = slider3.getValue() / 10;
            xLabel.setText(String. format("%.1f", x));
            yLabel.setText(String. format("%.1f", y));
            zLabel.setText(String. format("%.1f", z));
            pCamera.getTransforms().clear();
            pCamera.getTransforms().addAll(new Translate(0, 0, -100));
            pCamera.getTransforms().addAll(new Rotate(-x, Rotate.Y_AXIS));
            pCamera.getTransforms().addAll(new Rotate(-y, Rotate.X_AXIS));
            pCamera.getTransforms().addAll(new Translate(0, 0, z));

        };
        slider1.valueProperty().addListener(bob);
        slider2.valueProperty().addListener(bob);
        slider3.valueProperty().addListener(bob);

        Cylinder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Clicked!", ButtonType.FINISH);
                alert.showAndWait();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
