package test.example.volunteer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;

public class ApplyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        Opportunity opportunity = Objects.requireNonNull(getIntent().getExtras()).getParcelable("opportunity");
        TextView textViewtitle = findViewById(R.id.textViewtitle);
        textViewtitle.setText(opportunity.getTitle());

    }
}
