package sample.mpandroidchartstest;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class GraphBody {

    String measureweight;
    String goal;
    List<Weight> weight;
    List<Fat> body_fat;

    static class Weight {
        String record_date;
        String value;
    }

    static class Fat {
        String record_date;
        String value;
    }
}
