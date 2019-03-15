package test.example.volunteer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unchecked")
public class OpportunityAdapter extends FirestoreRecyclerAdapter<Opportunity, OpportunityAdapter.OpportunityHolder> {

    private View mView;
    private Context context;
    private String User_Page;

    // data is passed into the constructor
    OpportunityAdapter(@NonNull FirestoreRecyclerOptions options, String User_Page) {
        super(options);
        this.User_Page = User_Page;
    }

    // If needed to change ViewType
    @Override
    public int getItemViewType(int position) {
        // Change ViewType to be able to hide the Opportunities that volunteer already applied for
        if (User_Page.equals("Volunteer_Opportunities")) {
            // We are in the Open Opportunities page for the Volunteer
            Opportunity opportunity = getSnapshots().getSnapshot(position).toObject(Opportunity.class);
            HashMap<String, Integer> applicantsUIDs = opportunity.getApplicantsUIDs();
            if (applicantsUIDs != null) {
                for (Map.Entry<String, Integer> volunteerMap : applicantsUIDs.entrySet()) {
                    if (volunteerMap.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                        return 1;
                    }
                }
            }
        }
        return super.getItemViewType(position);
    }

    // Inflates the row layout from xml when needed (and based on the ViewType)
    @NonNull
    @Override
    public OpportunityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_item, parent, false);
        } else {
            mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_opportunity_item, parent, false);
        }
        context = parent.getContext();
        return new OpportunityHolder(mView, viewType);
    }

    // Binds the data to the Views in each row
    @Override
    protected void onBindViewHolder(@NonNull OpportunityHolder holder, int position, @NonNull Opportunity model) {
        if (holder.getItemViewType() == 1) {
            return;
        }

        final int current = position;
        final Opportunity opportunity = model;

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        holder.opportunityTitle.setText(model.getTitle());
        holder.opportunityDescription.setText(model.getDescription());
        holder.opportunityLocation.setText(String.valueOf(model.getLocation()));
        holder.opportunityDuration.setText(String.valueOf(model.getDuration()));
        holder.opportunityStartDate.setText(sfd.format(model.getStartDate().toDate()));
        holder.opportunityEndDate.setText(sfd.format(model.getEndDate().toDate()));

        // Set the On Click Listener Actions
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User_Page.equals("Volunteer_Opportunities")) {
                    // For Volunteer, to be able to apply or see application status
                    opportunity.setUID(getSnapshots().getSnapshot(current).getId());
                    Intent intent = new Intent(context, ApplyActivity.class);
                    intent.putExtra("opportunity", opportunity);
                    intent.putExtra("viewStatus", false);
                    context.startActivity(intent);
                } else if (User_Page.equals("Volunteer_Applications")) {
                    // For Volunteer, to be able to apply or see application status
                    opportunity.setUID(getSnapshots().getSnapshot(current).getId());
                    Intent intent = new Intent(context, ApplyActivity.class);
                    intent.putExtra("opportunity", opportunity);
                    intent.putExtra("viewStatus", true);
                    context.startActivity(intent);
                } else if (User_Page.equals("Hospital_Applications")) {
                    // For Hospital to be able to view applicants and their applications
                    opportunity.setUID(getSnapshots().getSnapshot(current).getId());
                    Intent intent = new Intent(context, ViewApplicantsActivity.class);
                    intent.putExtra("opportunity", opportunity);
                    context.startActivity(intent);
                }

            }
        });

        // Set ability to mark Open Opportunity completed
        if (User_Page.equals("Hospital_Opportunities_Open")) {
            holder.opportunityComplete.setVisibility(View.VISIBLE);
            holder.opportunityComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        opportunity.setCompleted(true);
                        FirebaseFirestore.getInstance().collection("opportunities")
                                .document(getSnapshots().getSnapshot(current).getId())
                                .set(opportunity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, context.getString(R.string.opportunity_status_updated),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        }
    }

    // Stores and recycles views as they are scrolled off screen
    class OpportunityHolder extends RecyclerView.ViewHolder {
        private TextView opportunityTitle;
        private TextView opportunityDescription;
        private TextView opportunityLocation;
        private TextView opportunityDuration;
        private TextView opportunityStartDate;
        private TextView opportunityEndDate;
        private CheckBox opportunityComplete;

        OpportunityHolder(View view, int viewType) {
            super(view);
            opportunityTitle = view.findViewById(R.id.opportunityTitle);
            opportunityDescription = view.findViewById(R.id.opportunityDescription);
            opportunityLocation = view.findViewById(R.id.opportunityLocation);
            opportunityDuration = view.findViewById(R.id.opportunityDuration);
            opportunityStartDate = view.findViewById(R.id.opportunityStartDate);
            opportunityEndDate = view.findViewById(R.id.opportunityEndDate);
            opportunityComplete = view.findViewById(R.id.opportunityComplete);
        }
    }
}
