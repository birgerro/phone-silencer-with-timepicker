package birger.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

public class DurationPicker extends FrameLayout implements NumberPicker.Formatter, NumberPicker.OnValueChangeListener {
	private NumberPicker hours_picker;
	private NumberPicker minutes_picker;
	
	private int minutes_interval = 5;
	
	public interface OnDurationChangedListener {
		public void onDurationChanged(DurationPicker which, int num_hours, int num_minutes);
	}
	
	private OnDurationChangedListener registered_listener;
	
	private static final OnDurationChangedListener DO_NOTHING = new OnDurationChangedListener() {
		public void onDurationChanged(DurationPicker which, int num_hours, int num_minutes) {
			// Do nothing
		}
	};
	
	public DurationPicker(Context context) {
		this(context, null);
	}
	
	public DurationPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnDurationChangedListener(DO_NOTHING);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.duration_picker, this, true);
		
        hours_picker = (NumberPicker) findViewById(R.id.hours_picker);
        hours_picker.setMinValue(0);
        hours_picker.setMaxValue(10);
		hours_picker.setFormatter(this);
		hours_picker.setOnValueChangedListener(this);
        hours_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hours_picker.setWrapSelectorWheel(false);
        
        minutes_picker = (NumberPicker) findViewById(R.id.minutes_picker);
		minutes_picker.setOnValueChangedListener(this);
        minutes_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setMinutesInterval(5);
	}
	
	public void setOnDurationChangedListener(OnDurationChangedListener listener) {
		if ( listener != null ) {
			registered_listener = listener;
		} else {
			registered_listener = DO_NOTHING;
		}
	}
	
	public void setDuration(int hours, int minutes) {
		setHours(hours);
		setMinutes(minutes);
	}
	
	public int getHours() {
		return hours_picker.getValue();
	}
	
	public void setHours(int hours) {
		hours_picker.setValue(hours);
	}
	
	public void setMaxHours(int new_max) {
		hours_picker.setMaxValue(new_max);
        hours_picker.setWrapSelectorWheel(false);
	}

	public int getMinutes() {
		return minutes_picker.getValue() * minutes_interval;
	}
	
	public void setMinutes(int minutes) {
		minutes_picker.setValue(Math.round(((float) minutes)/minutes_interval));
	}
	
	public void setMinutesInterval(int interval) {
		int current_minutes = getMinutes();
		minutes_picker.setValue(0);
		minutes_interval = interval;

		String[] minutes = new String[60/minutes_interval];
        for ( int i = 0; i < minutes.length; i++ ) {
        	minutes[i] = format(i * minutes_interval);
        }
        minutes_picker.setDisplayedValues(minutes);
		minutes_picker.setMinValue(0);
		minutes_picker.setMaxValue(minutes.length-1);
        minutes_picker.setWrapSelectorWheel(false);
        minutes_picker.setValue(Math.round((float)current_minutes/minutes_interval));
	}

	// For NumberPicker.Formatter interface:
    public String format(int value) {
    	// Format number with two digits:
		if ( value < 10 ) {
			return "0" + Integer.toString(value);
		} else {
			return Integer.toString(value);
		}
	}
    
    // For NumberPicker.OnValueChangeListener interface:
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		registered_listener.onDurationChanged(this, getHours(), getMinutes());
	}
}
