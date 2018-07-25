package sample.mpandroidchartstest.charts;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.jobs.AnimatedZoomJob;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    private Context mContext;

    private TimeScale mTimeScale = TimeScale.WEEK;
    public TimeScale getTimeScale() { return mTimeScale; }
    public void setTimeScale(TimeScale timeScale) {
        this.mTimeScale = timeScale;

        if (mXAxisRenderer instanceof CustomXAxisRenderer) {
            ((CustomXAxisRenderer) mXAxisRenderer).mTimeScale = mTimeScale;
        }

        // アニメーションを完全に止めてからscaleを変更しないと上手く動かない
        stopAnimated();

        List<ILineDataSet> listDataSets = getData().getDataSets();
        for(ILineDataSet item : listDataSets) {
            if (item instanceof LineDataSet) {
                LineDataSet data = (LineDataSet) item;
                data.setDrawCircles(mTimeScale.drawCircles(mTimeScale));
                data.setDrawCircleHole(mTimeScale.drawCircles(mTimeScale));
                data.setLineWidth(mTimeScale.getChartLineSize(mTimeScale));

                //TODO: ライブラリのDP->PX変換がバグってるので諦める
//                data.setCircleRadius(mTimeScale.setCircleRadius(mTimeScale));
//                data.setCircleHoleRadius(mTimeScale.circleHoleRadius(mTimeScale));
            }
        }

        float center = 0f;
        if (selectHighlightX != 0) {
            // highlightがあればxを中心にする
            center = selectHighlightX;

        } else {
            center = (getLowestVisibleX() + getHighestVisibleX()) / 2;

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis((long) center);

            float offset = 0;
            float left = 0;
            float num = 0;
            switch (mTimeScale) {

                case WEEK:

                    left = (calendar.get(Calendar.DAY_OF_WEEK) -2) * mInterval;
                    offset = Math.abs(center % mInterval);
                    center += (3.5f * mInterval) - (left + offset);
                    break;

                case MONTH:
                case QUARTER:

                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    num = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * mInterval;
                    left = (calendar.get(Calendar.DAY_OF_MONTH)) * mInterval;
                    center += (num / 2) - left + ((num % 2) * mInterval);
                    break;

                case YEAR:

                    int year = calendar.get(Calendar.YEAR);
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, 0);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);

                    center = cal.getTimeInMillis() + ((365f / 2) * mInterval);
                    break;
            }

        }

        zoomAndCenterAnimated(
                (float) (mXAxis.mAxisRange / (mTimeScale.getXRange() * mInterval)),
                0,
                center,
                1,
                YAxis.AxisDependency.RIGHT,
                500
        );

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
        mContext = context;
    }

    public CustomLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CustomLineChart(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void init() {
        super.init();
        setup();
    }

    private float selectHighlightX = 0;
    private void setup() {

        // chart view setting
        getDescription().setEnabled(false);
        getLegend().setEnabled(false);
        setScaleEnabled(false);
        setScaleXEnabled(true);
        setScaleYEnabled(false);
        setPinchZoom(true);
        setDoubleTapToZoomEnabled(false);
        setAutoScaleMinMaxEnabled(true);
        setDrawGridBackground(false);
        setHighlightPerDragEnabled(false);
        setHighlightPerTapEnabled(true);
        super.setViewPortOffsets(0, 50, 0, 0);     // ここで設定しても何故か効かない

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

        mRenderer = new CustomLineChartRenderer(this, mAnimator, mViewPortHandler);

        // select chart data
        super.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                selectHighlightX = h.getX();
            }

            @Override
            public void onNothingSelected() {
                selectHighlightX = 0f;
            }
        });

        // Y方向のジェスチャーをX方向に変換する
        mChartTouchListener = new CustomBarLineChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 3f);

        // custom gesture
        super.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                stopAnimated();
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                switch (lastPerformedGesture) {
                    case X_ZOOM:
                        if (getVisibleXRange() < (TimeScale.WEEK.getXRange()) * mInterval) {
                            // over zoom in
                            setTimeScale(TimeScale.WEEK);
                        } else if (getVisibleXRange() > TimeScale.YEAR.getXRange() * mInterval) {
                            // over zoom out
                            setTimeScale(TimeScale.YEAR);
                        } else if (getVisibleXRange() < mTimeScale.getXRange() * mInterval) {
                            // zoom in
                            setTimeScale(mTimeScale.zoomIn(getTimeScale()));
                        } else if (getVisibleXRange() > mTimeScale.getXRange() * mInterval) {
                            // zoom out
                            setTimeScale(mTimeScale.zoomOut(getTimeScale()));
                        }
                        break;
                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {}
            @Override
            public void onChartDoubleTapped(MotionEvent me) {}
            @Override
            public void onChartSingleTapped(MotionEvent me) {}
            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
            @Override
            public void onChartScale(MotionEvent me, float x, float y) {}
            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        });

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
        float xMin = (float)(item.getXMin() - (TimeScale.YEAR.getTerm() * interval));
        float xMax = (float)(item.getXMax() + (TimeScale.YEAR.getTerm() * interval));

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

    public void moveToInitPosition() {
        zoom((float) (mXAxis.mAxisRange / (mTimeScale.getXRange() * mInterval)), 0, 0, 0, YAxis.AxisDependency.RIGHT);
        moveViewToX(maxVisibleX());
    }

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

    @Override
    public void zoomAndCenterAnimated(float scaleX, float scaleY, float xValue, float yValue, YAxis.AxisDependency axis,
                                      long duration) {
        MPPointD origin = getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), axis);

        Runnable job = new AnimatedZoomJob(mViewPortHandler, this, getTransformer(axis), getAxis(axis), mXAxis.mAxisRange,
                scaleX, scaleY, mViewPortHandler.getScaleX(), mViewPortHandler.getScaleY(),
                xValue, yValue, (float) origin.x, (float) origin.y, duration);
        addViewportJob(job);

        MPPointD.recycleInstance(origin);
    }

    private void stopAnimated() {
        // アニメーションを完全に止めてからscaleを変更しないと上手く動かない
        if (mChartTouchListener instanceof BarLineChartTouchListener) {
            ((BarLineChartTouchListener) mChartTouchListener).stopDeceleration();
        }
    }


    private class CustomBarLineChartTouchListener extends BarLineChartTouchListener {
        public CustomBarLineChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
            super(chart, touchMatrix, dragTriggerDistance);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            stopAnimated();

            if (mTouchMode == Y_ZOOM) mTouchMode = X_ZOOM;

            super.onTouch(v, event);
            return true; // indicate event was handled
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

    }
}
