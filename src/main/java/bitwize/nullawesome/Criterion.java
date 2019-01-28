package bitwize.nullawesome;

public interface Criterion {
    public boolean test(int eid);
    public static final Criterion nullCriterion = (eid) -> false;

    public static final Criterion allCriterion = (eid) -> true;

}

