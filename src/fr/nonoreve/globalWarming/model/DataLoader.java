package fr.nonoreve.globalWarming.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class DataLoader {

    private static final HashMap<Integer, LocalDatedAnomaly> anomalies = new HashMap<>();
    private static final HashMap<Short, ExtremesTemp> extremesTemps = new HashMap<>();
    public static HashMap<Short, List<Integer>> years = new HashMap<Short, List<Integer>>();

    // computed at data load to speed up
    public static short firstYear, lastYear;
    public static int yearsSpan;
    public static GeoPosition firstPosition, lastPosition;
    public static double latitudeDelta, longitudeDelta;

    public static void load(String filename) {
        try {
            if (anomalies.size() > 0)
                return;
            FileReader file = new FileReader(filename);
            BufferedReader bufRead = new BufferedReader(file);

            // first line
            String line = bufRead.readLine();
            if (line != null) {
                String[] array = line.split(",");
                if (array.length < 3)
                    throw new Exception("Data is not compatible.");

                firstYear = parseSillyString(array[2]);
                lastYear = parseSillyString(array[array.length - 1]);
                for (String s : array) {
                    try {
                        years.put(parseSillyString(s), new ArrayList<Integer>());
                    } catch (NumberFormatException e) {
                        // expected lat and long at the beggining of the line
                    }
                }
            } else {
                throw new Exception("File : " + file + " is empty.");
            }
            yearsSpan = years.size();
            System.out.println("Read " + yearsSpan + " years.");

            // rest of the file
            line = bufRead.readLine();
            GeoPosition previousPosition = null;
            int latitudeIncrement = 0;
            int longitudeIncrement = 0;
            boolean firstLat = true;
            while (line != null) {
                String[] array = line.split(",");
                if (array.length < 3 || array.length < years.size() + 2)
                    throw new Exception("Data is not compatible.");

                // get the position
                GeoPosition currentPosition = new GeoPosition(Double.parseDouble(array[0]), Double.parseDouble(array[1]));
                if (previousPosition != null) {
                    if (previousPosition.getLatitude() != currentPosition.getLatitude()) {
                        latitudeIncrement++;
                        firstLat = false;
                    }
                    if (firstLat && previousPosition.getLongitude() != currentPosition.getLongitude())
                        longitudeIncrement++;
                }
                previousPosition = currentPosition;
                if (firstPosition == null)
                    firstPosition = currentPosition;

                // get all the values
                Iterator<Short> it = years.keySet().iterator();
                for (int i = 2; i < array.length; i++) {
                    Double value = null;
                    try {
                        value = Double.parseDouble(array[i]);
                    } catch (NumberFormatException e) {
                        // catching NAs
                    }
                    short currentYear = it.next();
                    LocalDatedAnomaly lda = new LocalDatedAnomaly(currentPosition, currentYear, value);
                    if (anomalies.containsKey(lda.hashCode())) {
                        System.err.println("Duplicated hash");
                        System.err.println(anomalies.get(lda.hashCode()));
                        System.err.println(lda);
                    }
                    int hash = lda.hashCode();
                    anomalies.put(hash, lda);
                    years.get(currentYear).add(hash);

                    // computing min and max temperatures
                    if (value != null) {
                        if (extremesTemps.containsKey(currentYear)) {
                            ExtremesTemp extremesTemp = extremesTemps.get(currentYear);
                            if (extremesTemp.getMax() < value)
                                extremesTemps.put(currentYear, new ExtremesTemp(extremesTemp.getMin(), value));
                            if (extremesTemp.getMin() > value)
                                extremesTemps.put(currentYear, new ExtremesTemp(value, extremesTemp.getMax()));
                        } else {
                            extremesTemps.put(currentYear, new ExtremesTemp(value, value));
                        }
                    }
                }

                line = bufRead.readLine();
            }
            DataLoader.lastPosition = previousPosition;
            latitudeDelta = (previousPosition.getLatitude() - firstPosition.getLatitude()) / latitudeIncrement;
            longitudeDelta = (previousPosition.getLongitude() - firstPosition.getLongitude()) / longitudeIncrement;
            System.out.println("Read " + anomalies.size() + " positions.");

            bufRead.close();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse a string removing quotation marks
     */
    private static short parseSillyString(String s) {
        s = s.replace('\"', ' ');
        s = s.trim();
        return Short.parseShort(s);
    }

    // TODO refactor : too long (cache system ?)
    public static LocalDatedAnomaly getValueForYearAndChunk(short year, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) throws Exception {
        List<LocalDatedAnomaly> yearData = getDataForYear(year);
        for (LocalDatedAnomaly a : yearData) {
            if (a.getPosition().isInChunk(minLatitude, maxLatitude, minLongitude, maxLongitude))
                return a;
        }
        return null;
    }

    /**
     * @return all the LocalDatedAnomalies that were recorded the given year
     */
    public static List<LocalDatedAnomaly> getDataForYear(short year) {
        List<LocalDatedAnomaly> result = new ArrayList<>();
        years.get(year).forEach(h -> result.add(anomalies.get(h)));
        result.sort((a1, a2) -> {
            if (a1.getPosition().getLatitude() != a2.getPosition().getLatitude())
                return (int) (a1.getPosition().getLatitude() - a2.getPosition().getLatitude());
            return (int) (a1.getPosition().getLongitude() - a2.getPosition().getLongitude());
        });
        return result;
    }

    public static ExtremesTemp getExtremesForYear(short year) {
        return extremesTemps.get(year);
    }

    /**
     * @return all the LocalDatedAnomalies that matches this position
     * DEPRECATED way too long to be used for more than a few calls
     */
    public static List<LocalDatedAnomaly> getDataForChunk(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        List<LocalDatedAnomaly> result = new ArrayList<>();
        anomalies.forEach((i, a) -> {
            try {
                if (a.getPosition().isInChunk(minLatitude, maxLatitude, minLongitude, maxLongitude))
                    result.add(a);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });
        result.sort(Comparator.comparingInt(LocalDatedAnomaly::getYear));
        return result;
    }
}
