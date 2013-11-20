package com.example.careapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class TodoDialog extends DialogFragment {
	
	boolean timeSelected = false;
	
	boolean timeSelected() { return timeSelected; }

//============Setup the passing of events back to the dialog host======================
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface TodoDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    // Use this instance of the interface to deliver action events
    TodoDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (TodoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    
  //============Setup the passing of events back to the dialog host======================
    
    
    
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		


		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		//Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because  its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.todo_dialog, null))

		.setMessage("Date only")
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogPositiveClick(TodoDialog.this);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//cancel
			}
		})
		.setNeutralButton("Time", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}



	@Override
	public void onStart() {
		super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
		final AlertDialog d = (AlertDialog)getDialog();
		if(d != null) {
			Button neutralButton = (Button) d.getButton(Dialog.BUTTON_NEUTRAL);
			neutralButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (d.findViewById(R.id.todoTimeSelect) == null) {
						LinearLayout layout = (LinearLayout) d.findViewById(R.id.todoDialog);
						layout.addView(TodoDialog.this.getActivity().getLayoutInflater().inflate(R.layout.todo_time_select, null));
						d.setMessage("Date and Time");
						timeSelected = true;
					} else {
						LinearLayout layout = (LinearLayout) d.findViewById(R.id.todoDialog);
						layout.removeView(d.findViewById(R.id.todoTimeSelect));	
						d.setMessage("Date only");
						timeSelected = false;
					}
					

				}
			});
		}
	}
}
