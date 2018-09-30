package a15071894.coursework1.Points;


public class BikePointInfo extends APointInfo{
    private String numberOfBikes;
    private String commonName;

    public BikePointInfo(String numberOfBikes, String commonName) {
        this.numberOfBikes = numberOfBikes;
        this.commonName = commonName;
    }

    @Override
    public String toString() {
        return "\n"+commonName+ "\n\n" + numberOfBikes+" bikes available.\n";
    }

}
