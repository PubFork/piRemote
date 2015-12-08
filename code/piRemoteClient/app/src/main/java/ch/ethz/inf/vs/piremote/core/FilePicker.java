package ch.ethz.inf.vs.piremote.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;

/**
 * Created by andrina on 08/12/15.
 * Take care of the file picker overlay and keep track of base path.
 */
public class FilePicker extends AbstractClientActivity {

    private String basePath;

    protected int defaultActivityView;
    private boolean filePickerIsActive = false;

    // UI references
    private ListView mPathList;
    private Button mBackButton;

    @Override
    protected void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {

    }

    @Override
    protected void onReceiveInt(int i) {

    }

    @Override
    protected void onReceiveDouble(double d) {

    }

    @Override
    protected void onReceiveString(String str) {

    }

    void updateFilePicker(@Nullable List<String> paths) {
        if (paths != null) {

            if (!filePickerIsActive) {
                // TODO FILE PICKER: update view and register listeners
                // update view to file picker overlay
                setContentView(R.layout.dialog_file_picker);

                filePickerIsActive = true;
            }
            // TODO FILE PICKER: update list anyway

            // Get an array of all available files and directories.
            final String[] pathNames = new String[paths.size()];
            for (int i = 0; i < paths.size(); i++) {
                // TODO FILE PICKER: only display the name of the file / directory and not the hole path ?
                pathNames[i] = paths.get(i);
            }

            // Display the available files and directories in a ListView.
            mPathList = (ListView) findViewById(R.id.list_paths);
            mPathList.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, pathNames));

            mPathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, @NonNull View view, int position, long id) {
                    Log.d("", "Clicked button: " + view.toString());
                    clientCore.pickFile(pathNames[position]); // Let the server know which file or directory the user picked.
                }
            });

            mBackButton = (Button) findViewById(R.id.button_back);
            mBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeFilePicker();
                }
            });
        } else {
            closeFilePicker(); // close the file picker if we get a null pointer
        }
    }

    void closeFilePicker() {
        setContentView(defaultActivityView); // TODO FILE PICKER: switch to original view
        filePickerIsActive = false;
    }
}
