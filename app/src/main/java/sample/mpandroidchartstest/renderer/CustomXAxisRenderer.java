package sample.mpandroidchartstest.renderer;

import android.graphics.Canvas;
import android.util.Log;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sample.mpandroidchartstest.Constain;
import sample.mpandroidchartstest.DateUtil;
import sample.mpandroidchartstest.TimeScale;
import sample.mpandroidchartstest.formatter.CustomAxisFormatter;

public class CustomXAxisRenderer extends XAxisRenderer {

    private static final long mInterval = 60 * 60 * 24 * 1000;

    public TimeScale mTimeScale = TimeScale.YEAR;

    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void computeAxisValues(float min, float max) {

        double interval = mInterval * mTimeScale.getTerm();

        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        /*
        double first = Math.floor(min / interval) * interval;
        double last = Math.floor(max / interval) * interval;
        if(mAxis.isCenterAxisLabelsEnabled()) {
            first -= interval;
        }
        */
        double first = Math.floor(min / mInterval) * mInterval;
        double last = Math.floor(max / mInterval) * mInterval;

//        Calendar cale = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        cale.setTimeInMillis((long) min);
//        Log.i("min", DateUtil.getCalendarFormat("yyyy/MM/dd HH:mm:ss",cale));
//        cale.clear();
//        cale.setTimeInMillis((long)max);
//        Log.i("max", DateUtil.getCalendarFormat("yyyy/MM/dd HH:mm:ss",cale));
//        cale.clear();
//        cale.setTimeInMillis((long) first);
//        Log.i("first", DateUtil.getCalendarFormat("yyyy/MM/dd HH:mm:ss",cale));
//        cale.clear();
//        cale.setTimeInMillis((long) last);
//        Log.i("last", DateUtil.getCalendarFormat("yyyy/MM/dd HH:mm:ss",cale));

        mAxis.mEntries = new float[]{};
        mAxis.mCenteredEntries = new float[]{};
        mAxis.mEntryCount = 0;

        double f, f2;
        int i;

        switch (mTimeScale) {

            case WEEK:

                for (f = first; f <= last; f += mInterval) {
                    calendar.clear();
                    calendar.setTimeInMillis((long) f);
                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        first = f;
                        for (f2 = f; f2 <= last; f2 += interval) {
                            ++n;
                        }
                        break;
                    }
                }

                mAxis.mEntryCount = n;

                if (mAxis.mEntries.length < n) {
                    // Ensure stops contains at least numStops elements.
                    mAxis.mEntries = new float[n];
                }

                for (f = first, i = 0; i < n; f += interval, ++i) {

                    if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                        f = 0.0;

                    mAxis.mEntries[i] = (float) f;
                }

                break;

            case MONTH:
            case QUARTER:

                calendar.clear();
                calendar.setTimeInMillis((long) first);
                int lastDay = calendar.get(Calendar.DAY_OF_MONTH) -1;
                first -= lastDay * mInterval;

                calendar.clear();
                calendar.setTimeInMillis((long) last);
                lastDay = calendar.get(Calendar.DAY_OF_MONTH) -1;
                last -= (lastDay * mInterval);


                f = first;
                while (f <= last) {
                    ++n;

                    calendar.clear();
                    calendar.setTimeInMillis((long)f);
                    f += calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * mInterval;
                }

                mAxis.mEntryCount = n;

                if (mAxis.mEntries.length < n) {
                    // Ensure stops contains at least numStops elements.
                    mAxis.mEntries = new float[n];
                }

                i = 0;
                f = first;
                while (f <= last) {
                    mAxis.mEntries[i] = (float) f;
                    i++;

                    calendar.clear();
                    calendar.setTimeInMillis((long)f);
                    f += calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * mInterval;
                }

                break;

            case YEAR:

                calendar.clear();
                calendar.setTimeInMillis((long) first);
                int year = calendar.get(Calendar.YEAR);
                Log.i("from", DateUtil.getCalendarFormat("yyyy/MM/dd",calendar));

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.clear();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Log.i("to", DateUtil.getCalendarFormat("yyyy/MM/dd", cal));

                int days = (Math.abs(DateUtil.getDiffDays(calendar, cal)));
                Log.i("days", days+"");

                first -= days * mInterval;
                cal.clear();
                cal.setTimeInMillis((long) first);
                Log.i("first", DateUtil.getCalendarFormat("yyyy/MM/dd", cal));

                cal.clear();
                cal.setTimeInMillis((long) last);
                Log.i("last", DateUtil.getCalendarFormat("yyyy/MM/dd", cal));

                f = first;
                while (f <= last) {
                    ++n;

                    calendar.clear();
                    calendar.setTimeInMillis((long)f);
                    f += (mTimeScale.getTerm()) * mInterval;
                }

                mAxis.mEntryCount = n;

                if (mAxis.mEntries.length < n) {
                    // Ensure stops contains at least numStops elements.
                    mAxis.mEntries = new float[n];
                }

                i = 0;
                f = first;
                while (f <= last) {
                    mAxis.mEntries[i] = (float) f;
                    i++;

                    calendar.clear();
                    calendar.setTimeInMillis((long) f);
                    f += (mTimeScale.getTerm()) * mInterval;
                }

                break;
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled()) {

            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = (float)interval / 2f;

            for (i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }

    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                if (mXAxis.getValueFormatter() instanceof CustomAxisFormatter) {
                    ((CustomAxisFormatter) mXAxis.getValueFormatter()).mTimeScale = mTimeScale;
                }
                String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.getChartWidth())
                            x -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        x += width / 2;
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
            }
        }
    }


}
