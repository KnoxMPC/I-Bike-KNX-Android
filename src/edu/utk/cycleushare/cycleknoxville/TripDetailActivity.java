package edu.utk.cycleushare.cycleknoxville;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TripDetailActivity extends Activity {
	long tripid;
	String purpose = "";
	EditText notes;

	private int transitUsed = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_detail);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		finishRecording();
		purpose = "";
		Intent myIntent = getIntent(); // gets the previously created intent
		//purpose = myIntent.getStringExtra("purpose");
		notes = (EditText) findViewById(R.id.tripNoteEdit);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	// submit btn is only activated after the service.finishedRecording() is
	// completed.
	void submit(String notesToUpload) {

		Spinner purpose_spinner = (Spinner) findViewById(R.id.tripPurposeSpinner);

		String purpose = purpose_spinner.getSelectedItem().toString();

		if (purpose_spinner.getSelectedItemPosition() == 0) {
			Toast.makeText(getBaseContext(), "Please select a trip purpose.", Toast.LENGTH_SHORT).show();
			return;
		} else if (transitUsed == -1) {
			Toast.makeText(getBaseContext(), "Please indicate whether or not transit was used.", Toast.LENGTH_SHORT).show();
			return;
		}

		if (transitUsed == 1) {
			notesToUpload += "|took_transit";
		}


		final Intent xi = new Intent(this, TripMapActivity.class);

		TripData trip = TripData.fetchTrip(TripDetailActivity.this, tripid);
		trip.populateDetails();

		SimpleDateFormat sdfStart = new SimpleDateFormat("MMMM d, y  HH:mm", Locale.US);
		String fancyStartTime = sdfStart.format(trip.startTime);
		Log.v("Jason", "Start: " + fancyStartTime);

		// "3.5 miles in 26 minutes"
		SimpleDateFormat sdf = new SimpleDateFormat("m", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String minutes = sdf.format(trip.endTime - trip.startTime);
		String fancyEndInfo = String.format(Locale.US, "%1.1f miles, %s minutes.  %s",
				(0.0006212f * trip.distance), minutes, notesToUpload);

		// Save the trip details to the phone database. W00t!

		trip.updateTrip(purpose, fancyStartTime, fancyEndInfo, notesToUpload);
		trip.updateTripStatus(TripData.STATUS_COMPLETE);
		resetService();

		// Now create the MainInput Activity so BACK btn works properly
		Intent i = new Intent(getApplicationContext(), TabsConfig.class);
		startActivity(i);

		// And, show the map!
		xi.putExtra("showtrip", trip.tripid);
		xi.putExtra("uploadTrip", true);
		Log.v("Jason", "Tripid: " + String.valueOf(trip.tripid));
		startActivity(xi);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		TripDetailActivity.this.finish();

	}

	void finishRecording() {
		Intent rService = new Intent(this, RecordingService.class);
		ServiceConnection sc = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				IRecordService rs = (IRecordService) service;
				tripid = rs.finishRecording();
				// TripDetailActivityOld.this.activateSubmitButton();
				unbindService(this);
			}
		};
		// This should block until the onServiceConnected (above) completes.
		bindService(rService, sc, Context.BIND_AUTO_CREATE);
	}

	void resetService() {
		Intent rService = new Intent(this, RecordingService.class);
		ServiceConnection sc = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				IRecordService rs = (IRecordService) service;
				rs.reset();
				unbindService(this);
			}
		};
		// This should block until the onServiceConnected (above) completes.
		bindService(rService, sc, Context.BIND_AUTO_CREATE);
	}

	/* Creates the menu items */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.trip_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_cancel_trip_detail:
				switchToMain();
				return true;

			case R.id.action_submit_trip_detail:
				// save
				submit(notes.getEditableText().toString());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	// 2.0 and above
	@Override
	public void onBackPressed() {
		switchToMain();
	}

	// Before 2.0
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switchToMain();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	void cancelRecording() {
		Intent rService = new Intent(this, edu.utk.cycleushare.cycleknoxville.RecordingService.class);
		ServiceConnection sc = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				edu.utk.cycleushare.cycleknoxville.IRecordService rs = (edu.utk.cycleushare.cycleknoxville.IRecordService) service;
				rs.cancelRecording();
				unbindService(this);
			}
		};
		// This should block until the onServiceConnected (above) completes.
		bindService(rService, sc, Context.BIND_AUTO_CREATE);
	}

	public void onRadioButtonClicked(View view) {
		switch (view.getId()) {
			case R.id.radioTransitNo:
				Log.v("trip", "transitUsed = 0");
				transitUsed = 0;
				break;
			case R.id.radioTransitYes:
				Log.v("trip", "transitUsed = 1");
				transitUsed = 1;
				break;
			default:
				Log.v("trip", "transitUsed = " + view.toString() + "???");
				break;
		}
	}

	private void switchToMain() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						cancelRecording();
						Intent i = new Intent(TripDetailActivity.this, edu.utk.cycleushare.cycleknoxville.TabsConfig.class);
						i.putExtra("keepme", true);
						startActivity(i);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						TripDetailActivity.this.finish();

						Toast.makeText(getBaseContext(), "Trip discarded.",
								Toast.LENGTH_SHORT).show();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Discard the recorded trip?").setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();


	}
}
