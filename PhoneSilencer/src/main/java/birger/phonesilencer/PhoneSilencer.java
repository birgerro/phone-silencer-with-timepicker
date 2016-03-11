package birger.phonesilencer;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import birger.widgets.DurationPicker;

public class PhoneSilencer extends Activity implements DurationPicker.OnDurationChangedListener, TimePicker.OnTimeChangedListener {
	private DurationPicker duration;
	private TimePicker endpoint;
	private Button button;
	private CheckBox allow_vibration;
	private boolean duration_mode;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        setContentView(R.layout.dialog_view);
        
        button = (Button) findViewById(R.id.button);
        allow_vibration = (CheckBox) findViewById(R.id.allow_vibration);
        
        duration = (DurationPicker) findViewById(R.id.duration_picker);
        duration.setOnDurationChangedListener(this);

        endpoint = (TimePicker) findViewById(R.id.time_picker);
        endpoint.setIs24HourView(true);
        endpoint.setOnTimeChangedListener(this);
        Calendar now = Calendar.getInstance();
        endpoint.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        endpoint.setCurrentMinute(now.get(Calendar.MINUTE));

        // Trigger first button update:
        duration_mode = true;
        duration.setDuration(2, 0);
        onDurationChanged(null, 2, 0);
    }
    
    public void buttonClicked(View view) {
    	
    	printSilencePeriode();
    	
    	if ( allow_vibration.isChecked() ) {
    		((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    	} else {
    		((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
    	}
    	
        Calendar end = Calendar.getInstance();
    	if ( duration_mode ) {
    		end.add(Calendar.HOUR_OF_DAY, duration.getHours());
    		end.add(Calendar.MINUTE, duration.getMinutes());
    	} else {
    		end.set(Calendar.HOUR_OF_DAY, endpoint.getCurrentHour());
    		end.set(Calendar.MINUTE, endpoint.getCurrentMinute());
    		end.set(Calendar.SECOND, 0);
        	Calendar now = Calendar.getInstance();
            if ( end.before(now) ){
            	end.roll(Calendar.DATE, +1);
            }
    	}
    	
		Intent intent = new Intent(getBaseContext(), ResetRingerMode.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Get the AlarmManager service
        ((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, end.getTimeInMillis(), sender);

        finish();
    }

    private void printSilencePeriode() {
    	String text = "The phone will be silenced";
    	if ( duration_mode ) {
    		text += " for";
    		if ( duration.getHours() > 0 ) {
    			text += " " + duration.getHours() + " h";
    		}
    		if ( duration.getMinutes() > 0 ) {
    			text += " " + duration.getMinutes() + " min";
    		}
    	} else {
    		text += " until " + duration.format(endpoint.getCurrentHour()) + ":" + duration.format(endpoint.getCurrentMinute());
    	}
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();    	
    }
    
    // For DurationPicker.OnDurationChangedListener interface:
	public void onDurationChanged(DurationPicker which, int num_hours, int num_minutes) {
		duration_mode = true;
		if ( num_hours == 0 && num_minutes == 0 ){
			onTimeChanged(endpoint, endpoint.getCurrentHour(), endpoint.getCurrentMinute());
		} else {
			String text = "Silence phone for";
			if ( num_hours > 0 ){
				text += " " + num_hours + " h";
			}
			if ( num_minutes > 0 ){
				text += " " + duration.format(num_minutes) + " min";
			}
			button.setText(text);
			button.invalidate();
		}
	}

	// For TimePicker.OnTimeChangedListener interface:
	public void onTimeChanged(TimePicker view, int hour, int minute) {
		duration_mode = false;
		button.setText("Silence phone until " + duration.format(hour) + ":" + duration.format(minute));
		button.invalidate();
	}
	
	// For BroadcastReceiver:
	public static class ResetRingerMode extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
	    	((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    	}
	}
}