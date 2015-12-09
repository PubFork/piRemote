package ch.ethz.inf.vs.piremote.core;

import android.widget.Button;
import android.widget.ListView;

/**
 * Created by andrina on 08/12/15.
 * Take care of the file picker dialog and keep track of base path.
 */
public class FilePicker {

    private String basePath;

    protected int defaultActivityView;
    private boolean filePickerIsActive = false;

    // UI references
    private ListView mPathList;
    private Button mBackButton;

    private final String DEBUG_TAG = "# FilePicker #";

/*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.dialog_file_picker);

        List<String> paths = getIntent().getStringArrayListExtra(AppConstants.EXTRA_PATH_LIST);
        // Get an array of all the available applications (given by the ServerState enumeration) and store their names.
        final String[] pathNames = new String[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            // TODO FILE PICKER: only display the name of the file / directory and not the hole path ?
            pathNames[i] = paths.get(i);
        }

        // Display the available applications in a ListView.
        ListView mPathList = (ListView) findViewById(R.id.list_paths);
        mPathList.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, pathNames));

        mPathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, @NonNull View view, int position, long id) {
                Log.d(DEBUG_TAG, "Clicked button: " + view.toString());
                Intent closeFilePicker = new Intent(getBaseContext(), getCallingActivity().getClass());
                closeFilePicker.putExtra(AppConstants.EXTRA_PICKED_PATH, pathNames[position]);
                startActivity(closeFilePicker);
                // clientCore.pickFile(pathNames[position]);
            }
        });

        Button mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                // Switch back to old activity
                Intent closeFilePicker = new Intent(getBaseContext(), getCallingActivity().getClass());
                startActivity(closeFilePicker);
                // finish(); ? -> old activities onActivityResult()
            }
        });
    }
*/

/*
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
*/

}
