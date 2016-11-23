package edu.utk.cycleushare.cycleknoxville;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.utk.cycleushare.cycleknoxville.R;

public class NoteTypeAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	public NoteTypeAdapter(Context context, String[] values) {
		super(context, R.layout.note_type_list_item, values);
		this.context = context;
		this.values = values;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.note_type_list_item, parent, false);
		TextView textView = (TextView) rowView
				.findViewById(R.id.TextViewNoteType);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.ImageViewNoteType);
        // textView.setText(values[position]);
		// Change the icon for Windows and iPhone

		int noteType = -1;

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
			/*default:
				noteType = NoteData.NoteType.noteThisIssue.num;
				break;*/
		}

        textView.setText(NoteData.NoteType.get(noteType).label);

		if(NoteData.NoteType.get(noteType).isIssue){
			imageView.setImageResource(R.drawable.noteissuepicker_high);
		} else {
			imageView.setImageResource(R.drawable.noteassetpicker_high);
		}
		return rowView;
	}
}