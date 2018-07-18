package sample.mpandroidchartstest.charts;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import sample.mpandroidchartstest.R;
import sample.mpandroidchartstest.TimeScale;
import sample.mpandroidchartstest.components.CustomMarkerView;
import sample.mpandroidchartstest.data.CustomLineDataSet;
import sample.mpandroidchartstest.data.Zone;
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

    public void setData(ArrayList<Entry> values) {

//        if (getData() != null && getData().getDataSetCount() > 0) {
//            CustomLineDataSet item;
//
//            for (int i=0; i<getData().getDataSetCount(); i++) {
//                item = (CustomLineDataSet) getData().getDataSetByIndex(i);
//                item.setValues(values);
//            }
//
//
//            getData().notifyDataChanged();
//            notifyDataSetChanged();
//            invalidate();
//
//        } else {
            CustomLineDataSet item = new CustomLineDataSet(values, "LineChart");

            item.setDrawIcons(false);
            item.setColor(Color.parseColor("#03CC66"));
            item.setLineWidth(2.5f);
            item.setCircleColor(Color.parseColor("#03CC66"));
            item.setCircleRadius(4f);
            item.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            item.setValueTextSize(9f);
            item.setDrawCircleHole(true);
            item.setDrawCircles(true);
            item.setDrawFilled(false);
            item.setDrawValues(false);
            item.setAxisDependency(YAxis.AxisDependency.RIGHT);
            item.setHighlightLineWidth(1f);
            item.setHighLightColor(Color.parseColor("#878787"));
            item.setDrawHighlightIndicators(true);
            item.setDrawIcons(true);

            setRenderer(new CustomLineChartRenderer(this, getAnimator(), getViewPortHandler()));

            //TODO オーバー領域の描画を作成
            float interval = 1000 * 60 * 60 * 24;
            float xMin = (float)(item.getXMin() - (TimeScale.QUARTER.getTerm() * interval));
            float xMax = (float)(item.getXMax() + (TimeScale.QUARTER.getTerm() * interval));

            float ave = (item.getYMin() + item.getYMax()) / 2;
            Zone gz = new Zone(ave, ave, Color.RED);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(gz.origin(xMin, xMax).getDataSet());
            dataSets.add(item); // add the chart data

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            super.setData(data);
//        }

    }

    public void setData(CustomLineDataSet chartData) {


    }


}
