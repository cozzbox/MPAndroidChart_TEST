package sample.mpandroidchartstest;

public enum TimeScale {

    WEEK(7),
    MONTH(30),
    QUARTER(90),
    YEAR(365);

    private final double term;

    TimeScale(double term) {
        this.term = term;
    }

    public double getTerm() {
        return term;
    }

    public double getXRange() {
        return term * 1.15;
    }

    public boolean drawCircles(TimeScale timeScale) {
        switch (timeScale) {
            case WEEK:
            case MONTH:
                return true;

            case QUARTER:
            case YEAR:
                return false;

            default:
                return true;
        }
    }

    public TimeScale zoomIn(TimeScale timeScale) {
        switch (timeScale) {
            case YEAR: return QUARTER;
            case QUARTER: return MONTH;
            case MONTH: return WEEK;
            case WEEK:
            default: return WEEK;
        }
    }

    public TimeScale zoomOut(TimeScale timeScale) {
        switch (timeScale) {
            case WEEK: return MONTH;
            case MONTH: return QUARTER;
            case QUARTER: return YEAR;
            case YEAR:
            default: return YEAR;
        }
    }
}
