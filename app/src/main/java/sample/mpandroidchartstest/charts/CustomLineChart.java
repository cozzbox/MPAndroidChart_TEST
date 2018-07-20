package sample.mpandroidchartstest.charts;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.jobs.AnimatedZoomJob;
import com.github.mikephil.charting.utils.MPPointD;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import sample.mpandroidchartstest.DateUtil;
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

    private static final int mGraphColor = Color.parseColor("#03CC66");
    private static final int mHighLightColor = Color.parseColor("#878787");
    private static final int mGridColor = Color.parseColor("#E3E2E2");
    private static final int mClearColor = Color.parseColor("#FF000000");

    private static final long mInterval = 1000 * 60 * 60 * 24;

    private TimeScale mTimeScale = TimeScale.WEEK;
    public TimeScale getTimeScale() { return mTimeScale; }
    public void setTimeScale(TimeScale timeScale) {
        this.mTimeScale = timeScale;
        if (mXAxisRenderer instanceof CustomXAxisRenderer) {
            ((CustomXAxisRenderer) mXAxisRenderer).mTimeScale = mTimeScale;
        }

        moveToPosition();
    }

    private LineData mLineData;

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
        mAxisRight.setGridLineWidth(0.5f);
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
        
        CustomLineDataSet item = new CustomLineDataSet(values, "LineChart");

        item.setDrawIcons(false);
        item.setColor(mGraphColor);
        item.setLineWidth(2.5f);
        item.setCircleColor(mGraphColor);
        item.setCircleRadius(4f);
        item.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        item.setValueTextSize(9f);
        item.setDrawCircleHole(true);
        item.setDrawCircles(true);
        item.setDrawFilled(false);
        item.setDrawValues(false);
        item.setAxisDependency(YAxis.AxisDependency.RIGHT);
        item.setHighlightLineWidth(1f);
        item.setHighLightColor(mHighLightColor);
        item.setDrawHighlightIndicators(true);
        item.setDrawIcons(true);

        setRenderer(new CustomLineChartRenderer(this, getAnimator(), getViewPortHandler()));

        //TODO オーバー領域の描画を作成
        float interval = 1000 * 60 * 60 * 24;
        float xMin = (float)(item.getXMin() - (TimeScale.QUARTER.getTerm() * interval));
        float xMax = (float)(item.getXMax() + (TimeScale.QUARTER.getTerm() * interval));

        float ave = (item.getYMin() + item.getYMax()) / 2;
        Zone gz = new Zone(ave, ave, mClearColor);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(gz.origin(xMin, xMax).getDataSet());
        dataSets.add(item); // add the chart data

        // create a data object with the datasets
        mLineData = new LineData(dataSets);

        // set data
        super.setData(mLineData);
    }

    public void moveToPosition() {
        fitScreen();    // scaleを初期化しないとだめ

        zoom((float) (mXAxis.mAxisRange / (mTimeScale.getXRange() * mInterval)), 0, 0, 0, YAxis.AxisDependency.RIGHT);
        moveViewToX(maxVisibleX());
    }

    /*
    // calculate visible area
    private var maxVisibleX: Double {
        guard _data != nil else { return 0 }
        let today = Date()
        return visibleOffset(date: today)
    }
    private var minVisibleX: Double {
        guard let data = _data else { return 0 }
        let earliestDay = Date.timeIntervalSinceToday(timeInterval: data.availableXMin * Date.aDayTimeInterval)
        return visibleOffset(date: earliestDay) + data.availableXMin
    }
    private func visibleOffset(date: Date) -> Double {
        let left, offset: Double
        switch timeScale {
        case .week:
            left = Double(Calendar.getWeekday(date).value)
            offset = (timeScale.xRange - timeScale.double) / 2
        case .month, .quarter:
            left = Double(Calendar.getDay(date) - 1)
            offset = (timeScale.xRange - Double(Calendar.getNumberOfDaysIn(.month, date: date))) / 2
        case .year:
            left = Double(Calendar.getDaysLeftInYear(date) - 1)
            offset = (timeScale.xRange - timeScale.double) / 2
        }
        return -left-offset
    }
     */

    private float maxVisibleX() {
        if (mLineData == null) return 0f;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return visibleOffset(calendar);
    }
    private float visibleOffset(Calendar calendar) {
        float target, left, offset;

        target = calendar.getTimeInMillis();
        left = 0;
        offset = 0;

        switch (mTimeScale) {
            case WEEK:
                left = (calendar.get(Calendar.DAY_OF_WEEK) -2) * mInterval;
                offset = (float) ((mTimeScale.getXRange() - mTimeScale.getTerm()) * mInterval) /2;
                break;

            case MONTH:
            case QUARTER:
                left = (calendar.get(Calendar.DAY_OF_MONTH) -1) * mInterval;
                offset = (float) ((mTimeScale.getXRange() - mTimeScale.getTerm()) * mInterval) /2;
                break;

            case YEAR:
                //TODO 実装途中
                int year = calendar.get(Calendar.YEAR);
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                int days = (Math.abs(DateUtil.getDiffDays(calendar, cal)));

                left = days * mInterval;
                offset = (float) ((mTimeScale.getXRange() - mTimeScale.getTerm()) * mInterval) /2;
                break;
        }
        return target - (left + offset);
    }


}
