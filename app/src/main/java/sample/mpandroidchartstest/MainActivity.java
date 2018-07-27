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

    private static final int TERM = 365;
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

        // ゴールライン
        setGoal();

        // データ・セット
        setData();

        mChart.moveToInitPosition();

        // set an alternative background color
        //mChart.setBackgroundColor(Color.LTGRAY);

        // set the marker to the chart
//        IMarker marker = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
//        mChart.setMarker(marker);

        //mChart.setVisibleXRangeMaximum(7);

        //一番左へ表示を移動
        //mChart.moveViewToX(mChart.getData().getXMax());

        //mChart.setScaleMinima(0.5f, 0.5f);
        //mChart.setAutoScaleMinMaxEnabled(true);

        // ボタンイベント
        Button btnWeek = (Button) findViewById(R.id.week);
        Button btnMonth = (Button) findViewById(R.id.month);
        Button btnQuarter = (Button) findViewById(R.id.quarter);
        Button btnYear = (Button) findViewById(R.id.year);

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (mChart.getTimeScale() == TimeScale.WEEK) return;
                mChart.setTimeScale(TimeScale.WEEK);
            }
        });
        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChart.getTimeScale() == TimeScale.MONTH) return;
                mChart.setTimeScale(TimeScale.MONTH);
            }
        });
        btnQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChart.getTimeScale() == TimeScale.QUARTER) return;
                mChart.setTimeScale(TimeScale.QUARTER);
            }
        });
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChart.getTimeScale() == TimeScale.YEAR) return;
                mChart.setTimeScale(TimeScale.YEAR);
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
        mChart.setData(values);
    }

    // data generate
    private ArrayList<Entry> generateGraphData(GraphBody data) {

        ArrayList<Entry> list = new ArrayList<>();

        for (int i=0; i<data.weight.size(); i++) {

            GraphBody.Weight weight = data.weight.get(i);
            Calendar cal = DateUtil.getSettingCalendar((weight.record_date).replaceAll("-", ""));
            long timestamp = cal.getTimeInMillis();
            float value = Float.parseFloat(weight.value);

            list.add(new Entry(timestamp, value));
        }

        return list;
    }

    // create sample data
    private GraphBody createTestData(int entryNum) {

        GraphBody graphBody = new GraphBody();
        graphBody.measureweight = "1";
        graphBody.goal = GOAL + "";

        List<GraphBody.Weight> weights = new ArrayList<>();
        Calendar cal = DateUtil.getSettingCalendar("20170720");

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
