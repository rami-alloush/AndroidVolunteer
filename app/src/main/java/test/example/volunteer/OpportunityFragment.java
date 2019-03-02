package test.example.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class OpportunityFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_USER_TYPE = "user-type";
    private int mColumnCount = 1;
    private String mUserType = "default";
    private OpportunityAdapter adapter;
    private Query query;
    private Boolean canRate = false;
    private Boolean newOpportunity = false;

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
            mUserType = getArguments().getString(ARG_USER_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opportunity, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.servicesRecyclerView);
        TextView servicesHeader = view.findViewById(R.id.opportunityListHeader);

        // Set the adapter
        Context context = view.getContext();

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // Using FirebaseUI
        String currentUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        switch (mUserType) {
            case "Volunteer":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
//                        .whereEqualTo("rate", null)
//                        .whereEqualTo("completed", true)
//                        .whereEqualTo("patientUID", currentUID)
                        .limit(50);
                servicesHeader.setText(R.string.services_you_received_not_evaluated);
                canRate = true;
                break;
            case "doctorCompleted":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .whereEqualTo("completed", true)
                        .whereEqualTo("doctorUID", currentUID)
                        .limit(50);
                servicesHeader.setText(R.string.completed_services);
                break;
            case "Hospital":
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
//                        .whereEqualTo("completed", false)
//                        .whereEqualTo("doctorUID", currentUID)
                        .limit(50);
                servicesHeader.setText(R.string.new_services);
                newOpportunity = true;
                break;
            default:
                query = FirebaseFirestore.getInstance()
                        .collection("opportunities")
                        .limit(50);
                servicesHeader.setText(R.string.your_opportunities);
                break;
        }

        // Configure recycler adapter options:
        //  * query is the Query object defined above.
        //  * Opportunity.class instructs the adapter to convert each DocumentSnapshot to a Chat object
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Opportunity>()
                .setQuery(query, Opportunity.class)
                .build();

        adapter = new OpportunityAdapter(options, canRate, newOpportunity);
        recyclerView.setAdapter(adapter);

        return view;
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
}