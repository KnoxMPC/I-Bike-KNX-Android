package edu.utk.cycleushare.cycleknoxville;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.utk.cycleushare.cycleknoxville.*;

public class NoteTypeActivity extends Activity {
	// HashMap<Integer, ToggleButton> purpButtons = new HashMap<Integer,
	// ToggleButton>();
	int noteType;

	long noteid;

	int isRecording;

	HashMap<Integer, String> noteTypeDescriptions = new HashMap<Integer, String>();

	String[] values;

	private MenuItem saveMenuItem;

	// Set up the purpose buttons to be one-click only
	void prepareNoteTypeButtons() {
		// Note Issue
		noteTypeDescriptions
				.put(NoteData.NoteType.pavementIssue.num, // 0,
						"Here's a spot where the road needs to be repaired (pothole, rough concrete, gravel in the road, manhole cover, sewer grate).");
		noteTypeDescriptions
				.put(NoteData.NoteType.trafficSignal.num, // 1,
				"Here's a signal that you can't activate with your bike.");
		noteTypeDescriptions
				.put(NoteData.NoteType.enforcement.num, // 2,
						"The bike lane is always blocked here, cars disobey \"no right on red\"; anything where the cops can help make cycling safer.");
		noteTypeDescriptions
				.put(NoteData.NoteType.bikeLaneIssue.num, // 4,
						"Where the bike lane ends (abruptly) or is too narrow (pesky parked cars).");
		noteTypeDescriptions
				.put(NoteData.NoteType.noteThisIssue.num, // 5,
						"Anything else ripe for improvement: want a sharrow, a sign, a bike lane? Share the details.");
		noteTypeDescriptions
				.put(NoteData.NoteType.secretPassage.num, // 9,
						"Here's an access point under the tracks, through the park, onto a trail, or over a ravine.");
		noteTypeDescriptions
				.put(NoteData.NoteType.crashNearMiss.num, // 12,
						"Note incidents here, after reporting via 911 call. Include your cell/email, what happened, time/date, location & description of vehicle. <a href=\"http://www.ibikeknx.com/brochures\">What to do after a crash</a>"
				);
						//"Please note crashes, near-misses, and harassment here. More information on what to do after a crash is on our website: http://www.ibikeknx.com/brochures");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_type);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Set up note type buttons
		noteType = -1;
		prepareNoteTypeButtons();

		Intent myIntent = getIntent(); // gets the previously created intent
		noteid = myIntent.getLongExtra("noteid", 0);

		isRecording = myIntent.getIntExtra("isRecording", -1);

		final ListView listView = (ListView) findViewById(R.id.listViewNoteType);
		values = new String[]{"Pavement issue", "Traffic signal",
				"Enforcement", "Bike lane issue", "Note this issue", "Secret passage",
				"Crash / Near Miss"};
		// final ArrayList<String> list = new ArrayList<String>();
		// for (int i = 0; i < values.length; ++i) {
		// list.add(values[i]);
		// }
		edu.utk.cycleushare.cycleknoxville.NoteTypeAdapter adapter = new edu.utk.cycleushare.cycleknoxville.NoteTypeAdapter(this, values);
		listView.setAdapter(adapter);
		// set default
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			private View oldSelection = null;

			public void clearSelection() {
				if (oldSelection != null) {
					oldSelection.setBackgroundColor(Color.parseColor("#ffffff"));
				}
			}

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// view.setSelected(true);
				// view.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_key));
				clearSelection();
				oldSelection = view;
				view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
				// view.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_key));
				//noteType = position;
				switch(position){
					case 0: // pavement issue
						noteType = NoteData.NoteType.pavementIssue.num; // 0;
						break;
					case 1: // traffic signal
						noteType = NoteData.NoteType.trafficSignal.num; // 1;
						break;
					case 2: // enforcement
						noteType = NoteData.NoteType.enforcement.num; // 2;
						break;
					case 3: // bike lane issue
						noteType = NoteData.NoteType.bikeLaneIssue.num; // 4;
						break;
					case 4: // note this
						noteType = NoteData.NoteType.noteThisIssue.num; // 5;
						break;
					case 5: // secret passage
						noteType = NoteData.NoteType.secretPassage.num; // 9;
						break;
					case 6: // crash / near miss
						noteType = NoteData.NoteType.crashNearMiss.num; // 12;
						break;
				}
/*
				if(noteType == NoteData.NoteType.crashNearMiss.num){
					AlertDialog.Builder builder = new AlertDialog.Builder(NoteTypeActivity.this);
					builder.setMessage("Please report the incident to police by calling 911. Then, please submit this note, including your contact info, what happened, time, date, location, and description of the vehicle involved.")
							.setCancelable(false)
							.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				}
*/

				// Log.v("Jason", purpose);

				TextView tv = (TextView) findViewById(R.id.textViewNoteTypeDesc);

                tv.setText(Html.fromHtml(noteTypeDescriptions.get(noteType)));
				tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setClickable(true);
                tv.setLinkTextColor(Color.BLUE);

                saveMenuItem.setEnabled(true);
				// highlight
			}

		});

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/* Creates the menu items */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_type, menu);
		saveMenuItem = menu.getItem(1);
		saveMenuItem.setEnabled(false);
		return super.onCreateOptionsMenu(menu);
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_cancel_note_type:
			// cancel
			Toast.makeText(getBaseContext(), "Note discarded.",
					Toast.LENGTH_SHORT).show();

			// Cancel
			edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteTypeActivity.this, noteid);
			Log.v("Jason", "Note id: " + noteid);
			note.dropNote();

			NoteTypeActivity.this.finish();

			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			return true;
		case R.id.action_save_note_type:
			// move to next view
			Intent intentToNoteDetail = new Intent(NoteTypeActivity.this,
					edu.utk.cycleushare.cycleknoxville.NoteDetailActivity.class);
			intentToNoteDetail.putExtra("noteType", noteType);

			intentToNoteDetail.putExtra("noteid", noteid);

			Log.v("Jason", "Note ID in NoteType: " + noteid);

			if (isRecording == 1) {
				intentToNoteDetail.putExtra("isRecording", 1);
			} else {
				intentToNoteDetail.putExtra("isRecording", 0);
			}

			startActivity(intentToNoteDetail);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			NoteTypeActivity.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// 2.0 and above
	@Override
	public void onBackPressed() {
		// cancel
		Toast.makeText(getBaseContext(), "Note discarded.", Toast.LENGTH_SHORT)
				.show();

		// Cancel
		edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteTypeActivity.this, noteid);
		Log.v("Jason", "Note id: " + noteid);
		note.dropNote();

		NoteTypeActivity.this.finish();

		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	// Before 2.0
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// cancel
			Toast.makeText(getBaseContext(), "Note discarded.",
					Toast.LENGTH_SHORT).show();

			// Cancel
			edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteTypeActivity.this, noteid);
			Log.v("Jason", "Note id: " + noteid);
			note.dropNote();

			NoteTypeActivity.this.finish();
			
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
