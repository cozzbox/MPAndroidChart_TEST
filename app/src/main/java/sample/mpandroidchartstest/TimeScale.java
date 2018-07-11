package sample.mpandroidchartstest;

public enum TimeScale {

    WEEK(7),
    MONTH(30),
    QUARTER(30),
    YEAR(364);

    private final double term;

    TimeScale(double term) {
        this.term = term;
    }

    public double getTerm() {
        return term;
    }
}
