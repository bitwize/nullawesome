package bitwize.nullawesome;

public class ElevatorStates {
    ElevatorState primaryState = new ElevatorState();
    ElevatorState alternateState = new ElevatorState();
    boolean isAlternate = false;
    boolean transitioning = false;
    float transitionSpeed = 1.f;
}
