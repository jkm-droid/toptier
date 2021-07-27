package jkmdroid.toptier;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.IOException;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

public class FragmentProfile extends Fragment {
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container,false);
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);
        ((TextView)view.findViewById(R.id.email)).setText(preferences.getString(Preferences.Login.EMAIL, ""));

//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Profile");

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (preferences.getInt(Preferences.Login.STATUS, 5) == 5){
            ((TextView)view.findViewById(R.id.status)).setText("Normal");
            view.findViewById(R.id.join_vip).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyHelper.isOnline(getActivity()))
                        startActivity(new Intent(getActivity(), Subscription.class));
                    else
                        Toast.makeText(getActivity(), "Enable internet connection", Toast.LENGTH_LONG).show();
                }
            });

        }
        else if (preferences.getInt(Preferences.Login.STATUS, 5) == 10){
            ((TextView)view.findViewById(R.id.status)).setText("VIP");
            ((TextView)view.findViewById(R.id.profile_message)).setText("Thank you for becoming one of our VIP members\nEnjoy our privileged services, VIP tips, and 24hours support");

           //((LinearLayout) view.findViewById(R.id.vip)).removeAllViews();
            LinearLayout linearLayout =  view.findViewById(R.id.vip);
            linearLayout.removeView(view.findViewById(R.id.join_vip));
        }
        return view;
    }
}