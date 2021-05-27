package jkmdroid.toptier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jkmdroid on 5/19/21.
 */
public class FragmentFreeTips extends Fragment {
    private ListView listView;
    private OnFragmentRestart onFragmentRestart;
    private ArrayList<Tip> tips;
    private int widthOfDevice;
    TextView errorView, vipTextView, title;
    ImageView imageError, imageVip1, imageVip2;
    int usernameLen;
    String email;
    Boolean isRegistered;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.free_tips_layout, null);
        //get shared preferences for registered
        SharedPreferences registerPreferences = getActivity().getSharedPreferences(Preferences.Register.NAME, MODE_PRIVATE);
        usernameLen = registerPreferences.getString(Preferences.Register.USERNAME,"").length();
        email = registerPreferences.getString(Preferences.Register.EMAIL, "");
        isRegistered = registerPreferences.getBoolean(Preferences.Register.REGISTERED, false);

        listView = view.findViewById(R.id.listview);
        title =  view.findViewById(R.id.title);
        //create VIP button and link to login activity
        Button joinVip = new Button(getActivity());
        joinVip.setText("Join VIP for $15");
        joinVip.setBackgroundColor(Color.parseColor("#0E73C2"));
        listView.addFooterView(joinVip);
        joinVip.setOnClickListener(v -> startActivity(new Intent(getActivity(), UserActivity.class)));

        listView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            widthOfDevice = listView.getWidth();
        });

        errorView = view.findViewById(R.id.error);
        vipTextView = view.findViewById(R.id.vip_textview);

        imageError = view.findViewById(R.id.image_error);
        imageVip1 = view.findViewById(R.id.vip_image1);
        imageVip2 = view.findViewById(R.id.vip_image2);

        if (MyHelper.isOnline(getActivity())) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Loading tips....");
        }else {
            imageError.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("There is no internet connection!!");
            errorView.setTextColor(this.getResources().getColor(R.color.errorColor));
        }

        FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(view);

        if (onFragmentRestart != null)
            onFragmentRestart.onTipsReceived();
        return layout;
    }
    public void setOnFragmentRestart(OnFragmentRestart onFragmentRestart){
        this.onFragmentRestart = onFragmentRestart;
    }
    public void setTips(ArrayList<Tip> tips){
        this.tips = tips;
        if (tips == null)
            errorView.setVisibility(View.VISIBLE);
        errorView.setText("No tips found");
        if (getContext() == null)
            return;
        listView.setAdapter(new Adapter(getContext(), tips));
        if (tips.size() > 0){
            errorView.setVisibility(View.GONE);
            if( usernameLen> 3) {
                if (isRegistered) {
                    imageVip1.setVisibility(View.VISIBLE);
                    imageVip2.setVisibility(View.VISIBLE);
                    vipTextView.setVisibility(View.VISIBLE);
                    vipTextView.setText(email+"\nLogin for MORE and CORRECT tips");
                    set_animation(vipTextView);
                }
            }else{
                imageVip1.setVisibility(View.VISIBLE);
                imageVip2.setVisibility(View.VISIBLE);
                vipTextView.setVisibility(View.VISIBLE);
                vipTextView.setText("Join VIP for MORE, SURE and CORRECT tips");
                set_animation(vipTextView);
            }

        }
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



//    public void set_animation(TextView textView) {
//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(1800); //manage the blinking time
//        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                //change color at start of animation
//                textView.setTextColor(getActivity().getResources().getColor(R.color.colorVIP1));
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                //change color at end of animation
//                textView.setTextColor(getActivity().getResources().getColor(R.color.green));
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                textView.setTextColor(getActivity().getResources().getColor(R.color.colorVIP2));
//            }
//        });
//        textView.startAnimation(anim);
//    }

    interface  OnFragmentRestart{
        void onTipsReceived();
    }
    public ArrayList<Tip> getTips() {
        return tips;
    }

    class Adapter extends ArrayAdapter {
        public Adapter(@NonNull Context context, @NonNull List objects){
            super(context, R.layout.all_match, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View v;
            if (convertView == null)
                v = LayoutInflater.from(getContext()).inflate(R.layout.all_match, null);
            else v = convertView;

            ((TextView)v.findViewById(R.id.time)).setText(MyHelper.toPostDate(tips.get(position).getMatchTime()));
            ((TextView)v.findViewById(R.id.team1)).setText(tips.get(position).getTeamA());
            ((TextView)v.findViewById(R.id.team2)).setText(tips.get(position).getTeamB());
            ((TextView)v.findViewById(R.id.drawodds)).setText(""+tips.get(position).getDraw());
            ((TextView)v.findViewById(R.id.homeodds)).setText(""+tips.get(position).getHome());
            ((TextView)v.findViewById(R.id.awayodds)).setText(""+tips.get(position).getAway());

            ImageView imgWinlose = v.findViewById(R.id.winlose);

            if (tips.get(position).getVipStatus() == 10){
                ((TextView) v.findViewById(R.id.vip_status)).setText("VIP");
            }

            String winlose = tips.get(position).getWinLose();
            String s, correct = tips.get(position).getCorrect();
            s = "Picked: " + correct + " -> ";


            if (winlose.equalsIgnoreCase("won")) {

                imgWinlose.setImageResource(R.drawable.won_status);

                if (correct.equalsIgnoreCase("home")){
                    ((TextView)v.findViewById(R.id.team1)).setTextColor(Color.parseColor("#12D41A"));
                    ((TextView)v.findViewById(R.id.homeodds)).setTextColor(Color.parseColor("#12D41A"));
                    ((TextView)v.findViewById(R.id.home)).setTextColor(Color.parseColor("#12D41A"));
                    s += tips.get(position).getHome();

                }else if(correct.equalsIgnoreCase("draw")){
                    ((TextView)v.findViewById(R.id.vs)).setTextColor(Color.parseColor("#12D41A"));
                    ((TextView)v.findViewById(R.id.drawodds)).setTextColor(Color.parseColor("#12D41A"));
                    ((TextView)v.findViewById(R.id.draw)).setTextColor(Color.parseColor("#12D41A"));
                    s += tips.get(position).getDraw();

                }else if (correct.equalsIgnoreCase("away")){
                    ((TextView)v.findViewById(R.id.team2)).setTextColor(Color.parseColor("#12D41A"));
                    ((TextView)v.findViewById(R.id.awayodds)).setTextColor(Color.parseColor("#12D41A"));
                    ((TextView)v.findViewById(R.id.away)).setTextColor(Color.parseColor("#12D41A"));
                    s += tips.get(position).getAway();

                }else{
                    s = "Picked: "+tips.get(position).getCorrect()+" -> "+tips.get(position).getOther();
                }

                if (tips.get(position).getScore().equals(""))
                    ((TextView)v.findViewById(R.id.score)).setText(""+tips.get(position).getScore());
                else
                    ((TextView)v.findViewById(R.id.score)).setText("Score:"+tips.get(position).getScore());

                ((TextView) v.findViewById(R.id.score)).setTextColor(Color.argb(250,0,165,0));
                ((TextView) v.findViewById(R.id.correct)).setTextColor(Color.argb(250,0,165,0));
                ((TextView) v.findViewById(R.id.correct)).setText(s);

            }

            return v;
        }
    }
}
