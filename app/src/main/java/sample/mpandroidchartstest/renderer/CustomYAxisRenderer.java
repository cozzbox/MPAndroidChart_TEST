package sample.mpandroidchartstest.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class CustomYAxisRenderer extends YAxisRenderer {

    private Paint mYAxisLabelCirclePaint;
    private Paint mYAxisGoalCirclePaint;

    public CustomYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);

        mYAxisLabelCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYAxisLabelCirclePaint.setStyle(Paint.Style.FILL);
        mYAxisLabelCirclePaint.setColor(Color.GRAY);
        mYAxisLabelCirclePaint.setAlpha(100);

        mYAxisGoalCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYAxisGoalCirclePaint.setStyle(Paint.Style.FILL);
        mYAxisGoalCirclePaint.setColor(Color.parseColor("#03B9FC"));
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        // AxisRendererではアンチエイリアスしか設定されていない
        mAxisLabelPaint.setTextSize(42);
        mAxisLabelPaint.setColor(Color.BLACK);

        float xPosition = fixedPosition - 15;

        // draw
        for (int i = from; i < to; i++) {

            String text = mYAxis.getFormattedLabel(i);

            int textW = Utils.calcTextWidth(mAxisLabelPaint, text) + 2;
            int textH = Utils.calcTextHeight(mAxisLabelPaint, text);

            RectF rectf = new RectF();
            rectf.left = xPosition - (textW + mYAxis.getXOffset());
            rectf.top = positions[i * 2 + 1] - (textH + 10);
            rectf.right = xPosition + mYAxis.getXOffset();
            rectf.bottom = positions[i * 2 + 1] + textH;

            c.drawRoundRect(rectf, 30, 30, mYAxisLabelCirclePaint);
            c.drawText(text, xPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);

        }
    }

    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] pts = mRenderLimitLinesBuffer;
        pts[0] = 0;
        pts[1] = 0;
        Path limitLinePath = mRenderLimitLines;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            mLimitLineClippingRect.set(mViewPortHandler.getContentRect());
            mLimitLineClippingRect.inset(0.f, -l.getLineWidth());
            c.clipRect(mLimitLineClippingRect);

            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());

            pts[1] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();
            // c.drawLines(pts, mLimitLinePaint);

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label != null && !label.equals("")) {

                mLimitLinePaint.setStyle(l.getTextStyle());
                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());
                mLimitLinePaint.setTypeface(l.getTypeface());
                mLimitLinePaint.setStrokeWidth(0.5f);
                mLimitLinePaint.setTextSize(l.getTextSize());

                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                float xOffset = Utils.convertDpToPixel(4f) + l.getXOffset();
                float yOffset = l.getLineWidth() + labelLineHeight + l.getYOffset();

                final LimitLine.LimitLabelPosition position = l.getLabelPosition();

                if (position == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                    mLimitLinePaint.setTextAlign(Paint.Align.RIGHT);
                    c.drawText(label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint);

                } else if (position == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                    mLimitLinePaint.setTextAlign(Paint.Align.RIGHT);
                    c.drawText(label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] + yOffset, mLimitLinePaint);

                } else if (position == LimitLine.LimitLabelPosition.LEFT_TOP) {

                    int textW = Utils.calcTextWidth(mLimitLinePaint, label);
                    int textH = Utils.calcTextHeight(mLimitLinePaint, label);

                    RectF rectF = new RectF(
                            mViewPortHandler.contentLeft() - 230,    // -30してRを隠す
                            pts[1] - textH,
                            mViewPortHandler.contentLeft() + xOffset + textW + 20,
                            pts[1] + textH);
                    c.drawRoundRect(rectF, 30, 30, mYAxisGoalCirclePaint);


                    mLimitLinePaint.setTextAlign(Paint.Align.LEFT);
                    mLimitLinePaint.setColor(Color.WHITE);
                    c.drawText(label,
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] + (textH / 2), mLimitLinePaint);

                } else {

                    mLimitLinePaint.setTextAlign(Paint.Align.LEFT);
                    c.drawText(label,
                            mViewPortHandler.offsetLeft() + xOffset,
                            pts[1] + yOffset, mLimitLinePaint);
                }
            }

            c.restoreToCount(clipRestoreCount);
        }
    }
}
