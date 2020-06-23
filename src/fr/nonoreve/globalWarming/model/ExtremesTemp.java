package fr.nonoreve.globalWarming.model;

/**
 * Store the min and max temperature of a year
 */
public class ExtremesTemp {
    private double min, max;

    public ExtremesTemp(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
