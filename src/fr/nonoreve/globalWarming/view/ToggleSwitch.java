package fr.nonoreve.globalWarming.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A custom toggle button that doesn't exist in javafx
 */
public class ToggleSwitch extends VBox {

    private final Label label = new Label();
    private final Button button = new Button();

    private Graph3D graph3D;
    private SimpleBooleanProperty switchedOn;

    public ToggleSwitch(Graph3D graph3D, Boolean defaultMode) {
        this.graph3D = graph3D;
        this.switchedOn = new SimpleBooleanProperty(defaultMode);
        if (defaultMode) {
            label.setStyle("-fx-graphic: url('/barGraph.png');");
            button.setStyle("-fx-graphic: url('/quadGraph.png');");
            button.toFront();
        } else {
            label.setStyle("-fx-graphic: url('/quadGraph.png');");
            button.setStyle("-fx-graphic: url('/barGraph.png');");
            label.toFront();
        }
        getChildren().addAll(label, button);
        button.setOnAction((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        label.setOnMouseClicked((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        setStyle();
        bindProperties();
        switchedOn.addListener((a, b, c) -> {
            if (c) {
                label.setStyle("-fx-graphic: url('/barGraph.png');");
                button.setStyle("-fx-graphic: url('/quadGraph.png');");
                button.toFront();
            } else {
                label.setStyle("-fx-graphic: url('/quadGraph.png');");
                button.setStyle("-fx-graphic: url('/barGraph.png');");
                label.toFront();
            }
            graph3D.redrawGraph(c);
        });
    }

    private void setStyle() {
        label.setAlignment(Pos.CENTER);
        setPadding(new Insets(200, 0, 200, 10));
        setAlignment(Pos.TOP_CENTER);
    }

    private void bindProperties() {
        label.prefWidthProperty().bind(widthProperty());
        label.prefHeightProperty().bind(heightProperty().divide(2));
        button.prefWidthProperty().bind(widthProperty());
        button.prefHeightProperty().bind(heightProperty().divide(2));
    }
}