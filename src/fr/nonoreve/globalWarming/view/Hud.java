package fr.nonoreve.globalWarming.view;

import fr.nonoreve.globalWarming.FXController;
import fr.nonoreve.globalWarming.model.DataLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Hud extends Group {
    private final double HEIGHT = 32, WIDTH = 32;
    private final double INTERVALS = 0.1, OFFSET = 1.01;
    private final double MIDDLE = 10 * HEIGHT / 2;
    private static final NumberFormat formatter = new DecimalFormat("#0.000");

    private Polygon marker; // TODO display the value of the current selected zone (not implemented)
    private double height; // size of the panel
    private Label minL;
    private Label maxL;

    public Hud() {
        minL = new Label();
        maxL = new Label();
        minL.setTextFill(FXController.UI_COLOR);
        maxL.setTextFill(FXController.UI_COLOR);
        minL.setTranslateY(500);
        maxL.setTranslateY(110);
        height = 600.0 / 2;
        try {
            for (double i = 0; i < 1.0; i += INTERVALS) {
                Rectangle rec = new Rectangle();
                rec.setX(5);
                rec.setY(height - HEIGHT * (10 * i * OFFSET) + MIDDLE);
                rec.setHeight(HEIGHT);
                rec.setWidth(WIDTH + (i * 20));
                Color c = Graph3D.percentageColor(i);
                rec.setFill(new Color(c.getRed(), c.getGreen(), c.getBlue(), 1.0));
                this.getChildren().add(rec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        marker = new Polygon();
        marker.getPoints().addAll(0.0, 0.0,
                0.0, 20.0,
                20.0, 10.0);
        marker.setFill(FXController.UI_COLOR);
//        this.getChildren().add(marker);
        this.getChildren().addAll(minL, maxL);
    }

    public void update(double minValue, double maxValue) {
        minL.setText(String.valueOf(formatter.format(minValue)));
        maxL.setText(String.valueOf(formatter.format(maxValue)));
    }
}
