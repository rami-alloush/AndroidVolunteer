package test.example.volunteer;
import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class OpportunityAdapter extends FirestoreRecyclerAdapter<Opportunity, OpportunityAdapter.OpportunityHolder> {

    private Context context;
    private Boolean canApply;
    private Boolean newOpportunity;

    OpportunityAdapter(@NonNull FirestoreRecyclerOptions options, Boolean canApply, Boolean newOpportunity) {
        super(options);
        this.canApply = canApply;
        this.newOpportunity = newOpportunity;
    }

    @Override
    protected void onBindViewHolder(@NonNull OpportunityHolder holder, int position, @NonNull Opportunity model) {
        final int current = position;
        final Opportunity opportunity = model;

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        holder.opportunityTitle.setText(model.getTitle());
        holder.opportunityDescription.setText(model.getDescription());
        holder.opportunityStartDate.setText(sfd.format(model.getStartDate().toDate()));
        holder.opportunityEndDate.setText(sfd.format(model.getEndDate().toDate()));
        holder.opportunityDuration.setText(String.valueOf(model.getDuration()));

        if (canApply) {
//            holder.opportunityEndDate.setIsIndicator(false);
        }

        if (newOpportunity) {
            holder.opportunityComplete.setVisibility(View.VISIBLE);
            holder.opportunityComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        opportunity.setCompleted(true);
                        FirebaseFirestore.getInstance().collection("Opportunities")
                                .document(getSnapshots().getSnapshot(current).getId())
                                .set(opportunity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, context.getString(R.string.opportunity_status_updated), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        }
        
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Opportunity No. " + (current + 1), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @NonNull
    @Override
    public OpportunityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_opportunity_item, parent, false);
        context = parent.getContext();
        return new OpportunityHolder(view);
    }

    class OpportunityHolder extends RecyclerView.ViewHolder {
        private TextView opportunityTitle;
        private TextView opportunityDescription;
        private TextView opportunityDuration;
        private TextView opportunityStartDate;
        private TextView opportunityEndDate;
        private CheckBox opportunityComplete;

        OpportunityHolder(View view) {
            super(view);
            opportunityTitle = view.findViewById(R.id.opportunityTitle);
            opportunityDescription = view.findViewById(R.id.opportunityDescription);
            opportunityDuration = view.findViewById(R.id.opportunityDuration);
            opportunityStartDate = view.findViewById(R.id.opportunityStartDate);
            opportunityEndDate = view.findViewById(R.id.opportunityEndDate);
            opportunityComplete = view.findViewById(R.id.opportunityComplete);
        }
    }
}
