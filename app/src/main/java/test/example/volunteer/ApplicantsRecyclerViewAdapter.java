package test.example.volunteer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ApplicantsRecyclerViewAdapter extends RecyclerView.Adapter<ApplicantsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private Opportunity opportunity;
    private ArrayList<Volunteer> volunteers;
    private ArrayList<Integer> volunteersAppsStatus;
    private LayoutInflater mInflater;
    public static final int APP_ACCEPTED = 1;
    public static final int APP_REJECTED = 2;

    // data is passed into the constructor
    ApplicantsRecyclerViewAdapter(Context context,
                                  ArrayList<Volunteer> volunteers,
                                  ArrayList<Integer> volunteersAppsStatus,
                                  Opportunity opportunity) {
        this.mInflater = LayoutInflater.from(context);
        this.volunteers = volunteers;
        this.volunteersAppsStatus = volunteersAppsStatus;
        this.opportunity = opportunity;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.applicant_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    // Binds the data to the Views in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Volunteer volunteer = volunteers.get(position);
        final Integer volunteersAppStatus = volunteersAppsStatus.get(position);

        if (volunteer != null) {
            holder.tvApplicantFName.setText(volunteer.getFname());
            holder.tvApplicantLName.setText(volunteer.getLname());
            holder.tvApplicantEmail.setText(volunteer.getEmail());

            if (volunteersAppStatus != null) {
                switch (volunteersAppStatus) {
                    case APP_ACCEPTED:
                        holder.statusImageView.setImageResource(R.drawable.ic_check_green_24dp);
                        break;
                    case APP_REJECTED:
                        holder.statusImageView.setImageResource(R.drawable.ic_block_red_24dp);
                        break;
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ViewApplicationActivity.class);
                        intent.putExtra("volunteer", volunteer);
                        intent.putExtra("opportunityUID", opportunity.getUID());
                        context.startActivity(intent);
                    }
                });
            }

        }
    }

    // Total number of rows
    @Override
    public int getItemCount() {
        return volunteers.size();
    }

    // Stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvApplicantFName;
        TextView tvApplicantLName;
        TextView tvApplicantEmail;
        ImageView statusImageView;

        ViewHolder(View itemView) {
            super(itemView);
            tvApplicantFName = itemView.findViewById(R.id.tvApplicantFName);
            tvApplicantLName = itemView.findViewById(R.id.tvApplicantLName);
            tvApplicantEmail = itemView.findViewById(R.id.tvApplicantEmail);
            statusImageView = itemView.findViewById(R.id.statusImageView);
        }
    }
}

