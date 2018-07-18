package sample.mpandroidchartstest.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.media.Image;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

import sample.mpandroidchartstest.formatter.CustomFillFormatter;

public class CustomLineChartRenderer extends LineChartRenderer {

    public CustomLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }
/*
    //---------------------------------------
    // ゴール領域の塗りつぶしに利用   ここから
    //---------------------------------------
    // This method is same as its parent implementation. (Required so our version of generateFilledPath() is called.)
    @Override
    protected void drawLinearFill(Canvas c, ILineDataSet dataSet, Transformer trans, XBounds bounds) {

        final Path filled = mGenerateFilledPathBuffer;

        final int startingIndex = bounds.min;
        final int endingIndex = bounds.range + bounds.min;
        final int indexInterval = 128;

        int currentStartIndex;
        int currentEndIndex;
        int iterations = 0;

        // Doing this iteratively in order to avoid OutOfMemory errors that can happen on large bounds sets.
        do {
            currentStartIndex = startingIndex + (iterations * indexInterval);

            currentEndIndex = currentStartIndex + indexInterval;
            currentEndIndex = currentEndIndex > endingIndex ? endingIndex : currentEndIndex;

            if (currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet, currentStartIndex, currentEndIndex, filled);

                trans.pathValueToPixel(filled);

                final Drawable drawable = dataSet.getFillDrawable();
                if (drawable != null) {
                    drawFilledPath(c, filled, drawable);
                }
                else {
                    drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }

            iterations++;

        } while (currentStartIndex <= currentEndIndex);
    }

    // This method defines the perimeter of the area to be filled for horizontal bezier data sets.
    @Override
    protected void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans, XBounds bounds) {

        final float phaseY = mAnimator.getPhaseY();

        //Call the custom method to retrieve the dataset for other line
        final List<Entry> boundaryEntries = ((CustomFillFormatter)dataSet.getFillFormatter()).getFillLineBoundary();

        // We are currently at top-last point, so draw down to the last boundary point
        Entry boundaryEntry = boundaryEntries.get(bounds.min + bounds.range);
        spline.lineTo(boundaryEntry.getX(), boundaryEntry.getY() * phaseY);

        // Draw a cubic line going back through all the previous boundary points
        Entry prev = dataSet.getEntryForIndex(bounds.min + bounds.range);
        Entry cur = prev;
        for (int x = bounds.min + bounds.range; x >= bounds.min; x--) {

            prev = cur;
            cur = boundaryEntries.get(x);

            final float cpx = (prev.getX()) + (cur.getX() - prev.getX()) / 2.0f;

            spline.cubicTo(
                    cpx, prev.getY() * phaseY,
                    cpx, cur.getY() * phaseY,
                    cur.getX(), cur.getY() * phaseY);
        }

        // Join up the perimeter
        spline.close();

        trans.pathValueToPixel(spline);

        final Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {
            drawFilledPath(c, spline, drawable);
        }
        else {
            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }

    }

    // This method defines the perimeter of the area to be filled for straight-line (default) data sets.
    private void generateFilledPath(final ILineDataSet dataSet, final int startIndex, final int endIndex, final Path outputPath) {

        final float phaseY = mAnimator.getPhaseY();
        final Path filled = outputPath; // Not sure if this is required, but this is done in the original code so preserving the same technique here.
        filled.reset();

        //Call the custom method to retrieve the dataset for other line
        final List<Entry> boundaryEntries = ((CustomFillFormatter)dataSet.getFillFormatter()).getFillLineBoundary();

        final Entry entry = dataSet.getEntryForIndex(startIndex);
        final Entry boundaryEntry = boundaryEntries.get(startIndex);

        // Move down to boundary of first entry
        filled.moveTo(entry.getX(), boundaryEntry.getY() * phaseY);

        // Draw line up to value of first entry
        filled.lineTo(entry.getX(), entry.getY() * phaseY);

        // Draw line across to the values of the next entries
        Entry currentEntry;
        for (int x = startIndex + 1; x <= endIndex; x++) {
            currentEntry = dataSet.getEntryForIndex(x);
            filled.lineTo(currentEntry.getX(), currentEntry.getY() * phaseY);
        }

        // Draw down to the boundary value of the last entry, then back to the first boundary value
        Entry boundaryEntry1;
        for (int x = endIndex; x > startIndex; x--) {
            boundaryEntry1 = boundaryEntries.get(x);
            filled.lineTo(boundaryEntry1.getX(), boundaryEntry1.getY() * phaseY);
        }

        // Join up the perimeter
        filled.close();

    }
    //---------------------------------------
    // ゴール領域の塗りつぶしに利用   ここまで
    //---------------------------------------
*/

}
