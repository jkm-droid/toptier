package jkmdroid.toptier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentAllMatches extends Fragment{
    private ListView listView;
    private OnFragmentRestart onFragmentRestart;
    private ArrayList<Tip> tips;
    private int widthOfDevice;
    TextView errorView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_latest, null);
        listView = view.findViewById(R.id.listview);
        listView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            widthOfDevice = listView.getWidth();
        });
        ((TextView) view.findViewById(R.id.title)).setText("PAST MATCHES");

        errorView = view.findViewById(R.id.error);

        if (MyHelper.isOnline(getActivity()))
            errorView.setText("Loading tips....");
        else {
            errorView.setText("There is no internet connection!!");
            errorView.setTextColor(this.getResources().getColor(R.color.errorColor));
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = 100;

        errorView.setLayoutParams(params);

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
            errorView.setText("No tips found");
        if (getContext() == null)
            return;
        listView.setAdapter(new Adapter(getContext(), tips));
        if (tips.size() > 0){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params.gravity = Gravity.CENTER;
            params.topMargin = 1;

            errorView.setLayoutParams(params);
            errorView.setText("");
        }
    }
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
            s = "Pick: " + correct + " -> ";

            if (winlose.equalsIgnoreCase("won")) {
                imgWinlose.setImageResource(R.drawable.won_status);

                if (correct.equalsIgnoreCase("home")){
                    ((TextView)v.findViewById(R.id.team1)).setTextColor(Color.argb(250,0,165,0));
                    ((TextView)v.findViewById(R.id.homeodds)).setTextColor(Color.argb(250,0,165,0));
                    ((TextView)v.findViewById(R.id.home)).setTextColor(Color.argb(250,0,165,0));
                    s += tips.get(position).getHome();

                }else if(correct.equalsIgnoreCase("draw")){
                    ((TextView)v.findViewById(R.id.vs)).setTextColor(Color.argb(250,0,165,0));
                    ((TextView)v.findViewById(R.id.drawodds)).setTextColor(Color.argb(250,0,165,0));
                    ((TextView)v.findViewById(R.id.draw)).setTextColor(Color.argb(250,0,165,0));
                    s += tips.get(position).getDraw();

                }else if (correct.equalsIgnoreCase("away")){
                    ((TextView)v.findViewById(R.id.team2)).setTextColor(Color.argb(250,0,165,0));
                    ((TextView)v.findViewById(R.id.awayodds)).setTextColor(Color.argb(250,0,165,0));
                    ((TextView)v.findViewById(R.id.away)).setTextColor(Color.argb(250,0,165,0));
                    s += tips.get(position).getAway();

                }else{
                    s = "Pick -> "+tips.get(position).getCorrect();
                }
                if (tips.get(position).getScore().equals(""))
                    ((TextView)v.findViewById(R.id.score)).setText(""+tips.get(position).getScore());
                else
                    ((TextView)v.findViewById(R.id.score)).setText("Score:"+tips.get(position).getScore());

                ((TextView) v.findViewById(R.id.score)).setTextColor(Color.argb(250,0,165,0));
                ((TextView) v.findViewById(R.id.correct)).setTextColor(Color.argb(250,0,165,0));
                ((TextView) v.findViewById(R.id.correct)).setText(s);

            }else if(winlose.equalsIgnoreCase("lost")){
                imgWinlose.setImageResource(R.drawable.lost_status);

                if (correct.equalsIgnoreCase("home")) {
                    ((TextView) v.findViewById(R.id.team1)).setTextColor(Color.argb(250, 219, 18, 15));
                    ((TextView) v.findViewById(R.id.homeodds)).setTextColor(Color.argb(250, 219, 18, 15));
                    ((TextView) v.findViewById(R.id.home)).setTextColor(Color.argb(250, 219, 18, 15));
                    s += tips.get(position).getHome();

                }else if(correct.equalsIgnoreCase("draw")){
                    ((TextView)v.findViewById(R.id.vs)).setTextColor(Color.argb(250,219,18,15));
                    ((TextView)v.findViewById(R.id.drawodds)).setTextColor(Color.argb(250,219,18,15));
                    ((TextView)v.findViewById(R.id.draw)).setTextColor(Color.argb(250,219,18,15));
                    s += tips.get(position).getDraw();

                }else if (correct.equalsIgnoreCase("away")){
                    ((TextView)v.findViewById(R.id.team2)).setTextColor(Color.argb(250,219,18,15));
                    ((TextView)v.findViewById(R.id.awayodds)).setTextColor(Color.argb(250,219,18,15));
                    ((TextView)v.findViewById(R.id.away)).setTextColor(Color.argb(250,219,18,15));
                    s += tips.get(position).getAway();

                }else {
                    s = "Pick -> "+tips.get(position).getCorrect();
                }
                if (tips.get(position).getScore().equals(""))
                    ((TextView)v.findViewById(R.id.score)).setText(""+tips.get(position).getScore());
                else
                    ((TextView)v.findViewById(R.id.score)).setText("Score:"+tips.get(position).getScore());

                ((TextView) v.findViewById(R.id.score)).setTextColor(Color.argb(250, 219, 18, 15));
                ((TextView) v.findViewById(R.id.correct)).setTextColor(Color.argb(250, 219, 18, 15));
                ((TextView) v.findViewById(R.id.correct)).setText(s);
            }

            return v;
        }
    }
}