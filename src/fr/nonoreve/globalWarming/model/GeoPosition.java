package fr.nonoreve.globalWarming.model;

public class GeoPosition {
    private double latitude;
    private double longitude;

    public GeoPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPosition that = (GeoPosition) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return (int) (latitude * 1009.0 + longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Test if the position is within the given boundaries
     * @return
     */
    public boolean isInChunk(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) throws Exception {
        if(minLatitude > maxLatitude || minLongitude > maxLongitude)
            throw new Exception("Min latitudes and longitudes must be less than their respective max");
        return minLatitude <= latitude && latitude < maxLatitude && minLongitude <= longitude && longitude < maxLongitude;
    }

    @Override
    public String toString() {
        return "GeoPosition{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
