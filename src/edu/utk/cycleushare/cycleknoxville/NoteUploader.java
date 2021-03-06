/**	 Cycle Altanta, Copyright 2012 Georgia Institute of Technology
 *                                    Atlanta, GA. USA
 *
 *   @author Christopher Le Dantec <ledantec@gatech.edu>
 *   @author Anhong Guo <guoanhong15@gmail.com>
 *
 *   Updated/Modified for Atlanta's app deployment. Based on the
 *   CycleTracks codebase for SFCTA.
 *
 *   CycleTracks, Copyright 2009,2010 San Francisco County Transportation Authority
 *                                    San Francisco, CA, USA
 *
 * 	 @author Billy Charlton <billy.charlton@sfcta.org>
 *
 *   This file is part of CycleTracks.
 *
 *   CycleTracks is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CycleTracks is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CycleTracks.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.utk.cycleushare.cycleknoxville;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class NoteUploader extends AsyncTask<Long, Integer, Boolean> {
	Context mCtx;
	edu.utk.cycleushare.cycleknoxville.DbAdapter mDb;
	byte[] imageData;
	Boolean imageDataNull;
	String imgname;

	public static final int kSaveNoteProtocolVersion = 4;

	public static final String NOTE_RECORDED = "r";
	public static final String NOTE_LAT = "l";
	public static final String NOTE_LGT = "n";
	public static final String NOTE_HACC = "h";
	public static final String NOTE_VACC = "v";
	public static final String NOTE_ALT = "a";
	public static final String NOTE_SPEED = "s";
	public static final String NOTE_TYPE = "t";
	public static final String NOTE_DETAILS = "d";
	public static final String NOTE_IMGURL = "i";

	String twoHyphens = "--";
	String boundary = "cycle*******notedata*******atlanta";
	String lineEnd = "\r\n";

	public NoteUploader(Context ctx) {
		super();
		this.mCtx = ctx;
		this.mDb = new edu.utk.cycleushare.cycleknoxville.DbAdapter(this.mCtx);
	}

	private JSONObject getNoteJSON(long noteId) throws JSONException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

		mDb.openReadOnly();
		Cursor noteCursor = mDb.fetchNote(noteId);

		Map<String, Integer> fieldMap = new HashMap<String, Integer>();
		fieldMap.put(NOTE_RECORDED,
				noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_RECORDED));
		fieldMap.put(NOTE_LAT, noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_LAT));
		fieldMap.put(NOTE_LGT, noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_LGT));
		fieldMap.put(NOTE_HACC, noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_ACC));
		fieldMap.put(NOTE_VACC, noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_ACC));
		fieldMap.put(NOTE_ALT, noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_ALT));
		fieldMap.put(NOTE_SPEED,
				noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_SPEED));
		fieldMap.put(NOTE_TYPE,
				noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_TYPE));
		fieldMap.put(NOTE_DETAILS,
				noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_DETAILS));
		fieldMap.put(NOTE_IMGURL,
				noteCursor.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_IMGURL));

		JSONObject note = new JSONObject();

		note.put(NOTE_RECORDED,
				df.format(noteCursor.getDouble(fieldMap.get(NOTE_RECORDED))));
		note.put(NOTE_LAT, noteCursor.getDouble(fieldMap.get(NOTE_LAT)) / 1E6);
		note.put(NOTE_LGT, noteCursor.getDouble(fieldMap.get(NOTE_LGT)) / 1E6);
		note.put(NOTE_HACC, noteCursor.getDouble(fieldMap.get(NOTE_HACC)));
		note.put(NOTE_VACC, noteCursor.getDouble(fieldMap.get(NOTE_VACC)));
		note.put(NOTE_ALT, noteCursor.getDouble(fieldMap.get(NOTE_ALT)));
		note.put(NOTE_SPEED, noteCursor.getDouble(fieldMap.get(NOTE_SPEED)));
		note.put(NOTE_TYPE, noteCursor.getInt(fieldMap.get(NOTE_TYPE)));
		note.put(NOTE_DETAILS, noteCursor.getString(fieldMap.get(NOTE_DETAILS)));
		note.put(NOTE_IMGURL, noteCursor.getString(fieldMap.get(NOTE_IMGURL)));

		if (noteCursor.getString(fieldMap.get(NOTE_IMGURL)).equals("")) {
			imageDataNull = true;
		} else {
			imageDataNull = false;
			imgname = noteCursor.getString(fieldMap.get(NOTE_IMGURL));
			//imageData = noteCursor.getBlob(noteCursor
			//		.getColumnIndex(edu.utk.cycleushare.cycleknoxville.DbAdapter.K_NOTE_IMGDATA));
		}

		noteCursor.close();
		mDb.close();
		return note;
	}

	public String getDeviceId() {
		String androidId = System.getString(this.mCtx.getContentResolver(),
				System.ANDROID_ID);
		String androidBase = "androidDeviceId-";

		if (androidId == null) { // This happens when running in the Emulator
			return "android-RunningAsTestingDeleteMe";
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

	// private BasicHttpEntity getPostData(long noteId) throws JSONException,
	// IOException {
	// JSONObject note = getNoteJSON(noteId);
	// String deviceId = getDeviceId();
	//
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	// nameValuePairs.add(new BasicNameValuePair("note", note.toString()));
	// nameValuePairs.add(new BasicNameValuePair("device", deviceId));
	// nameValuePairs.add(new BasicNameValuePair("version", ""
	// + kSaveNoteProtocolVersion));
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// DataOutputStream dos = new DataOutputStream(baos);
	//
	// dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
	// + "Content-Disposition: form-data; name=\"note\"\r\n\r\n"
	// + note.toString() + "\r\n");
	// dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
	// + "Content-Disposition: form-data; name=\"version\"\r\n\r\n"
	// + String.valueOf(kSaveNoteProtocolVersion) + "\r\n");
	// dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
	// + "Content-Disposition: form-data; name=\"device\"\r\n\r\n"
	// + deviceId + "\r\n");
	//
	// if (imageDataNull = false) {
	// dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
	// + "Content-Disposition: form-data; name=\"file\"; filename=\""
	// + deviceId + ".jpg\"\r\n"
	// + "Content-Type: image/jpeg\r\n\r\n");
	// dos.write(imageData);
	// dos.writeBytes("\r\n");
	// }
	//
	// dos.writeBytes("--cycle*******notedata*******atlanta--\r\n");
	//
	// dos.flush();
	// dos.close();
	//
	// Log.v("Jason", "" + baos);
	//
	// ByteArrayInputStream content = new ByteArrayInputStream(
	// baos.toByteArray());
	// BasicHttpEntity entity = new BasicHttpEntity();
	// entity.setContent(content);
	//
	// entity.setContentLength(content.toString().length());
	//
	// return entity;
	// }
	//
	// private static String convertStreamToString(InputStream is) {
	// /*
	// * To convert the InputStream to String we use the
	// * BufferedReader.readLine() method. We iterate until the BufferedReader
	// * return null which means there's no more data to read. Each line will
	// * appended to a StringBuilder and returned as String.
	// */
	// BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	// StringBuilder sb = new StringBuilder();
	//
	// String line = null;
	// try {
	// while ((line = reader.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return sb.toString();
	// }

	boolean uploadOneNote(long currentNoteId) {
		boolean result = false;
		SharedPreferences settings = this.mCtx.getSharedPreferences("PREFS", 0);

		String postUrl;

		// get URL from settings; if it's not found (default), then use the preset
		//if((postUrl = settings.getString(""+FragmentUserInfo.PREF_UPLOAD_URL,"")).equals("")){
			postUrl = mCtx.getString(R.string.postURL);
		//}

		// byte[] postBodyDataZipped;
		//
		// BasicHttpEntity postBodyEntity;
		//
		// List<NameValuePair> nameValuePairs;
		// try {
		// postBodyEntity = getPostData(currentNoteId);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// return result;
		// } catch (IOException e) {
		// e.printStackTrace();
		// return result;
		// }
		//
		// HttpClient client = new DefaultHttpClient();
		// // TODO: Server URL
		// final String postUrl = "http://cycleatlanta.org/post_dev/";
		// HttpPost postRequest = new HttpPost(postUrl);

		try {

			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			conn.setRequestProperty("Cycleatl-Protocol-Version", "4");

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			JSONObject note = getNoteJSON(currentNoteId);
			String deviceId = getDeviceId();

			dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
					+ "Content-Disposition: form-data; name=\"note\"\r\n\r\n"
					+ note.toString() + "\r\n");
			dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
					+ "Content-Disposition: form-data; name=\"version\"\r\n\r\n"
					+ String.valueOf(kSaveNoteProtocolVersion) + "\r\n");
			dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
					+ "Content-Disposition: form-data; name=\"device\"\r\n\r\n"
					+ deviceId + "\r\n");

			if (imageDataNull == false) {
				dos.writeBytes("--cycle*******notedata*******atlanta\r\n"
						+ "Content-Disposition: form-data; name=\"file\"; filename=\""
						+ deviceId + ".jpg\"\r\n"
						+ "Content-Type: image/jpeg\r\n\r\n");
				//dos.write(imageData);

				Log.v("imgup", "trying to upload file: " + NoteDetailActivity.imgDir() + "/" + imgname + ".jpg");

				int bufsz = 8192;
				int len;
				byte[] buf = new byte[bufsz];
				InputStream is = new FileInputStream(NoteDetailActivity.imgDir() + "/" + imgname + ".jpg");
				while ((len = is.read(buf, 0, bufsz)) != -1) {
					dos.write(buf, 0, len);
				}

				dos.writeBytes("\r\n");
			}

			dos.writeBytes("--cycle*******notedata*******atlanta--\r\n");

			dos.flush();
			dos.close();

			int serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();
			// JSONObject responseData = new JSONObject(serverResponseMessage);
			Log.v("Jason", "HTTP Response is : " + serverResponseMessage + ": "
					+ serverResponseCode);
			if (serverResponseCode == 201 || serverResponseCode == 202) {
				mDb.open();
				mDb.updateNoteStatus(currentNoteId, edu.utk.cycleushare.cycleknoxville.NoteData.STATUS_SENT);
				mDb.close();
				result = true;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}

	@Override
	protected Boolean doInBackground(Long... noteid) {
		// First, send the note user asked for:
		Boolean result = true;
		if (noteid.length != 0) {
			result = uploadOneNote(noteid[0]);
		}

		// Then, automatically try and send previously-completed notes
		// that were not sent successfully.
		Vector<Long> unsentNotes = new Vector<Long>();

		mDb.openReadOnly();
		Cursor cur = mDb.fetchUnsentNotes();
		if (cur != null && cur.getCount() > 0) {
			// pd.setMessage("Sent. You have previously unsent notes; submitting those now.");
			while (!cur.isAfterLast()) {
				unsentNotes.add(Long.valueOf(cur.getLong(0)));
				cur.moveToNext();
			}
			cur.close();
		}
		mDb.close();

		for (Long note : unsentNotes) {
			result &= uploadOneNote(note);
		}
		return result;
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(mCtx.getApplicationContext(),
				"Submitting. Thanks for using I BIKE KNX!",
				Toast.LENGTH_LONG).show();
	}

	private edu.utk.cycleushare.cycleknoxville.SavedNotesAdapter mSavedNotesAdapter;

	public edu.utk.cycleushare.cycleknoxville.SavedNotesAdapter setSavedNotesAdapter(
			edu.utk.cycleushare.cycleknoxville.SavedNotesAdapter mSavedNotesAdapter) {
		this.mSavedNotesAdapter = mSavedNotesAdapter;
		return mSavedNotesAdapter;
	}

	private edu.utk.cycleushare.cycleknoxville.FragmentSavedNotesSection fragmentSavedNotesSection;

	public edu.utk.cycleushare.cycleknoxville.FragmentSavedNotesSection setFragmentSavedNotesSection(
			edu.utk.cycleushare.cycleknoxville.FragmentSavedNotesSection fragmentSavedNotesSection) {
		this.fragmentSavedNotesSection = fragmentSavedNotesSection;
		return fragmentSavedNotesSection;
	}

	private ListView listSavedNotes;

	public ListView setListView(ListView listSavedNotes) {
		this.listSavedNotes = listSavedNotes;
		return listSavedNotes;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		try {
			if (mSavedNotesAdapter != null) {
				mSavedNotesAdapter.notifyDataSetChanged();
			}

			if (fragmentSavedNotesSection != null) {
				listSavedNotes.invalidate();
				fragmentSavedNotesSection.populateNoteList(listSavedNotes);
			}

			if (result) {
				Toast.makeText(mCtx.getApplicationContext(),
						"Note uploaded successfully.", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(
						mCtx.getApplicationContext(),
						"I BIKE KNX couldn't upload the note, and will retry when your next note is completed.",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// Just don't toast if the view has gone out of context
		}
	}
}
