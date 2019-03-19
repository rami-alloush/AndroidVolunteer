package test.example.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class OpportunityFragment extends Fragment implements FilterOpportunitiesDialogFragment.FilterOpportunitiesDialogListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_USER_TYPE = "user-type";
    private static final String TAG = "OpportunityFragmentTAG";
    private int mColumnCount = 1;
    private String User_Page = "default";
    private RecyclerView recyclerView;
    private OpportunityAdapter adapter;
    private TextView servicesHeader;
    private Spinner spinnerAppStatus;
    private Button filterBtn;

    private Integer appStatus = null;
    private String locationFilter = "";
    private Integer durationMinValue = 0;
    private Integer durationMaxValue = 1000;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OpportunityFragment() {
    }

    @SuppressWarnings("unused")
    public static OpportunityFragment newInstance(String userType, int columnCount) {
        OpportunityFragment fragment = new OpportunityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            User_Page = getArguments().getString(ARG_USER_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opportunity, container, false);
        recyclerView = view.findViewById(R.id.servicesRecyclerView);
        servicesHeader = view.findViewById(R.id.opportunityListHeader);
        filterBtn = view.findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        spinnerAppStatus = view.findViewById(R.id.spinnerAppStatus);
        spinnerAppStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appStatus = position;
                updateUIParams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Set the adapter
        Context context = view.getContext();

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // Configure recycler adapter options
        updateUIParams();

        return view;
    }

    private void updateUIParams() {
        // Using FirebaseUI
        //  * query is the Query object defined above.
        //  * Opportunity.class instructs the adapter to convert each DocumentSnapshot to a Chat object
        String currentUID = FirebaseAuth.getInstance().getUid();
        Query query;
        Log.d(TAG, User_Page);
        switch (User_Page) {
            case "Volunteer_Opportunities":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", false)
                        .orderBy("startDate")
                        .limit(50);
                servicesHeader.setText(R.string.open_opportunities);
                // Show button to open Filter Opportunities Dialog
                filterBtn.setVisibility(View.VISIBLE);
                break;

            case "Volunteer_Opportunities_Filtered":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", false)
                        .whereEqualTo("location", locationFilter)
                        .orderBy("duration")
                        .whereGreaterThanOrEqualTo("duration", durationMinValue)
                        .whereLessThanOrEqualTo("duration", durationMaxValue)
                        .limit(50);
                servicesHeader.setText(R.string.open_opportunities);
                // Show button to open Filter Opportunities Dialog
                filterBtn.setVisibility(View.VISIBLE);
                break;

            case "Volunteer_Applications":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", false)
                        .whereEqualTo("applicantsUIDs." + currentUID, appStatus)
                        .limit(50);
                servicesHeader.setText(R.string.your_applications);
                // Show menu to filter applications by status
                spinnerAppStatus.setVisibility(View.VISIBLE);
                break;

            case "Hospital_Opportunities_Open":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", false)
                        .whereEqualTo("hospitalUID", currentUID)
                        .limit(50);
                servicesHeader.setText(R.string.your_open_opportunities);
                break;

            case "Hospital_Opportunities_Completed":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", true)
                        .whereEqualTo("hospitalUID", currentUID)
                        .limit(50);
                servicesHeader.setText(R.string.your_completed_opportunities);
                break;

            case "Hospital_Applications":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", false)
                        .whereEqualTo("hasApplications", true)
                        .whereEqualTo("hospitalUID", currentUID)
                        .limit(50);
                servicesHeader.setText(R.string.your_applications);
                break;

            default:
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .limit(50);
                servicesHeader.setText(R.string.all_opportunities);
                break;
        }

        // For normal fragments to work
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Opportunity>()
                .setQuery(query, Opportunity.class)
                .build();

        adapter = new OpportunityAdapter(options, User_Page);
        recyclerView.setAdapter(adapter);

        // Important to restart the adapter when query change
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showFilterDialog() {
        Log.d("RamiReading", "From showFilterDialog " + String.valueOf(durationMinValue));
        FilterOpportunitiesDialogFragment editNameDialogFragment =
                FilterOpportunitiesDialogFragment.newInstance();
        // SETS the target fragment for use later when sending results
        editNameDialogFragment.setTargetFragment(OpportunityFragment.this, 300);
        editNameDialogFragment.show(getFragmentManager(), "fragment_edit_name");
    }

    @Override
    public void onFinishFilterOpportunitiesDialogListener(String location,
                                                          Integer durationMinVal,
                                                          Integer durationMaxVal) {
        locationFilter = location;
        durationMinValue = durationMinVal;
        durationMaxValue = durationMaxVal;
        User_Page = "Volunteer_Opportunities_Filtered";
        updateUIParams();
    }
}