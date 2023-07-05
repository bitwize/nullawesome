package bitwize.nullawesome;

public class InvalidEntityException extends Exception {
    int eid;
    public InvalidEntityException(int i) {
        eid = i;
    }
}
