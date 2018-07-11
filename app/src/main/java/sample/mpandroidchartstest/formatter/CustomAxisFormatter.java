package sample.mpandroidchartstest.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sample.mpandroidchartstest.TimeScale;

public class CustomAxisFormatter implements IAxisValueFormatter {

    private DateFormat mDateFormat;
    private Date mDate;

    public TimeScale mTimeScale = TimeScale.WEEK;
    public long mReference = 0;

    public CustomAxisFormatter() {
        mDateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
        mDate = new Date();
    }

    public void setReference(long reference) {
        this.mReference = reference;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return getDateString((long)value);
    }

    private String getDateString(long timestamp) {
        String label = "";

        mDateFormat = getDateFormat(mTimeScale);

        timestamp += mReference;

        switch (mTimeScale) {
            case WEEK:

                mDate.setTime(timestamp);
                label += mDateFormat.format(mDate);
                label += " - ";
                timestamp += (24 * 60 * 60 * 1000) * 6;
                mDate.setTime(timestamp);
                label += mDateFormat.format(mDate);
                break;

            case MONTH:
            case QUARTER:
            case YEAR:
            default:

                mDate.setTime(timestamp);
                label = mDateFormat.format(mDate);
        }

        return label;
    }

    private SimpleDateFormat getDateFormat(TimeScale timeScale) {

        switch (timeScale) {
            case WEEK:
                return new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);

            case MONTH:
            case QUARTER:
                return new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

            case YEAR:
                return new SimpleDateFormat("yyyy", Locale.ENGLISH);

            default:
                return new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
        }
    }
}
