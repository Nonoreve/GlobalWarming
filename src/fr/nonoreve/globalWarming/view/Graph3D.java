package fr.nonoreve.globalWarming.view;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import fr.nonoreve.globalWarming.FXController;
import fr.nonoreve.globalWarming.model.DataLoader;
import fr.nonoreve.globalWarming.model.ExtremesTemp;
import fr.nonoreve.globalWarming.model.GeoPosition;
import fr.nonoreve.globalWarming.model.LocalDatedAnomaly;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Graph3D extends Group implements ChangeListener<Number> {

    public static final NumberFormat formatter = new DecimalFormat("#0.000");
    private final double earthRadius = 1.0;
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    private static final double OVERLAY_ALPHA = 0.2;
    private static final double OVERLAY_RADIUS = 1.05;
    private static final Color minColor = new Color(0.05, 0.2, 0.69, 1.0);
    private static final Color maxColor = new Color(0.69, 0.2, 0.05, 1.0);

    private final Group earth;
    private final Group quads;
    private final Group bars;
    private final Label displayedYear;
    private final Boolean currentMode;
    private final Hud hud;
    private short currentYear;

    public Graph3D(Boolean defaultMode, Label yearsLabel, Hud hud) {
        super();
        // let there be light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-1000);
        light.setTranslateY(-1000);
        light.setTranslateZ(1000);
        light.getScope().add(this);
        this.getChildren().add(light);
        AmbientLight ambient = new AmbientLight();
        this.getChildren().add(ambient);

        ObjModelImporter objImporter = new ObjModelImporter();
        URL modelUrl = this.getClass().getResource("/Earth/earth.obj");
        objImporter.read(modelUrl);
        MeshView[] meshViews = objImporter.getImport();
        earth = new Group(meshViews);
        this.getChildren().add(earth);
        quads = new Group();
        bars = new Group();
        this.getChildren().add(quads);
        this.displayedYear = yearsLabel;
        this.currentMode = defaultMode;
        this.hud = hud;
        this.currentYear = FXController.initYear;
    }

    /**
     * Called by ToggleSwitch when its state change
     *
     * @param graphMode true when bar graph mode is selected false when quad mode is selected
     */
    public void redrawGraph(Boolean graphMode) {
        ExtremesTemp temp = DataLoader.getExtremesForYear(currentYear);
        hud.update(temp.getMin(), temp.getMax());
        if (graphMode)
            drawBarGraph();
        else drawQuadGraph();
    }

    /**
     * Called when yearsSelector's value has changed
     */
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        currentYear = (short) Math.floor((Double) newValue);
        displayedYear.setText(String.valueOf(currentYear));
        redrawGraph(currentMode);
    }

    private void drawBarGraph() {
        quads.getChildren().clear();
        bars.getChildren().clear();
        // TODO
    }

    private void drawQuadGraph() {
        quads.getChildren().clear();
        bars.getChildren().clear();
        try {
            for (double lat = DataLoader.firstPosition.getLatitude(); lat <= DataLoader.lastPosition.getLatitude(); lat += DataLoader.latitudeDelta) {
                for (double lon = DataLoader.firstPosition.getLongitude(); lon <= DataLoader.lastPosition.getLongitude(); lon += DataLoader.longitudeDelta) {
                    LocalDatedAnomaly lda = DataLoader.getValueForYearAndChunk(currentYear, lat, lat + 2.0, lon, lon + 2.0);
                    if (lda != null && !Double.isNaN(lda.getValue())) {
                        System.out.println("Loading : " + formatter.format((1 + (lat * lon) / (DataLoader.lastPosition.getLatitude() * DataLoader.lastPosition.getLongitude())) / 0.02) + "%");
                        PhongMaterial mat = new PhongMaterial();
                        double max = Math.max(DataLoader.getExtremesForYear(currentYear).getMax(), Math.abs(DataLoader.getExtremesForYear(currentYear).getMin()));
                        double percentage = (lda.getValue() + max)
                                / (max * 2);
                        mat.setSpecularColor(percentageColor(percentage));
                        mat.setDiffuseColor(percentageColor(percentage));
                        Point3D topRight = geoPosTo3dCoord(lat + DataLoader.latitudeDelta, lon + DataLoader.longitudeDelta, earthRadius * OVERLAY_RADIUS);
                        Point3D botRight = geoPosTo3dCoord(lat, lon + DataLoader.longitudeDelta, earthRadius * OVERLAY_RADIUS);
                        Point3D topLeft = geoPosTo3dCoord(lat, lon, earthRadius * OVERLAY_RADIUS);
                        Point3D botLeft = geoPosTo3dCoord(lat + DataLoader.latitudeDelta, lon, earthRadius * OVERLAY_RADIUS);
                        drawQuadrilateral(topRight, botRight, topLeft, botLeft, mat);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawQuadrilateral(Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material) {
        final TriangleMesh triangleMesh = new TriangleMesh();
        final float[] points = {
                (float) topRight.getX(), (float) topRight.getY(), (float) topRight.getZ(),
                (float) topLeft.getX(), (float) topLeft.getY(), (float) topLeft.getZ(),
                (float) bottomLeft.getX(), (float) bottomLeft.getY(), (float) bottomLeft.getZ(),
                (float) bottomRight.getX(), (float) bottomRight.getY(), (float) bottomRight.getZ(),
        };
        final float[] texCoords = {
                1, 1,
                1, 0,
                0, 1,
                0, 0
        };
        final int[] faces = {
                0, 1, 1, 0, 2, 2,
                0, 1, 2, 2, 3, 3
        };

        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);

        final MeshView meshView = new MeshView(triangleMesh);
//        meshView.cullFaceProperty().setValue(CullFace.NONE); // DEBUG PURPOSE
        meshView.setMaterial(material);
        quads.getChildren().add(meshView);
    }

    public static Point3D geoPosTo3dCoord(GeoPosition position, double radius) {
        double rlat = Math.toRadians(position.getLatitude() + TEXTURE_LAT_OFFSET);
        double rlon = Math.toRadians(position.getLongitude() + TEXTURE_LON_OFFSET);
        return new Point3D(-Math.sin(rlon) * Math.cos(rlat) * radius,
                -Math.sin(rlat) * radius,
                Math.cos(rlon) * Math.cos(rlat) * radius);
    }

    public static Point3D geoPosTo3dCoord(double latitude, double longitude, double radius) {
        double rlat = Math.toRadians(latitude + TEXTURE_LAT_OFFSET);
        double rlon = Math.toRadians(longitude + TEXTURE_LON_OFFSET);
        return new Point3D(-Math.sin(rlon) * Math.cos(rlat) * radius,
                -Math.sin(rlat) * radius,
                Math.cos(rlon) * Math.cos(rlat) * radius);
    }

    /**
     * @param percentage as a double between 0 and 1
     * @return Color lineary mapped between minColor and maxColor
     */
    public static Color percentageColor(double percentage) throws Exception {
        if (percentage < 0 || percentage > 1)
            throw new Exception("Percentage out of range [0.0-1.0] : " + percentage);
        double inv = 1 - percentage;
        double r = maxColor.getRed() * percentage + minColor.getRed() * inv;
        double g = maxColor.getGreen() * percentage + minColor.getGreen() * inv;
        double b = maxColor.getBlue() * percentage + minColor.getBlue() * inv;
        return new Color(r, g, b, OVERLAY_ALPHA);
    }
}
