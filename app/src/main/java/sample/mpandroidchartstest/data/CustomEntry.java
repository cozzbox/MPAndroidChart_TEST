package sample.mpandroidchartstest.data;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import sample.mpandroidchartstest.GraphBody;

public class CustomEntry extends Entry {

    public CustomEntry(float x, float y, String record_date) {
        super.setX(x);
    }

    public float weight;

    public static class Weight {
        String record_date;
        String value;
    }

}
