package a15071894.coursework1.Points;

//Seems to work for both Bus stops and Tube Stations with a subtle difference with lines and bearings
public class StopPointInfo extends APointInfo {

    String naptanId;
    String stationName;
    String lineName;
    String bearing;
    String destinationName;
    String expectedArrival;
    String modeName;

    public StopPointInfo(String naptanId, String stationName, String lineName, String bearing,
                         String destinationName, String expectedArrival, String modeName) {
        this.naptanId = naptanId;
        this.stationName = stationName;
        this.lineName = lineName;
        this.bearing = bearing;
        this.destinationName = destinationName;
        this.expectedArrival = expectedArrival;
        this.modeName = modeName;
    }

    @Override
    public String toString(){
        String lineOrBearing = (this.lineName.equals("N/A") ? this.bearing: this.lineName);
        return  "\nTransport Mode: "+this.modeName.toUpperCase()+" "+lineOrBearing+"\n"
                +this.stationName+" towards "+this.destinationName+ " at "+this.expectedArrival+"\n";
    }
}