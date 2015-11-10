package edu.utk.cycleushare.cycleknoxville;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import edu.utk.cycleushare.cycleknoxville.R;

public class NoteDetailActivity extends Activity {
	long noteid;
	int noteType = 0;
	int isRecording;
	EditText noteDetails;
	ImageButton imageButton;
	ImageView imageView;
	String imageURL = "";
	byte[] noteImage;
	//Bitmap photo;

	boolean photo_taken;

	private static final int CAMERA_REQUEST = 1888;

	String mCurrentPhotoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_detail);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Intent myIntent = getIntent();
		noteType = myIntent.getIntExtra("noteType", -1);
		noteid = myIntent.getLongExtra("noteid", -1);
		Log.v("Jason", "Note ID in NoteDetail: " + noteid);
		Log.v("Jason", "Note Type is: " + noteType);
		isRecording = myIntent.getIntExtra("isRecording", -1);

		noteDetails = (EditText) findViewById(R.id.editTextNoteDetail);
		imageView = (ImageView) findViewById(R.id.imageView);
		// imageView.setVisibility(4);
		imageButton = (ImageButton) findViewById(R.id.imageButton);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		Button addPhotoButton = (Button) findViewById(R.id.addPhotoButton);
		addPhotoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v("Jason", "Add Photo");
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				// Ensure that there's a camera activity to handle the intent
				if (cameraIntent.resolveActivity(getPackageManager()) != null) {
					// Create the File where the photo should go
					File photoFile = null;
					try {
						photoFile = createImageFile();
						if (photoFile != null) {
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
							startActivityForResult(cameraIntent, CAMERA_REQUEST);
							Log.v("img", "storing in " + mCurrentPhotoPath);
						} else throw new IOException();
					} catch (IOException ex) {
						Toast.makeText(getBaseContext(), "Couldn't create image, please try again.", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		photo_taken = false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST) {

			if (resultCode == RESULT_OK /*&& data != null*/) {
				try {

					Uri img = Uri.fromFile(new File(mCurrentPhotoPath));

					BitmapFactory.Options bmo = new BitmapFactory.Options();
					bmo.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(mCurrentPhotoPath, bmo);

					bmo.inJustDecodeBounds = false;
					bmo.inSampleSize = Math.min(bmo.outWidth / imageView.getWidth(), bmo.outHeight / imageView.getHeight());
					bmo.inPurgeable = true;
					imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath, bmo));


					//photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), img);

					photo_taken = true;

					Log.v("img", "got photo");

					//Log.v("Jason", "Image Photo: " + data.getData().toString());
				} catch (NullPointerException e) {
					Toast.makeText(getBaseContext(), "Couldn't open image, please try again.", Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getBaseContext(), "Image capture failed, please try again.", Toast.LENGTH_SHORT).show();
			}

		}
	}

	void submit(String noteDetailsToUpload) {
		final Intent xi = new Intent(this, edu.utk.cycleushare.cycleknoxville.NoteMapActivity.class);

		edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteDetailActivity.this, noteid);
		note.populateDetails();

		SimpleDateFormat sdfStart = new SimpleDateFormat("MMMM d, y  HH:mm", Locale.US);
		String fancyStartTime = sdfStart.format(note.startTime);
		Log.v("Jason", "Start: " + fancyStartTime);

		SimpleDateFormat sdfStart2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
		String date = sdfStart2.format(note.startTime);

		// Save the note details to the phone database. W00t!

		String deviceId = getDeviceId();

		if (!photo_taken && (noteDetailsToUpload == null || noteDetailsToUpload.length() <= 0)) {
			Toast.makeText(getBaseContext(), "Please add a description or photo before submitting.", Toast.LENGTH_SHORT).show();
			return;
		}

		if (photo_taken) {
			//noteImage = getBitmapAsByteArray(photo);

			noteImage = new byte[0];
			imageURL = deviceId + "-" + date + "-type-" + noteType;

			BitmapFactory.Options bmo = new BitmapFactory.Options();
			bmo.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(mCurrentPhotoPath, bmo);

			bmo.inJustDecodeBounds = false;

			{
				int log = 1;
				int pixels = Math.max(bmo.outWidth, bmo.outHeight);
				while (pixels > 1000) {
					log++;
					pixels /= 2;
				}
				bmo.inSampleSize = log;
			}

			bmo.inPurgeable = true;

			Bitmap compressed = BitmapFactory.decodeFile(mCurrentPhotoPath, bmo);

			if (!new File(imgDir()).exists()) {
				if (!new File(imgDir()).mkdir()) {
					Toast.makeText(getBaseContext(), "Couldn't create image directory.", Toast.LENGTH_SHORT);
					return;
				}
			}

			try {
				compressed.compress(CompressFormat.JPEG, 60,
						new BufferedOutputStream(new FileOutputStream(
								imgDir() + "/" + imageURL + ".jpg")));
			} catch (FileNotFoundException fne) {
				Toast.makeText(getBaseContext(), "Couldn't create compressed image.", Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			noteImage = null;
			imageURL = "";
		}

		note.updateNote(noteType, fancyStartTime, noteDetailsToUpload,
				imageURL, noteImage);

		note.updateNoteStatus(edu.utk.cycleushare.cycleknoxville.NoteData.STATUS_COMPLETE);

		// Now create the MainInput Activity so BACK btn works properly
		// Should not use this.

		// TODO: note uploader
		if (note.notestatus < edu.utk.cycleushare.cycleknoxville.NoteData.STATUS_SENT) {
			// And upload to the cloud database, too! W00t W00t!
			edu.utk.cycleushare.cycleknoxville.NoteUploader uploader = new edu.utk.cycleushare.cycleknoxville.NoteUploader(NoteDetailActivity.this);
			uploader.execute(note.noteid);
		}

		if (isRecording == 1) {
		} else {
			Intent i = new Intent(getApplicationContext(), edu.utk.cycleushare.cycleknoxville.TabsConfig.class);
			startActivity(i);

			// And, show the map!
			xi.putExtra("shownote", note.noteid);
			xi.putExtra("uploadNote", true);
			Log.v("Jason", "Noteid: " + String.valueOf(note.noteid));
			startActivity(xi);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}

		NoteDetailActivity.this.finish();
	}

	public String getDeviceId() {
		String androidId = System.getString(this.getContentResolver(),
				System.ANDROID_ID);
		String androidBase = "androidDeviceId-";

		if (androidId == null) { // This happens when running in the Emulator
			final String emulatorId = "android-RunningAsTestingDeleteMe";
			return emulatorId;
		}
		String deviceId = androidBase.concat(androidId);

		// Fix String Length
		int a = deviceId.length();
		if (a < 32) {
			for (int i = 0; i < 32 - a; i++) {
				deviceId = deviceId.concat("0");
			}
		} else {
			deviceId = deviceId.substring(0, 32);
		}

		return deviceId;
	}

	/* Creates the menu items */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_cancel_note_detail:
				// cancel
				Toast.makeText(getBaseContext(), "Note discarded.",
						Toast.LENGTH_SHORT).show();

				// Cancel
				edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteDetailActivity.this, noteid);
				Log.v("Jason", "Note id: " + noteid);
				note.dropNote();

				NoteDetailActivity.this.finish();

				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);

			return true;
		case R.id.action_save_note_detail:
			// save
			submit(noteDetails.getEditableText().toString());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// 2.0 and above
	@Override
	public void onBackPressed() {
		// cancel
		Toast.makeText(getBaseContext(), "Note discarded.",
				Toast.LENGTH_SHORT).show();

		// Cancel
		edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteDetailActivity.this, noteid);
		Log.v("Jason", "Note id: " + noteid);
		note.dropNote();

		NoteDetailActivity.this.finish();

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
			edu.utk.cycleushare.cycleknoxville.NoteData note = edu.utk.cycleushare.cycleknoxville.NoteData.fetchNote(NoteDetailActivity.this, noteid);
			Log.v("Jason", "Note id: " + noteid);
			note.dropNote();

			NoteDetailActivity.this.finish();

			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "note_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = /*"file:" +*/ image.getAbsolutePath();

		return image;
	}

	public static String imgDir() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ibikeknx";
	}
}
