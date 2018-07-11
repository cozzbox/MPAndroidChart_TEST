package sample.mpandroidchartstest.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import sample.mpandroidchartstest.DateUtil;
import sample.mpandroidchartstest.R;

public class CustomMarkerView extends RelativeLayout implements IMarker {

    private MPPointF mOffset = new MPPointF();
    private MPPointF mOffset2 = new MPPointF();
    private WeakReference<Chart> mWeakChart;

    private TextView tvDate, tvContent;

    public CustomMarkerView(Context context) {
        super(context);
    }

    public CustomMarkerView(Context context, int layoutResource) {
        super(context);
        setupLayoutResource(layoutResource);
    }

    private void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);

        inflated.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        // measure(getWidth(), getHeight());
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());

        tvDate = (TextView) inflated.findViewById(R.id.tvDate);
        tvContent = (TextView) inflated.findViewById(R.id.tvContent);
        tvContent.setClickable(true);
        tvContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO クリックイベントはここへ
            }
        });

    }

    public void setOffset(MPPointF offset) {
        mOffset = offset;

        if (mOffset == null) {
            mOffset = new MPPointF();
        }
    }

    public void setOffset(float offsetX, float offsetY) {
        mOffset.x = offsetX;
        mOffset.y = offsetY;
    }

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }

    public void setChartView(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    public Chart getChartView() {
        return mWeakChart == null ? null : mWeakChart.get();
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {

        MPPointF offset = getOffset();
        mOffset2.x = offset.x;
        mOffset2.y = offset.y;

        Chart chart = getChartView();

        float width = getWidth() / 2;
        float height = getHeight() / 2;

        if (posX + mOffset2.x < 0) {
            mOffset2.x = - posX;
        } else if (chart != null && posX + width + mOffset2.x > chart.getWidth()) {
            mOffset2.x = chart.getWidth() - posX - width;
        }

        if (posY + mOffset2.y < 0) {
            mOffset2.y = - posY;
        } else if (chart != null && posY + height + mOffset2.y > chart.getHeight()) {
            mOffset2.y = chart.getHeight() - posY - height;
        }

        return mOffset2;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText("" + ce.getHigh());
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis((long) e.getX());
            tvDate.setText(DateUtil.getCalendarFormat("yyyy/MM/dd", cal));
            tvContent.setText("" + e.getY());
        }

        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());

    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        drawPointCircel(canvas, posX, posY, 15);

        float calcPosX = posX - (getMeasuredWidth() / 2);
        float calcPosY = getMeasuredHeight() + 30;          // 表示の高さはここで微調整

        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(calcPosX + offset.x, calcPosY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    private void drawPointCircel(Canvas canvas, float posX, float posY, float radius) {
        Paint mPaint = new Paint();
        canvas.drawCircle(posX, posY, radius, mPaint);
    }
}
