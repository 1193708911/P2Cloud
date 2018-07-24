package movi.ui.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.ctvit.p2cloud.R;


/**
 * Widget that lets users select a minimum and maximum value on a given
 * numerical range. The range value types can be one of Long, Double, Integer,
 * Float, Short, Byte or BigDecimal.<br />
 * <br />
 * Improved {@link MotionEvent} handling for smoother use, anti-aliased painting
 * for improved aesthetics.
 *
 * @param <T> The Number type of the range values. One of Long, Double, Integer,
 *            Float, Short, Byte or BigDecimal.
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 * @author Thomas Barrasso (tbarrasso@sevenplusandroid.org)
 */
public class RangeSeekBar<T extends Number> extends ImageView {


	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	public final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	public Bitmap minThumb = BitmapFactory.decodeResource(
			getResources(), R.mipmap.ic_normal);
	public Bitmap normalThumb = BitmapFactory.decodeResource(
			getResources(), R.mipmap.ic_seek);
	public Bitmap maxThumb = BitmapFactory.decodeResource(
			getResources(), R.mipmap.ic_normal);
	private final float thumbWidth = minThumb.getWidth();
	private final float thumbHalfWidth = 0.5f * thumbWidth;
	private final float thumbHalfHeight = 0.5f * minThumb.getHeight();
	private final float lineHeight = 0.2f * thumbHalfHeight;
	private final float padding = thumbHalfWidth;
	private T absoluteMinValue, absoluteMaxValue, absoluteNormalValue;

	private NumberType numberType;
	private double absoluteMinValuePrim;
	private double absoluteNormalValuePrim;
	private double absoluteMaxValuePrim;
	private double normalizedMinValue = 0d;

	private double normalizedNormalValue = 0d;
	private double normalizedMaxValue = 1d;
	private Thumb pressedThumb = null;
	private boolean notifyWhileDragging = false;
	private OnRangeSeekBarChangeListener<T> listener;
	private boolean isInvisible = false;
	private boolean isTouched = false;
	private float currentPosition = 0.0f;
	public static int min = 0;
	public static int normal = 0;
	public static int max = 100;
	private float startPosition = 20f;
	private float maxPosition = 180f;
	public String minText = "00:00:00:00";
	public String maxText = "00:00:00:00";
	private boolean isMinValue = false;
	private boolean isMaxValue = false;

	private  boolean  isNoCanTouch;

	public  void  setIsNoCanTouch(boolean  isCanTouch){
		this.isNoCanTouch=isCanTouch;
	}


	/**
	 * Default color of a {@link RangeSeekBar}, #FF33B5E5. This is also known as
	 * "Ice Cream Sandwich" blue.
	 */
	public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

	/**
	 * An invalid pointer id.
	 */
	public static final int INVALID_POINTER_ID = 255;

	// Localized constants from MotionEvent for compatibility
	// with API < 8 "Froyo".
	public static final int ACTION_POINTER_UP = 0x6,
			ACTION_POINTER_INDEX_MASK = 0x0000ff00,
			ACTION_POINTER_INDEX_SHIFT = 8;

	private float mDownMotionX;
	private int mActivePointerId = INVALID_POINTER_ID;

	/**
	 * On touch, this offset plus the scaled value from the position of the
	 * touch will form the progress value. Usually 0.
	 */
	float mTouchProgressOffset;

	private int mScaledTouchSlop;
	private boolean mIsDragging;

	/**
	 * 声明值作为判断
	 */
	private T currentMAX;
	private T currentMin;
	/**
	 * 声明状态值
	 */
	public static final int STATE_MAX = 2;
	public static final int STATE_MIN = 0;
	public static final int STATE_NORMAL = 1;
	public static  final  int  STATE_START=3;
	/**
	 * 构造方法
	 *
	 * @param context
	 */
	public RangeSeekBar(Context context,T absoluteMinValue, T absoluteNormalValue, T absoluteMaxValue) {
		super(context);
		this.absoluteMinValue = absoluteMinValue;
		this.absoluteNormalValue = absoluteNormalValue;
		this.absoluteMaxValue = absoluteMaxValue;

		absoluteMinValuePrim = absoluteMinValue.doubleValue();
		absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
		absoluteNormalValuePrim = absoluteNormalValue.doubleValue();
		numberType = NumberType.fromNumber(absoluteMinValue);
		//初始化要判断的状态值
		this.currentMAX = absoluteMaxValue;
		this.currentMin = absoluteMinValue;

		this.maxText = formatDuration(absoluteMaxValue.longValue() / 40);
		// make RangeSeekBar focusable. This solves focus handling issues in
		// case EditText widgets are being used along with the RangeSeekBar
		// within ScollViews.
		setFocusable(true);
		setFocusableInTouchMode(true);
		init();
	}

	/**
	 * 初始化相应的值
	 *
	 * @param absoluteMinValue
	 * @param absoluteNormalValue
	 * @param absoluteMaxValue
	 * @throws IllegalArgumentException
	 */
	public void setRangeSeekBarParam(T absoluteMinValue, T absoluteNormalValue, T absoluteMaxValue)
			throws IllegalArgumentException {
		this.absoluteMinValue = absoluteMinValue;
		this.absoluteNormalValue = absoluteNormalValue;
		this.absoluteMaxValue = absoluteMaxValue;

		absoluteMinValuePrim = absoluteMinValue.doubleValue();
		absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
		absoluteNormalValuePrim = absoluteNormalValue.doubleValue();
		numberType = NumberType.fromNumber(absoluteMinValue);
		//初始化要判断的状态值
		this.currentMAX = absoluteMaxValue;
		this.currentMin = absoluteMinValue;

		this.maxText = formatDuration(absoluteMaxValue.longValue() / 40);
		// make RangeSeekBar focusable. This solves focus handling issues in
		// case EditText widgets are being used along with the RangeSeekBar
		// within ScollViews.
		setFocusable(true);
		setFocusableInTouchMode(true);
		init();
	}

	private final void init() {
		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
		invalidate();
	}

	public boolean isNotifyWhileDragging() {
		return notifyWhileDragging;
	}

	/**
	 * Should the widget notify the listener callback while the user is still
	 * dragging a thumb? Default is false.
	 *
	 * @param flag
	 */
	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}

	/**
	 * Returns the absolute minimum value of the range that has been set at
	 * construction time.
	 *
	 * @return The absolute minimum value of the range.
	 */
	public T getAbsoluteMinValue() {
		return absoluteMinValue;
	}

	/**
	 * Returns the absolute maximum value of the range that has been set at
	 * construction time.
	 *
	 * @return The absolute maximum value of the range.
	 */
	public T getAbsoluteMaxValue() {
		return absoluteMaxValue;
	}

	/**
	 * Returns the currently selected min value.
	 *
	 * @return The currently selected min value.
	 */
	public T getSelectedMinValue() {
		return normalizedToValue(normalizedMinValue);
	}

	/**
	 * 计算中间的有多大
	 *
	 * @return
	 */
	public T getSelectedNormalValue() {
		return normalizedToValue(normalizedNormalValue);
	}

	/**
	 * Sets the currently selected minimum value. The widget will be invalidated
	 * and redrawn.
	 *
	 * @param value The Number value to set the minimum value to. Will be clamped
	 *              to given absolute minimum/maximum range.
	 */
	public void setSelectedMinValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMinValue(0d);
		} else {
			setNormalizedMinValue(valueToNormalized(value));
		}
	}

	/**
	 * 设置中间Thumb选中的位置
	 *
	 * @param
	 */

	public void setSelectedNormalValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteNormalValuePrim)) {
			setNormalizedNormalValue(0d);
		} else {
			setNormalizedNormalValue(valueToNormalized(value));
		}
	}


	/**
	 * Returns the currently selected max value.
	 *
	 * @return The currently selected max value.
	 */
	public T getSelectedMaxValue() {
		return normalizedToValue(normalizedMaxValue);
	}

	/**
	 * Sets the currently selected maximum value. The widget will be invalidated
	 * and redrawn.
	 *
	 * @param value The Number value to set the maximum value to. Will be clamped
	 *              to given absolute minimum/maximum range.
	 */
	public void setSelectedMaxValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMaxValue(1d);
		} else {
			setNormalizedMaxValue(valueToNormalized(value));
		}
	}

	/**
	 * Registers given listener callback to notify about changed selected
	 * values.
	 *
	 * @param listener The listener to notify about changed selected values.
	 */
	public void setOnRangeSeekBarChangeListener(
			OnRangeSeekBarChangeListener<T> listener) {
		this.listener = listener;
	}

	/**
	 * Handles thumb selection and movement. Notifies listener callback on
	 * certain events.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isNoCanTouch){
			return false;
		}

		if (!isEnabled())
			return false;

		int pointerIndex;

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			// Remember where the motion event started
			isTouched = true;
			mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
			pointerIndex = event.findPointerIndex(mActivePointerId);
			mDownMotionX = event.getX(pointerIndex);
			pressedThumb = evalPressedThumb(mDownMotionX);

			// Only handle thumb presses.
			if (pressedThumb == null)
				return super.onTouchEvent(event);
			if(listener!=null){
				listener.onRangeSeekBarValuesChanged(this,getSelectedMinValue(), getSelectedNormalValue(), getSelectedMaxValue(), STATE_START);
			}
			setPressed(true);
			invalidate();
			onStartTrackingTouch();
			trackTouchEvent(event);
			attemptClaimDrag();

			break;
		case MotionEvent.ACTION_MOVE:
			if (pressedThumb != null) {

				if (mIsDragging) {
					trackTouchEvent(event);
				} else {
					// Scroll to follow the motion event
					pointerIndex = event.findPointerIndex(mActivePointerId);
					final float x = event.getX(pointerIndex);

					if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {

						setPressed(true);
						invalidate();
						onStartTrackingTouch();
						trackTouchEvent(event);
						attemptClaimDrag();
					}
				}

			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsDragging) {
				trackTouchEvent(event);
				onStopTrackingTouch();
				setPressed(false);
			} else {
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();
			}

			//                pressedThumb = null;
			//                currentMin = getSelectedMinValue();
			//                currentMAX = getSelectedMaxValue();
			if (listener != null) {
				if (isMinValue && Thumb.MIN == pressedThumb) {
					listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedNormalValue(), getSelectedMaxValue(), STATE_MIN);
					//                        LogUtil.d("当前事件" + "UO事件" + "States" + STATE_MIN);
				}
				if (isMaxValue && Thumb.MAX == pressedThumb) {
					listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedNormalValue(), getSelectedMaxValue(), STATE_MAX);
					//                        LogUtil.d("当前事件" + "UO事件" + "States" + STATE_MAX);
				}
				currentMin = getSelectedMinValue();
				currentMAX = getSelectedMaxValue();
				isMaxValue = false;
				isMinValue = false;
			}
			break;

		}
		isTouched = false;
		return true;

	}


	private final void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);
		if (pointerIndex < 0) {
			return;
		}
		final float x = event.getX(pointerIndex);
		if (Thumb.MIN.equals(pressedThumb)) {
			setNormalizedMinValue(screenToNormalized(x));
		} else if (Thumb.MAX.equals(pressedThumb)) {
			setNormalizedMaxValue(screenToNormalized(x));
		}
		//		else  if(Thumb.NORMAL.equals(pressedThumb)){
		//			setNormalizedNormalValue(screenToNormalized(x));
		//		}
	}

	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * This is called when the user has started touching this widget.
	 */
	void onStartTrackingTouch() {
		mIsDragging = true;
	}

	/**
	 * This is called when the user either releases his touch or the touch is
	 * canceled.
	 */
	void onStopTrackingTouch() {
		mIsDragging = false;
	}

	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		int width = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		int height = minThumb.getHeight() + 60;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	/**
	 * Draws the widget on the given canvas.
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		textPaint.setTextSize(30);
		textPaint.setColor(Color.WHITE);
		// draw seek bar background line
		final RectF rect = new RectF(padding,
				0.5f * (getHeight() - lineHeight), getWidth() - padding,
				0.5f * (getHeight() + lineHeight));
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GRAY);
		paint.setAntiAlias(true);
		canvas.drawRect(rect, paint);

		// draw seek bar active range line
		rect.left = normalizedToScreen(normalizedMinValue);
		rect.right = normalizedToScreen(normalizedMaxValue);

		// orange color
		//      paint.setColor(DEFAULT_COLOR);
		paint.setColor(Color.RED);
		canvas.drawRect(rect, paint);

		// draw minimum thumb
		drawMinThumb(normalizedToScreen(normalizedMinValue),
				Thumb.MIN.equals(pressedThumb), canvas);


		// draw maximum thumb
		drawMaxThumb(normalizedToScreen(normalizedMaxValue),
				Thumb.MAX.equals(pressedThumb), canvas);

		if (isInvisible) {
			//draw  normal thumb
			drawNormalthumb(normalizedToScreen(normalizedNormalValue),
					Thumb.NORMAL.equals(pressedThumb), canvas);
		}
		if (listener != null) {
			if (!String.valueOf(currentMin).equals(String.valueOf(getSelectedMinValue()))) {
				listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedNormalValue(), getSelectedMaxValue(), STATE_NORMAL);
				isMinValue = true;
			}
			if (!String.valueOf(currentMAX).equals(String.valueOf(getSelectedMaxValue()))) {
				listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedNormalValue(), getSelectedMaxValue(), STATE_NORMAL);
				isMaxValue = true;
			}
			currentMin = getSelectedMinValue();
			currentMAX = getSelectedMaxValue();
		}


	}

	/**
	 * Overridden to save instance state when device orientation changes. This
	 * method is called automatically if you assign an id to the RangeSeekBar
	 * widget using the {@link #setId(int)} method. Other members of this class
	 * than the normalized min and max values don't need to be saved.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putDouble("MIN", normalizedMinValue);
		bundle.putDouble("MAX", normalizedMaxValue);
		bundle.putDouble("NORMAL", normalizedNormalValue);
		return bundle;
	}

	/**
	 * Overridden to restore instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the
	 * RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
		normalizedMinValue = bundle.getDouble("MIN");
		normalizedMaxValue = bundle.getDouble("MAX");
		normalizedNormalValue = bundle.getDouble("NORMAL");
	}

	/**
	 * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
	 *
	 * @param screenCoord
	 *            The x-coordinate in screen space where to draw the image.
	 * @param pressed
	 *            Is the thumb currently in "pressed" state?
	 * @param canvas
	 *            The canvas to draw upon.
	 */


	/**
	 * 绘制最小的thumb
	 *
	 * @param screenCoord
	 * @param pressed
	 * @param canvas
	 */
	private void drawMinThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(minThumb, screenCoord
				- thumbHalfWidth,
				(float) ((0.5f * getHeight()) - thumbHalfHeight), paint);

		float sp = startPosition;
		if (screenCoord > maxPosition) {
			sp = screenCoord - (maxPosition - startPosition);
		}
		if (getWidth() - screenCoord < maxPosition + startPosition) {
			sp = getWidth() - 2 * (maxPosition);
		}
		canvas.drawText(minText, sp, (float) (getHeight()), textPaint);
	}

	/**
	 * 画中间的Thumb
	 *
	 * @param screenCoord
	 * @param pressed
	 * @param canvas
	 */
	private void drawNormalthumb(float screenCoord, boolean pressed, Canvas canvas) {
		//		canvas.drawBitmap(normalThumb, screenCoord
		//				- thumbHalfWidth,
		//				(float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
		if (!isTouched) {
			canvas.drawBitmap(normalThumb, screenCoord
					- thumbHalfWidth,
					(float) ((0.5f * getHeight()) - thumbHalfHeight) + 22, paint);
		}


	}

	/**
	 * 画最大的thumb
	 *
	 * @param screenCoord
	 * @param pressed
	 * @param canvas
	 */
	private void drawMaxThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(maxThumb, screenCoord
				- thumbHalfWidth,
				(float) ((0.5f * getHeight()) - thumbHalfHeight), paint);

		float sp = getWidth() - maxPosition;
		if (getWidth() - screenCoord > maxPosition) {
			sp = screenCoord - thumbHalfWidth + startPosition;
		}
		if (sp < maxPosition + startPosition + 10) {
			sp = maxPosition + startPosition;
		}
		canvas.drawText(maxText, sp, (float) (getHeight()), textPaint);
	}

	/**
	 * Decides which (if any) thumb is touched by the given x-coordinate.
	 *
	 * @param touchX The x-coordinate of a touch event in screen space.
	 * @return The pressed thumb or null if none has been touched.
	 */
	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;
		boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
		boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
		boolean normalThumbpressed = isInThumbRange(touchX, normalizedNormalValue);

		if (minThumbPressed && maxThumbPressed && normalThumbpressed) {
			// if both thumbs are pressed (they lie on top of each other),
			// choose the one with more room to drag. this avoids "stalling" the
			// thumbs in a corner, not being able to drag them apart anymore.
			result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
		} else if (minThumbPressed) {
			result = Thumb.MIN;
		} else if (maxThumbPressed) {
			result = Thumb.MAX;
		} else if (normalThumbpressed) {
			result = Thumb.NORMAL;
		}
		return result;
	}

	/**
	 * Decides if given x-coordinate in screen space needs to be interpreted as
	 * "within" the normalized thumb x-coordinate.
	 *
	 * @param touchX               The x-coordinate in screen space to check.
	 * @param normalizedThumbValue The normalized x-coordinate of the thumb to check.
	 * @return true if x-coordinate is in thumb range, false otherwise.
	 */
	private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
		return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
	}

	/**
	 * Sets normalized min value to value so that 0 <= value <= normalized max
	 * value <= 1. The View will get invalidated when calling this method.
	 *
	 * @param value The new normalized min value to set.
	 */
	public void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d,
				Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}

	/**
	 * 设置中间的thumb的
	 *
	 * @param value
	 */
	public void setNormalizedNormalValue(double value) {
		//thumb移动
		normalizedNormalValue = Math.max(0d,
				Math.min(1d, Math.min(value, 1000)));
		invalidate();
	}

	/**
	 * Sets normalized max value to value so that 0 <= normalized min value <=
	 * value <= 1. The View will get invalidated when calling this method.
	 *
	 * @param value The new normalized max value to set.
	 */
	public void setNormalizedMaxValue(double value) {
		normalizedMaxValue = Math.max(0d,
				Math.min(1d, Math.max(value, normalizedMinValue)));
		invalidate();
	}

	/**
	 * Converts a normalized value to a Number object in the value space between
	 * absolute minimum and maximum.
	 *
	 * @param normalized
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T normalizedToValue(double normalized) {
		return (T) numberType.toNumber(absoluteMinValuePrim + normalized
				* (absoluteMaxValuePrim - absoluteMinValuePrim));
	}

	/**
	 * Converts the given Number value to a normalized double.
	 *
	 * @param value The Number value to normalize.
	 * @return The normalized double.
	 */
	private double valueToNormalized(T value) {
		if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		return (value.doubleValue() - absoluteMinValuePrim)
				/ (absoluteMaxValuePrim - absoluteMinValuePrim);
	}

	/**
	 * Converts a normalized value into screen space.
	 *
	 * @param normalizedCoord The normalized value to convert.
	 * @return The converted value in screen space.
	 */
	private float normalizedToScreen(double normalizedCoord) {
		return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
	}

	/**
	 * Converts screen space x-coordinates into normalized values.
	 *
	 * @param screenCoord The x-coordinate in screen space to convert.
	 * @return The normalized value.
	 */
	private double screenToNormalized(float screenCoord) {
		int width = getWidth();
		if (width <= 2 * padding) {
			// prevent division by zero, simply return 0.
			return 0d;
		} else {
			double result = (screenCoord - padding) / (width - 2 * padding);
			return Math.min(1d, Math.max(0d, result));
		}
	}

	/**
	 * Callback listener inter to notify about changed range values.
	 *
	 * @param <T> The Number type the RangeSeekBar has been declared with.
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 */
	public interface OnRangeSeekBarChangeListener<T> {
		public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
												T minValue, T normalValue, T maxValue, int state);
	}

	/**
	 * Thumb constants (min and max).
	 */
	public static enum Thumb {
		MIN, MAX, NORMAL
	}

	;

	/**
	 * Utility enumaration used to convert between Numbers and doubles.
	 *
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 */
	private static enum NumberType {
		LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

		public static <E extends Number> NumberType fromNumber(E value)
				throws IllegalArgumentException {
			if (value instanceof Long) {
				return LONG;
			}
			if (value instanceof Double) {
				return DOUBLE;
			}
			if (value instanceof Integer) {
				return INTEGER;
			}
			if (value instanceof Float) {
				return FLOAT;
			}
			if (value instanceof Short) {
				return SHORT;
			}
			if (value instanceof Byte) {
				return BYTE;
			}
			if (value instanceof BigDecimal) {
				return BIG_DECIMAL;
			}
			throw new IllegalArgumentException("Number class '"
					+ value.getClass().getName() + "' is not supported");
		}

		public Number toNumber(double value) {
			switch (this) {
			case LONG:
				return new Long((long) value);
			case DOUBLE:
				return value;
			case INTEGER:
				return new Integer((int) value);
			case FLOAT:
				return new Float(value);
			case SHORT:
				return new Short((short) value);
			case BYTE:
				return new Byte((byte) value);
			case BIG_DECIMAL:
				return new BigDecimal(value);
			}
			throw new InstantiationError("can't convert " + this
					+ " to a Number object");
		}
	}

	public T getAbsoluteNormalValue() {
		return absoluteNormalValue;
	}

	/**
	 * 设置最大的max
	 */
	public void setAbsoluteMaxValue(T value) {
		this.absoluteMaxValue = value;
	}

	/**
	 * 设置是否绘制中间的thumb
	 */
	public void setInvisibleThumb(boolean isVisible) {
		this.isInvisible = isVisible;
		invalidate();
	}

	/**
	 * 设置最小的thumb的改变时的背景
	 *
	 * @param resId
	 */

	public void setMinThumbRes(int resId) {
		minThumb = BitmapFactory.decodeResource(getResources(), resId);
		invalidate();
	}

	public void setMaxThumbRes(int resId) {
		maxThumb = BitmapFactory.decodeResource(getResources(), resId);
		invalidate();
	}

	/**
	 * 设置中间的value到最小的value中
	 * 关键点
	 *
	 * @param value
	 */
	public void setNormalValue2Min(T value) {
		setSelectedNormalValue(value);
		invalidate();
	}

	/**
	 * 不断的绘制thumb
	 * 测试
	 */
	public synchronized void setProgress(final T value) {
		setSelectedNormalValue(value);
		invalidate();
		//        LogUtil.d("当前的进度为" + value);
	}

	/**
	 * 向后移动40毫秒的距离
	 */
	public void setMinWard(T mills) {
		setSelectedMinValue(mills);
		invalidate();
	}

	/**
	 * 向前移动40毫秒的距离
	 */
	public void setMaxWard(T mills) {
		setSelectedMaxValue(mills);
		invalidate();

	}

	/**
	 * 帧值 转化成 时:分:秒:帧
	 *
	 * @param frame 帧值
	 * @return
	 */
	public static String formatDuration(long frame) {
		if (frame < 0) {
			frame = 0;
		}
		StringBuilder duration = new StringBuilder();
		DecimalFormat decimal = new DecimalFormat("00");
		for (long rate = 3600, second = frame / 25; rate > 0; second %= rate, rate /= 60) {
			duration.append(decimal.format(second / rate)).append(":");
		}
		duration.append(decimal.format(frame % 25));
		return duration.toString();
	}

	/**
	 * 设置最大值最小值
	 *
	 * @param minvalue
	 * @param maxValue
	 */

	public void setMinAndMaxValue(T minvalue, T maxValue) {
		setSelectedMinValue(minvalue);
		setSelectedMaxValue(maxValue);
	}


}
