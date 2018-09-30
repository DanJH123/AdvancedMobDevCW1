package a15071894.coursework1.Control;

//Class contains the controls of the Main Menu and the inputs from the user in this case.
public class MainMenuView {
    private String location;
    private String distance;
    private boolean tubeModeToggle;
    private boolean busModeToggle;
    private boolean bikeModeToggle;


    public MainMenuView(String location, String distance, boolean tubeModeToggle,
                        boolean busModeToggle, boolean bikeModeToggle) {
        this.location = location;
        this.distance = distance;
        this.tubeModeToggle = tubeModeToggle;
        this.busModeToggle = busModeToggle;
        this.bikeModeToggle = bikeModeToggle;
    }

    public String getLocation() {
        return location;
    }

    public String getDistance() {
        return distance;
    }

}
