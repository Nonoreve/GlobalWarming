package fr.nonoreve.globalWarming.view;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;

public class CameraManager {

    private static final double CAMERA_MIN_DISTANCE = -1.1;
    private static final double CAMERA_INITIAL_DISTANCE = -5;
    private static final double CAMERA_INITIAL_X_ANGLE = -20.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 120.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double CONTROL_MULTIPLIER = 0.2;
    private static final double SHIFT_MULTIPLIER = 5.0;
    private static final double SCROLL_SPEED = 0.005;
    private static final double ROTATION_SPEED = 0.2;

    private final Group cameraXform = new Group();
    private final Rotate rx = new Rotate();
    private final Rotate ry = new Rotate();
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseModifier = 1.0;
    private double velocity = 1.0; // TODO motion rolling system

    private final Camera camera;

    public CameraManager(Camera cam, Node mainRoot, Group root) {

        camera = cam;

        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(camera);

        rx.setAxis(Rotate.X_AXIS);
        ry.setAxis(Rotate.Y_AXIS);
        cameraXform.getTransforms().addAll(ry, rx);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        rx.setAngle(CAMERA_INITIAL_X_ANGLE);

        // Add keyboard and mouse handler
        handleKeyboard(mainRoot, root);
        handleMouse(mainRoot, root);
    }

    private void handleMouse(Node mainRoot, final Node root) {

        mainRoot.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();

                // Set focus on the mainRoot to be able to detect key press
                mainRoot.requestFocus();
            }
        });
        mainRoot.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                double mouseDeltaX = (mousePosX - mouseOldX);
                double mouseDeltaY = (mousePosY - mouseOldY);
                if (me.isPrimaryButtonDown()) {
                    ry.setAngle(ry.getAngle() + mouseDeltaX * mouseModifier * ROTATION_SPEED);
                    rx.setAngle(rx.getAngle() - mouseDeltaY * mouseModifier * ROTATION_SPEED);
                }
            }
        });
        mainRoot.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double z = camera.getTranslateZ();
                double newZ = z + event.getDeltaY() * SCROLL_SPEED;
                if (newZ > CAMERA_MIN_DISTANCE) newZ = CAMERA_MIN_DISTANCE;
                camera.setTranslateZ(newZ);
            }
        });
    }

    private void handleKeyboard(Node mainRoot, final Node root) {
        mainRoot.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                mouseModifier = 1.0;
                switch (event.getCode()) {
                    case ALT:
                        cameraXform.setTranslateX(0.0);
                        cameraXform.setTranslateY(0.0);
                        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                        ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                    case CONTROL:
                        mouseModifier = CONTROL_MULTIPLIER;
                        break;
                    case SHIFT:
                        mouseModifier = SHIFT_MULTIPLIER;
                        break;
                    default:

                }
            }
        });
    }

}
