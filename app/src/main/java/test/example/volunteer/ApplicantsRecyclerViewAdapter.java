package test.example.volunteer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ApplicantsRecyclerViewAdapter extends RecyclerView.Adapter<ApplicantsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Volunteer> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    ApplicantsRecyclerViewAdapter(Context context, ArrayList<Volunteer> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
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
        final Volunteer volunteer = mData.get(position);
        if (volunteer != null) {
            holder.tvApplicantFName.setText(volunteer.getFname());
            holder.tvApplicantLName.setText(volunteer.getLname());
            holder.tvApplicantEmail.setText(volunteer.getEmail());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VolunteerEditorActivity.class);
                    intent.putExtra("volunteer", volunteer);
                    intent.putExtra("viewMode", true);
                    context.startActivity(intent);
                }
            });
        }
    }

    // Total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvApplicantFName;
        TextView tvApplicantLName;
        TextView tvApplicantEmail;

        ViewHolder(View itemView) {
            super(itemView);
            tvApplicantFName = itemView.findViewById(R.id.tvApplicantFName);
            tvApplicantLName = itemView.findViewById(R.id.tvApplicantLName);
            tvApplicantEmail = itemView.findViewById(R.id.tvApplicantEmail);
        }
    }
}

