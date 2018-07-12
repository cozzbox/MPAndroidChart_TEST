package sample.mpandroidchartstest;

import android.annotation.SuppressLint;
import android.drm.DrmStore;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Utils;
import com.orhanobut.hawk.Hawk;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import sample.mpandroidchartstest.charts.CustomLineChart;
import sample.mpandroidchartstest.components.CustomMarkerView;
import sample.mpandroidchartstest.data.CustomEntry;
import sample.mpandroidchartstest.data.CustomLineDataSet;
import sample.mpandroidchartstest.formatter.CustomAxisFormatter;
import sample.mpandroidchartstest.formatter.CustomFillFormatter;
import sample.mpandroidchartstest.renderer.CustomLineChartRenderer;

import static com.github.mikephil.charting.components.LimitLine.LimitLabelPosition.LEFT_TOP;


public class MainActivity extends AppCompatActivity {

    private static final int TERM = 180;
    private static final int GOAL = 55;

    private CustomLineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout areaGraph = (RelativeLayout)findViewById(R.id.area_graph);

        mChart = new CustomLineChart(this);
        mChart.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        areaGraph.addView(mChart);

        // グラフ周りのpaddingをオフは、なぜかここでやらないと効かない。。。
        mChart.setViewPortOffsets(0,40,0,0);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // TODO タップのデータ取得
                //Log.e("XXX", "" + e.getY());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // データ・セット
        setData();


        // set an alternative background color
        //mChart.setBackgroundColor(Color.LTGRAY);

        // set the marker to the chart
//        IMarker marker = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
//        mChart.setMarker(marker);

        // ゴールライン
        setGoal();

        //mChart.setVisibleXRangeMaximum(7);

        //一番左へ表示を移動
        //mChart.moveViewToX(mChart.getData().getXMax());

        //mChart.setScaleMinima(0.5f, 0.5f);
        //mChart.setAutoScaleMinMaxEnabled(true);

        mChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                switch (lastPerformedGesture) {
                    case PINCH_ZOOM:
                        //TODO ここでズームし直す
                        Log.d("TEST:", "PINCH_ZOOM");
                        break;
                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });


        // ボタンイベント
        Button btnWeek = (Button) findViewById(R.id.week);
        Button btnMonth = (Button) findViewById(R.id.month);
        Button btnQuarter = (Button) findViewById(R.id.quarter);
        Button btnYear = (Button) findViewById(R.id.year);

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.setTimeScale(TimeScale.WEEK);
                mChart.invalidate();
            }
        });
        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.setTimeScale(TimeScale.MONTH);
                mChart.invalidate();
            }
        });
        btnQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.setTimeScale(TimeScale.QUARTER);
                mChart.invalidate();
            }
        });
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart.setTimeScale(TimeScale.YEAR);
                mChart.invalidate();
            }
        });

    }

    private void setGoal() {
        // 目標線はLimitLineで決まり！
        YAxis leftAxis = mChart.getAxisRight();
        float limt = GOAL;
        LimitLine ll = new LimitLine(limt, "Goal");
        ll.setLineColor(Color.parseColor("#03B9FC"));
        ll.setLineWidth(2f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(16f);
        ll.setLabelPosition(LEFT_TOP);

        // addするかしないかでゴールラインの描画を制御
        leftAxis.addLimitLine(ll);

        // グラフの後ろ側に描画する
        //mChart.getAxisRight().setDrawLimitLinesBehindData(true);
    }

    private void setData() {

        GraphBody entity = createTestData(TERM);
        ArrayList<Entry> values = generateGraphData(entity);

//
//        for (int i=0; i<count; i++) {
//            float val = (float)(Math.random() * range) + 195;
//            values.add(new Entry(i, val));
//        }

        CustomLineDataSet set1, set2, set3, set4;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (CustomLineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);

//            if (mChart.getVisibleXRange() <= 30) {
//                set1.setDrawCircleHole(true);
//                set1.setDrawCircles(true);
//            } else {
//                set1.setDrawCircleHole(false);
//                set1.setDrawCircles(false);
//            }

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();

        } else {

            // create a dataset and give it a type
            set1 = new CustomLineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            set1.setColor(Color.parseColor("#03CC66"));
            set1.setLineWidth(2.5f);
            set1.setCircleColor(Color.parseColor("#03CC66"));
            set1.setCircleRadius(4f);
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set1.setValueTextSize(9f);
            set1.setDrawCircleHole(true);
            set1.setDrawCircles(true);
            set1.setDrawFilled(false);
            set1.setDrawValues(false);
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(5.f);
//
//            if (Utils.getSDKInt() >= 18) {
//                // fill drawable only supported on api level 18 and above
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
//                set1.setFillDrawable(drawable);
//            }
//            else {
//                set1.setFillColor(Color.BLACK);
//            }
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set1.setHighlightLineWidth(1f);
            set1.setHighLightColor(Color.parseColor("#878787"));
            set1.setDrawHighlightIndicators(true);
            set1.setDrawIcons(true);

            /*
            List<Entry> list = new ArrayList<Entry>();
            list.add(new Entry(0,192));
            list.add(new Entry(count, 192));
            set2 = new CustomLineDataSet(list, "Range");
            set2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set2.setLineWidth(0);
            set2.setDrawCircles(false);
            set2.setDrawCircleHole(false);
            set2.setDrawFilled(true);
            set2.setDrawValues(false);
            set2.setFillColor(Color.parseColor("#C5EDFF"));
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setDrawHighlightIndicators(false);
            set2.setDrawHorizontalHighlightIndicator(false);
            set2.setDrawVerticalHighlightIndicator(false);
            set2.setHighlightEnabled(false);

            List<Entry> list2 = new ArrayList<Entry>();
            list2.add(new Entry(0,198));
            list2.add(new Entry(count, 198));
            set3 = new CustomLineDataSet(list2, "Range2");
            set3.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set3.setLineWidth(0);
            set3.setDrawCircles(false);
            set3.setDrawCircleHole(false);
            set3.setDrawFilled(true);
            set3.setDrawValues(false);
            set3.setFillColor(Color.parseColor("#FFA3A3"));
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setDrawHighlightIndicators(false);
            set3.setDrawVerticalHighlightIndicator(false);
            set3.setDrawHorizontalHighlightIndicator(false);
            set3.setHighlightEnabled(false);

            set2.setFillFormatter(new CustomFillFormatter(set3));

            List<Entry> list3 = new ArrayList<Entry>();
            list3.add(new Entry(mChart.getXAxis().getAxisMinimum(), 201));
            list3.add(new Entry(count, 201));
            set4 = new CustomLineDataSet(list3, "Range3");
            set4.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set4.setLineWidth(0);
            set4.setDrawCircles(false);
            set4.setDrawCircleHole(false);
            set4.setDrawFilled(true);
            set4.setDrawValues(false);
            set4.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set4.setDrawHighlightIndicators(false);
            set4.setDrawHorizontalHighlightIndicator(false);
            set4.setDrawVerticalHighlightIndicator(false);
            set4.setHighlightEnabled(false);

            set3.setFillFormatter(new CustomFillFormatter(set4));
            */

            mChart.setRenderer(new CustomLineChartRenderer(mChart, mChart.getAnimator(), mChart.getViewPortHandler()));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//            dataSets.add(set3);
//            dataSets.add(set2);
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }

    }

    // generate
    private ArrayList<Entry> generateGraphData(GraphBody data) {

        ArrayList<Entry> list = new ArrayList<>();

        for (int i=0; i<data.weight.size(); i++) {

            GraphBody.Weight weight = data.weight.get(i);

            Calendar cal = DateUtil.getSettingCalendar((weight.record_date).replaceAll("-", ""));
            //Log.i("cal", DateUtil.getCalendarFormat("yyyy/MM/dd HH:mm:ss", cal));

            long timestamp = cal.getTimeInMillis();

            float value = Float.parseFloat(weight.value);

            list.add(new Entry(timestamp, value));
        }

        //mChart.setReference(mReference);

        return list;
    }

    // create sample data
    private GraphBody createTestData(int entryNum) {

        GraphBody graphBody = new GraphBody();
        graphBody.measureweight = "1";
        graphBody.goal = GOAL + "";

        List<GraphBody.Weight> weights = new ArrayList<>();
        Calendar cal = DateUtil.getSettingCalendar("20171116");

        for (int i=0; i<entryNum; i++) {

            GraphBody.Weight weight = new GraphBody.Weight();

            weight.record_date = DateUtil.getCalendarFormat("yyyy-MM-dd", cal);
            weight.value = String.format("%.1f", (float)(Math.random() * 10) + GOAL);

            weights.add(weight);

            Log.i("No." + i, weight.record_date + " / " + weight.value);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        graphBody.weight = weights;

        return graphBody;
    }
}
