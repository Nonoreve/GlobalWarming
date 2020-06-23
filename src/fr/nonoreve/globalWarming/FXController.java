package fr.nonoreve.globalWarming;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import fr.nonoreve.globalWarming.model.DataLoader;
import fr.nonoreve.globalWarming.view.CameraManager;
import fr.nonoreve.globalWarming.view.Graph3D;
import fr.nonoreve.globalWarming.view.Hud;
import fr.nonoreve.globalWarming.view.ToggleSwitch;

import java.net.URL;
import java.util.ResourceBundle;

public class FXController implements Initializable {

    @FXML
    private Pane pane3D;

    @FXML
    private Pane gui;

    @FXML
    private VBox leftBox;

    @FXML
    private Slider yearsSelector;

    @FXML
    private Label yearsLabel;

    public static Color UI_COLOR = new Color(0.63, 0.95, 0.1, 0.99);
    public static final short initYear = (short) ((DataLoader.lastYear - DataLoader.firstYear) / 2 + DataLoader.firstYear);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Boolean defaultGraphMode = false;
        Hud hud = new Hud();
        Graph3D root3D = new Graph3D(defaultGraphMode, yearsLabel, hud);

        yearsSelector.setMin(DataLoader.firstYear);
        yearsSelector.setMax(DataLoader.lastYear);
        yearsSelector.valueProperty().addListener(root3D);
        yearsSelector.setValue(initYear);

        ToggleSwitch modeSelector = new ToggleSwitch(root3D, defaultGraphMode);
        leftBox.getChildren().add(modeSelector);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        SubScene subscene = new SubScene(root3D, 600, 600, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(camera);
        subscene.setFill(Color.BLACK);
        pane3D.getChildren().addAll(subscene);
        CameraManager cameraManager = new CameraManager(camera, pane3D, root3D);

        SubScene guiScene = new SubScene(hud, 100, 600, true, SceneAntialiasing.BALANCED);
        guiScene.setFill(Color.BLACK);
        gui.getChildren().addAll(guiScene);

    }
}
