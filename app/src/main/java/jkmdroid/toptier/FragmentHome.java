package jkmdroid.toptier;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class FragmentHome extends Fragment {
    TextView allMatches, upComingMatches, homeMessage, correctTips, allMembers, vipMessage;
    private int all = 360;
    private int upcoming =150;
    private int correct =340;
    private int members =34000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home, null);
        FrameLayout layout = new FrameLayout(getActivity());
        allMatches = view.findViewById(R.id.allmatches);
        upComingMatches = view.findViewById(R.id.upcomingmatches);
        homeMessage = view.findViewById(R.id.home_message);
        correctTips = view.findViewById(R.id.correct_tips);
        allMembers = view.findViewById(R.id.members);
        vipMessage = view.findViewById(R.id.vip_poster);

        allMatches.setText(all+"+\nMatches");
        upComingMatches.setText(upcoming+"\nUpcoming Matches");
        correctTips.setText(correct+"+\nCorrect Tips");
        allMembers.setText(members+"+\nHappy Members");
        homeMessage.setText("Benefits of choosing this platform\n-Number one accurate odds predictor\n-Get real-time updates\n-Correct odds");
        vipMessage.setText("Join VIP for $15 to enjoy MORE, SURE, and CORRECT tips");
        set_animation(vipMessage);

        layout.addView(view);
        return layout;
    }
    public void set_animation(TextView textView) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1800); //manage the blinking time
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //change color at start of animation
                textView.setTextColor(getActivity().getResources().getColor(R.color.colorVIP1));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //change color at end of animation
                textView.setTextColor(getActivity().getResources().getColor(R.color.green));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                textView.setTextColor(getActivity().getResources().getColor(R.color.colorVIP2));
            }
        });
        textView.startAnimation(anim);
    }

    public void setData(int upcoming, int all, int correct, int members){
        this.all = all;
        this.upcoming = upcoming;
        this.correct = correct;
        this.members = members;

        allMatches.setText(all+"+\nMatches");
        upComingMatches.setText(upcoming+"\nUpcoming Matches");
        correctTips.setText(correct+"+\nCorrect Tips");
        allMembers.setText(members+"+\nHappy Members");
    }
}