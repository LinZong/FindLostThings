package misaka.nemesiss.com.findlostthings.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class PercentageProgressBar extends View
{
    private Paint TextPaint;
    private Paint ArcPaint;
    private int Percentage = 0;
    private float StrokeWidth = 9f;
    private float FontSize = 40f;

    public PercentageProgressBar(Context context)
    {
        super(context);
        InitPaintResources();
    }

    public PercentageProgressBar(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        InitPaintResources();
    }

    public PercentageProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitPaintResources();
    }

    private void InitPaintResources()
    {
        TextPaint = new Paint();
        ArcPaint = new Paint();
        TextPaint.setStyle(Paint.Style.STROKE);
        ArcPaint.setStyle(Paint.Style.STROKE);
        ArcPaint.setStrokeWidth(StrokeWidth);
        ArcPaint.setColor(Color.WHITE);
        TextPaint.setColor(Color.WHITE);
        TextPaint.setTextSize(FontSize);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int R = Math.min(width, height);
        float r = (float) R / 2;

        float beginX, beginY, endX, endY;
        if (R == width)
        {
            beginX = 0;
            beginY = (float) height / 2 - (float) width / 2;
            endX = width;
            endY = (float) height / 2 + (float) width / 2;
        } else
        {
            beginX = (float) width / 2 - (float) height / 2;
            beginY = 0;
            endX = (float) width / 2 + (float) height / 2;
            endY = height;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(beginX + StrokeWidth, beginY + StrokeWidth, endX - StrokeWidth, endY - StrokeWidth, 270, CircleArcPercentage(Percentage), false, ArcPaint);
        }

        String PercentageStr = Percentage + "%";
        float textShowWidth = TextPaint.measureText(PercentageStr);
        Paint.FontMetrics fontMetrics = TextPaint.getFontMetrics();
        float dy = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        canvas.drawText(PercentageStr, (float) R / 2 - textShowWidth / 2, (float) R / 2 + dy, TextPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureWidth(widthMeasureSpec), MeasureHeight(heightMeasureSpec));
    }

    private int MeasureWidth(int WidthMeasureSpec)
    {
        return MeasureSizeParams(WidthMeasureSpec);
    }

    private int MeasureHeight(int HeightMeasureSpec)
    {
        return MeasureSizeParams(HeightMeasureSpec);
    }

    private int MeasureSizeParams(int HeightMeasureSpec)
    {
        int result = 0;
        int Mode = MeasureSpec.getMode(HeightMeasureSpec);
        int Size = MeasureSpec.getSize(HeightMeasureSpec);
        if (Mode == MeasureSpec.EXACTLY)
        {
            return Size;
        } else
        {
            result = 200;
            if (Mode == MeasureSpec.AT_MOST)
            {
                result = Math.min(Size, result);
            }
        }
        return result;
    }

    private float CircleArcPercentage(int NormalPercentage)
    {
        return ((float) NormalPercentage / 100) * 360;
    }

    public int getPercentage()
    {
        return Percentage;
    }

    public void setPercentage(int percentage)
    {
        Percentage = percentage;
        invalidate();
    }
}
