package sample.mpandroidchartstest.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;

import sample.mpandroidchartstest.Constain;
import sample.mpandroidchartstest.DateUtil;
import sample.mpandroidchartstest.R;
import sample.mpandroidchartstest.TimeScale;
import sample.mpandroidchartstest.components.CustomMarkerView;
import sample.mpandroidchartstest.formatter.CustomAxisFormatter;
import sample.mpandroidchartstest.renderer.CustomLineChartRenderer;
import sample.mpandroidchartstest.renderer.CustomXAxisRenderer;
import sample.mpandroidchartstest.renderer.CustomYAxisRenderer;

public class CustomLineChart extends LineChart {

    private static final int mGridColor = Color.parseColor("#E3E2E2");

    private TimeScale mTimeScale = TimeScale.WEEK;
    public void setTimeScale(TimeScale timeScale) {
        this.mTimeScale = timeScale;
        if (mXAxisRenderer instanceof CustomXAxisRenderer) {
            ((CustomXAxisRenderer) mXAxisRenderer).mTimeScale = mTimeScale;
        }
    }

//    private long mReference = 0;
//    public void setReference(long reference) {
//        this.mReference = reference;
//        if (mXAxisRenderer instanceof CustomXAxisRenderer) {
//            ((CustomXAxisRenderer) mXAxisRenderer).mReference = mReference;
//        }
//
//        CustomAxisFormatter formatter = new CustomAxisFormatter();
//        formatter.setReference(mReference);
//        mXAxis.setValueFormatter(formatter);
//
//    }

    public CustomLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLineChart(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        setup();

        mRenderer = new CustomLineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    private void setup() {

        // chart view setting
        getDescription().setEnabled(false);
        getLegend().setEnabled(false);
        setScaleXEnabled(true);
        setScaleYEnabled(true);
        setPinchZoom(true);
        setDoubleTapToZoomEnabled(false);
        setAutoScaleMinMaxEnabled(true);
        setDrawGridBackground(false);
        setHighlightPerDragEnabled(false);
        setHighlightPerTapEnabled(true);
        setViewPortOffsets(0, 100, 0, 0);     // ここで設定しても効かない

        // xAxis setting
        mXAxis.setDrawAxisLine(true);
        mXAxis.setDrawGridLines(true);
        mXAxis.setGridColor(mGridColor);
        mXAxis.setGridLineWidth(0.8f);
        mXAxis.setLabelCount(5);
        mXAxis.setDrawLabels(true);
        mXAxis.setCenterAxisLabels(true);
        mXAxis.setValueFormatter(new CustomAxisFormatter());
        mXAxisRenderer = new CustomXAxisRenderer(
                mViewPortHandler,
                mXAxis,
                mRightAxisTransformer);

        // yAxis setting (Right)
        mAxisRight.setDrawAxisLine(false);
        mAxisRight.setDrawGridLines(true);
        mAxisRight.setGridColor(mGridColor);
        mAxisRight.setGridLineWidth(0.3f);
        mAxisRight.setDrawLabels(true);
        mAxisRight.setSpaceTop(35f);
        mAxisRight.setSpaceBottom(25f);
        mAxisRight.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        // yAxis setting (Left)
        mAxisLeft.setEnabled(false);

        // yAxis renderer setting
        mAxisRendererRight = new CustomYAxisRenderer(
                mViewPortHandler,
                mAxisRight,
                mRightAxisTransformer);

        // set the marker to the chart
        mMarker = new CustomMarkerView(getContext(), R.layout.custom_marker_view_layout);

    }

}
