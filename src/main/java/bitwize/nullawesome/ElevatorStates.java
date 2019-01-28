package bitwize.nullawesome;

public class ElevatorStates {
    ElevatorState primaryState = new ElevatorState();
    ElevatorState alternateState = new ElevatorState();
    boolean transitioning = false;
    float transitionSpeed = 0.4f;
}
