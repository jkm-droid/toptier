package jkmdroid.toptier;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentHome extends Fragment {
    TextView allMatches, upComingMatches, homeMessage;
    private int all = 36;
    private int upcoming =15;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home, null);
        FrameLayout layout = new FrameLayout(getActivity());
        allMatches = view.findViewById(R.id.allmatches);
        upComingMatches = view.findViewById(R.id.upcomingmatches);
        homeMessage = view.findViewById(R.id.home_message);

        allMatches.setText(all+"\nMatches");
        upComingMatches.setText(upcoming+"\nUpcoming Matches");
        homeMessage.setText("Benefits of choosing this platform\n-Number one accurate odds predictor\n-Get real-time updates\n-Correct odds");
        layout.addView(view);
        return layout;
    }
    public void setData(int upcoming, int all){
        this.all = all;
        this.upcoming = upcoming;
        allMatches.setText(all+"\nMatches");
        upComingMatches.setText(upcoming+"\nUpcoming Matches");
    }
}