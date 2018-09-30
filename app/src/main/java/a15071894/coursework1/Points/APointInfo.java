package a15071894.coursework1.Points;

// This abstract class represents each of the different types of information for each point. It
// contains a toString method that needs implementing by subclasses. This makes dialogFragment creation
// far more simple as the toString method is called for each type and can be referenced using APointInfo.
public abstract class APointInfo {

    public abstract String toString();
}
