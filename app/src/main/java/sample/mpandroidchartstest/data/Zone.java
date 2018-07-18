package sample.mpandroidchartstest.data;

import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class Zone {

    private float mYMax;
    private float mYMin;
    private float mXMin;
    private float mXMax;

    private int mColor;

    public Zone(float min, float max, int color) {
        this.mYMin = min;
        this.mYMax = max;
        this.mColor = color;
    }

    public Zone origin(float min, float max) {
        this.mXMin = min;
        this.mXMax = max;

        return this;
    }

    public LineDataSet getDataSet() {

        ArrayList<Entry> list = new ArrayList<>();
        list.add(new Entry(mXMin, mYMin));
        list.add(new Entry(mXMax, mYMax));

        LineDataSet dataSet = new LineDataSet(list, "");
        dataSet.setLineWidth(1);
        //dataSet.setColor(mColor, 0);
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(mColor);
        dataSet.setFillAlpha(1);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        return dataSet;
    }
}
