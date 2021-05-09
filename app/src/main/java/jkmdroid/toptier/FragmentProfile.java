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
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

public class FragmentProfile extends Fragment {
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, null);
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);
        ((TextView)view.findViewById(R.id.email)).setText(preferences.getString(Preferences.Login.EMAIL, ""));
        ((Switch) view.findViewById(R.id.deactivate)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    progressDialog = new ProgressDialog(getActivity(),R.style.progressDialogColor);
                    progressDialog.setMessage("Deactivating... Please wait");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    if (progressDialog != null)
                        progressDialog.show();
                    String email = preferences.getString(Preferences.Login.EMAIL, "");

                    String d = "";
                    try {
                        d += URLEncoder.encode("deactivate_account", "UTF-8") + "=" + URLEncoder.encode("c", "UTF-8") + "&";
                        d += URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&";
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    final String data = d;
                    final String link = "https://toptier.mblog.co.ke/deactivate_account.php";
                    @SuppressLint("HandlerLeak") Handler handler = new Handler(){
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            while (progressDialog.isShowing())
                                progressDialog.dismiss();
                            if(((String)msg.obj).equalsIgnoreCase("Deactivated successfully")){
                                Toast.makeText(getActivity(), "Deactivated", Toast.LENGTH_LONG).show();
                                SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(getActivity(), UserActivity.class));
                                getActivity().finish();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(((String)msg.obj))
                                        .setTitle("Error Occurred")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }
                    };
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                String response = MyHelper.connectOnline(link, data);
                                Message message = new Message();
                                message.arg1 = 1;
                                message.obj = response;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();

                }
            }
        });
        if (preferences.getInt(Preferences.Login.STATUS, 5) == 5){
            ((TextView)view.findViewById(R.id.status)).setText("Normal");
            view.findViewById(R.id.join_vip).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyHelper.isOnline(getActivity()))
                        startActivity(new Intent(getActivity(), SubscribeActivity.class));
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