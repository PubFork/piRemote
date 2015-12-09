package ch.ethz.inf.vs.piremote.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import ch.ethz.inf.vs.piremote.R;

/**
 * Created by andrina on 08/12/15.
 * File Picker Dialog that is placed over the current activity when the user wants to select a file or directory.
 */
public class FilePickerDialogFragment extends DialogFragment {

    private final String DEBUG_TAG = "# DialogFP #";
    private final String ERROR_TAG = "# DialogFP ERROR #";

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FilePickerDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    FilePickerDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the FilePickerDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the FilePickerDialogListener so we can send events to the host
            mListener = (FilePickerDialogListener) activity;
            Log.d(DEBUG_TAG, "Attached listener.");
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.e(ERROR_TAG, "Activity does not implement FilePickerDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.title_dialog_file_picker)
                .setView(inflater.inflate(R.layout.dialog_file_picker, null))
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity, as the user cancelled the dialog.
                        mListener.onDialogNegativeClick(FilePickerDialogFragment.this);

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}