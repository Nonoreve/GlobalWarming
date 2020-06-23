package fr.nonoreve.globalWarming.model;

import java.util.Objects;

public class LocalDatedAnomaly {
    private GeoPosition position;
    private short year;
    private Double value;

    public LocalDatedAnomaly(GeoPosition position, Short year, Double value) {
        this.position = position;
        this.year = year;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDatedAnomaly that = (LocalDatedAnomaly) o;
        return year == that.year &&
                Double.compare(that.value, value) == 0 &&
                Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {

        return position.hashCode() * 1009 + year;
    }

    public GeoPosition getPosition() {
        return position;
    }

    public short getYear() {
        return year;
    }

    public double getValue() {
        return (value != null ? value : Double.NaN);
    }

    @Override
    public String toString() {
        return "LocalDatedAnomaly{" +
                "position=" + position +
                ", year=" + year +
                ", value=" + value +
                '}';
    }
}
