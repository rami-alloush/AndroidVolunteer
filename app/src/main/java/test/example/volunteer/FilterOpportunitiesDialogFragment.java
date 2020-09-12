package test.example.volunteer;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class FilterOpportunitiesDialogFragment extends DialogFragment {

    private Spinner locationSpinner;
    private EditText durationMin;
    private int durationMinValue;
    private EditText durationMax;
    private int durationMaxValue;

    // Defines the listener interface
    public interface FilterOpportunitiesDialogListener {
        void onFinishFilterOpportunitiesDialogListener(String location,
                                                       Integer durationMinValue,
                                                       Integer durationMaxValue);
    }

    public FilterOpportunitiesDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterOpportunitiesDialogFragment newInstance() {
        FilterOpportunitiesDialogFragment frag = new FilterOpportunitiesDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_opportunities, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        Button filterBtn = view.findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false);
            }
        });
        Button clearFilterBtn = view.findViewById(R.id.clearFilterBtn);
        clearFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(true);
            }
        });



        locationSpinner = view.findViewById(R.id.locationSpinner);
        durationMin = view.findViewById(R.id.durationMin);
        durationMax = view.findViewById(R.id.durationMax);

        // Fetch arguments from bundle and set title
        int position = SharedPrefsUtils.getIntegerPreference(getContext(),
                "locationPosition", 0);
        locationSpinner.setSelection(position);

        durationMinValue = SharedPrefsUtils.getIntegerPreference(getContext(),
                "durationMinValue", 1);
        durationMin.setText(String.valueOf(durationMinValue));

        durationMaxValue = SharedPrefsUtils.getIntegerPreference(getContext(),
                "durationMaxValue", 365);
        durationMax.setText(String.valueOf(durationMaxValue));

        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(boolean clear) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        FilterOpportunitiesDialogListener listener = (FilterOpportunitiesDialogListener) getTargetFragment();
        // Convert values
        int locationPosition = (int) locationSpinner.getSelectedItemId();
        String locationSpinnerStr = locationSpinner.getSelectedItem().toString();
        Integer durationMinVal = Integer.valueOf(durationMin.getText().toString());
        Integer durationMaxVal = Integer.valueOf(durationMax.getText().toString());
        if (clear) {
            locationSpinnerStr = "ClearFilter";
        }

        // Save values to SharedPerfs
        SharedPrefsUtils.setIntegerPreference(getContext(), "locationPosition", locationPosition);
        SharedPrefsUtils.setIntegerPreference(getContext(), "durationMinValue", durationMinVal);
        SharedPrefsUtils.setIntegerPreference(getContext(), "durationMaxValue", durationMaxVal);
        assert listener != null;
        // Send data to Listener
        listener.onFinishFilterOpportunitiesDialogListener(
                locationSpinnerStr, durationMinVal, durationMaxVal);
        // Close filter dialog
        dismiss();
    }

    public void onResume() {
        super.onResume();

        // Fix dialog size
        Window window = getDialog().getWindow();
        Point size = new Point();
        if (window != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            int width = size.x;

            window.setLayout((int) (width * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

    }
}