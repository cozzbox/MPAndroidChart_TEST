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

    public float setCircleRadius(TimeScale timeScale) {
        float radius = 0f;
        switch (timeScale) {
            case WEEK:
                radius = 10f;
                break;

            case MONTH:
                radius = 5f;
                break;

            case QUARTER:
            case YEAR:
                radius = 0;
        }

        return radius;
    }

    public float circleHoleRadius(TimeScale timeScale) {
        float radius = 0f;
        switch (timeScale) {
            case WEEK:
                radius = 10f;
                break;

            case MONTH:
            case QUARTER:
                radius = 2.5f;
                break;

            case YEAR:
                radius = 0;
        }

        return radius;
    }
}
