package jkmdroid.toptier;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class FragmentLatest extends Fragment{
    private ArrayList<Tip> tips;
    ListView listView;
    TextView errorView;
    ImageView imageError;
    private OnFragmentRestart onFragmentRestart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_matches, container, false);
        listView = view.findViewById(R.id.listview);

//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Upcoming Matches");

        errorView = view.findViewById(R.id.error);
        imageError = view.findViewById(R.id.image_error);

        if (MyHelper.isOnline(getActivity())) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Loading tips....");
        }else {
            imageError.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("There is no internet connection!!");
            errorView.setTextColor(this.getResources().getColor(R.color.errorColor));
        }

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(view);
        if (onFragmentRestart != null)
            onFragmentRestart.onTipsReceived();
        return layout;
    }
    public void setTips(ArrayList<Tip> tips){
        this.tips = tips;
        if (getContext() == null)
            return;

        if (tips != null && tips.size() > 0){
            errorView.setVisibility(View.GONE);
            listView.setAdapter(new Adapter(getContext(), tips));
        }
        if(tips == null){
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("No tips found!!");
            errorView.setTextColor(this.getResources().getColor(R.color.errorColor));
        }
    }
    public void setOnFragmentRestart(OnFragmentRestart onFragmentRestart){
        this.onFragmentRestart = onFragmentRestart;
    }

    interface  OnFragmentRestart{
        void onTipsReceived();
    }

    class Adapter extends ArrayAdapter{
        public Adapter(@NonNull Context context, @NonNull List objects){
            super(context, R.layout.latest_matches, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View v;
            if (convertView == null)
                v = LayoutInflater.from(getContext()).inflate(R.layout.latest_matches, null);
            else v = convertView;

            ((TextView)v.findViewById(R.id.time)).setText(MyHelper.toPostDate(tips.get(position).getMatchTime()));
            ((TextView)v.findViewById(R.id.team1)).setText(tips.get(position).getTeamA());
            ((TextView)v.findViewById(R.id.team2)).setText(tips.get(position).getTeamB());

            if (tips.get(position).getVipStatus() == 10){
                ((TextView) v.findViewById(R.id.vip_status)).setText("VIP");
            }

            String correct = tips.get(position).getCorrect(), s;
            s = "Pick: "+correct;
            ((TextView) v.findViewById(R.id.correct)).setText(s);

            return v;
        }
    }
}