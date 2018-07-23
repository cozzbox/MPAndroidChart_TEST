package sample.mpandroidchartstest.data;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

public class CustomLineDataSet extends LineDataSet {

    public CustomLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public void setDrawHighlightIndicators(boolean enabled) {
        setDrawVerticalHighlightIndicator(enabled);
        setDrawHorizontalHighlightIndicator(false); // 横線は不要
    }

}

