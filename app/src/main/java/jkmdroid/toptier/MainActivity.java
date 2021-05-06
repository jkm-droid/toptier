package jkmdroid.toptier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.BuildConfig;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 11/6/2017.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TabLayout tabLayout;
    ViewPager viewPager;
    int selectedTab = 0;
    DrawerLayout drawer;
    boolean stopThread = false;
    FragmentLatest fragmentLatest;
    FragmentHome fragmentHome;
    FragmentAllMatches fragmentAllMatches;
    FragmentProfile fragmentProfile;
    private ArrayList<Tip> latestTips, allMacthesTips;
    boolean requestSuccessful = false;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar) ;
        drawer = findViewById(R.id.drawer_layout) ;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer , toolbar , R.string.navigation_drawer_open ,
                R.string.navigation_drawer_close) ;
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view ) ;
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        init();
    }
    void init(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Guaranteed Win");

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            selectedTab = extras.getInt("tab", 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 3);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 88);
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText(""));
        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.addTab(tabLayout.newTab().setText(""));
        tabLayout.getTabAt(1).setIcon(R.drawable.latest);
        tabLayout.addTab(tabLayout.newTab().setText(""));
        tabLayout.getTabAt(2).setIcon(R.drawable.sports_soccer);
        tabLayout.addTab(tabLayout.newTab().setText(""));
        tabLayout.getTabAt(3).setIcon(R.drawable.profile);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final MyAdapter adapter = new MyAdapter(MainActivity.this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(selectedTab).select();
        notification();
        background();
    }

    private void notification() {

        //perform the work periodically, every 10 minutes e.t.c
        final PeriodicWorkRequest messagesWorkRequest = new PeriodicWorkRequest
                .Builder(NotificationWorker.class, 6, TimeUnit.HOURS)
                .build();


        //initiate the work using work manager
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(messagesWorkRequest);

        workManager.getWorkInfoByIdLiveData(messagesWorkRequest.getId()).observe(
                this, workInfo -> {
                    if (workInfo != null) {
                        Log.d("periodicWorkRequest", "Status changed to : " + workInfo.getState());
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopThread = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopThread = false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat. START )) {
            drawer.closeDrawer(GravityCompat. START ) ;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you really want to exit?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();
                        }
                    }).create().show();
        }
    }

    String string = "https://play.google.com/store/apps/details?id=jkmdroid.toptier";

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
        if (drawer.isDrawerOpen(GravityCompat. START )){
            drawer.closeDrawer(GravityCompat. START ) ;
        }
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.privacy:
                Intent privacy = new Intent(Intent.ACTION_VIEW);
                privacy.setData(Uri.parse("https://toptier.mblog.co.ke/privacy.html"));
                startActivity(privacy);
                break;
            case R.id.terms:
                Intent terms = new Intent(Intent.ACTION_VIEW);
                terms.setData(Uri.parse("https://toptier.mblog.co.ke/terms.html"));
                startActivity(terms);
                break;
            case R.id.developers:
                Intent dev = new Intent(Intent.ACTION_VIEW);
                dev.setData(Uri.parse("https://jpdevelopers.mblog.co.ke"));
                startActivity(dev);
                break;
            case R.id.whatsapp:
                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+
                            URLEncoder.encode("+254738801655", "UTF-8") +"&text=" + URLEncoder.encode("Hello GolPredicts", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.email:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "toptierodds@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                email.putExtra(Intent.EXTRA_TEXT, "Hello TopTier Odds");

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
            case R.id.telegram:
                String url = "http://t.me/toptierodds";
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(url));
                startActivity(Intent.createChooser(intent1, "Choose browser"));
                break;
            case R.id.logout:
                logout_user();
                break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share_button) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Top-Tier Odds");
                intent.putExtra(Intent.EXTRA_TEXT, string);
                startActivity(Intent.createChooser(intent, "Share with"));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //method to logout the user
    private void logout_user() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.progressDialogColor);
        String titleText = "Confirm Exit!";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
        ssBuilder.setSpan(foregroundColorSpan,0,titleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        alertDialog.setTitle(ssBuilder);
        alertDialog.setIcon(R.drawable.logout_icon);
        alertDialog.setMessage("Are you sure you want to exit?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //if clicked
                //close the dialog box,
                //log out the user
                SharedPreferences preferences = getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(MainActivity.this, UserActivity.class));
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if clicked
                //close the dialog box and do nothing
                dialog.cancel();
            }
        });
        //create the alert box and display it to the user
        alertDialog.create().show();
    }

    private ArrayList<Tip> extractTips(JSONObject response) {
        JSONArray array = null;
        JSONObject object;
        ArrayList<Tip> tips = new ArrayList<>();
        try {
            array = response.getJSONArray("tips");
            Tip tip;
            int s = array.length();
            for (int i = 0; i < s; i++){
                tip = new Tip();
                object = array.getJSONObject(i);
                tip.setId(object.getInt("id"));
                tip.setTeamA(object.getString("teamA"));
                tip.setTeamB(object.getString("teamB"));
                tip.setHome(object.getDouble("home"));
                tip.setWinLose(object.getString("wl_status"));
                tip.setDraw(object.getDouble("draw"));
                tip.setAway(object.getDouble("away"));
                tip.setCorrect(object.getString("correct_tip").trim());
                try {
                    tip.setMatchTime(object.getString("match_time"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tip.setVipStatus(object.getInt("vip_status"));
                try {
                    tip.setCreatedAt(object.getString("created_at"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tips.add(tip);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tips;
    }

    private void background() {
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                SharedPreferences preferences = getSharedPreferences(Preferences.Login.NAME, MODE_PRIVATE);
                int statusCode = preferences.getInt(Preferences.Login.STATUS, 5);
                String data = "";
                try {
                    data += URLEncoder.encode("latest_matches", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + "&";
                    data += URLEncoder.encode("keyword", "UTF-8") + "=" + URLEncoder.encode(""+statusCode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = "https://toptier.mblog.co.ke/get_tips.php?" + data;

                PostJson postJson = new PostJson(MainActivity.this, url), postJson1, postJson2;
                postJson.setOnSuccessListener(response -> {
                    requestSuccessful = true;
                    latestTips = extractTips(response);

                    if (fragmentLatest == null){
                        fragmentLatest = new FragmentLatest();
                        fragmentLatest.setOnFragmentRestart(() -> {
                            if (latestTips != null)
                                fragmentLatest.setTips(latestTips);
                            else fragmentLatest.setTips(new ArrayList<>());
                        });
                    }
                    fragmentLatest.setTips(latestTips);

                });
                postJson.get();
                data  = "";
                try {
                    data += URLEncoder.encode("all_matches", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + "&";
                    data += URLEncoder.encode("keyword", "UTF-8") + "=" + URLEncoder.encode(""+statusCode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url = "https://toptier.mblog.co.ke/get_tips.php?" + data;
                postJson1 = new PostJson(MainActivity.this, url);
                postJson1.setOnSuccessListener(new PostJson.OnSuccessListener() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        allMacthesTips = extractTips(response);

                        requestSuccessful = true;

                        if (fragmentAllMatches == null){
                            fragmentAllMatches = new FragmentAllMatches();
                            fragmentAllMatches.setOnFragmentRestart(new FragmentAllMatches.OnFragmentRestart() {
                                @Override
                                public void onTipsReceived() {
                                    if (allMacthesTips != null)
                                        fragmentAllMatches.setTips(allMacthesTips);
                                    else fragmentAllMatches.setTips(new ArrayList<>());
                                }
                            });
                        }
                        fragmentAllMatches.setTips(allMacthesTips);
                    }
                });
                postJson1.get();
                url = "https://toptier.mblog.co.ke/home_details.php?get_details";
                postJson2 = new PostJson(MainActivity.this, url);
                postJson2.setOnSuccessListener(new PostJson.OnSuccessListener() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        if (fragmentHome == null)
                            fragmentHome = new FragmentHome();
                        fragmentHome.setData(response.getInt("upcoming_matches"), response.getInt("all_matches"));
                    }
                });
                postJson2.get();
            }
        };
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if (stopThread)
                    return;
                handler.sendEmptyMessage(1);

                try {
                    if (requestSuccessful)
                        sleep(120000);
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                run();
            }
        };
        thread.start();
    }

    public class MyAdapter extends FragmentPagerAdapter {
        Context context;
        int totalTabs;

        public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
            super(fm);
            this.context = context;
            this.totalTabs = totalTabs;
        }

        // this is for fragment tabs
        @Override
        public Fragment getItem(int position){
//            System.out.println("Requesting fragment for position "+position);
            switch (position){
                case 0:
                    if (fragmentHome == null)
                        fragmentHome = new FragmentHome();
                    return fragmentHome;
                case 1:
                    if (fragmentLatest == null){
                        fragmentLatest = new FragmentLatest();
                        fragmentLatest.setOnFragmentRestart(new FragmentAllMatches.OnFragmentRestart() {
                            @Override
                            public void onTipsReceived() {
                                if (latestTips != null)
                                    fragmentLatest.setTips(latestTips);
                                else fragmentLatest.setTips(new ArrayList<>());
                            }
                        });
                    }
                    if (latestTips != null)
                        fragmentLatest.setTips(latestTips);
                    return fragmentLatest;
                case 2:
                    if (fragmentAllMatches == null){
                        fragmentAllMatches = new FragmentAllMatches();
                        fragmentAllMatches.setOnFragmentRestart(new FragmentAllMatches.OnFragmentRestart() {
                            @Override
                            public void onTipsReceived() {
                                if (allMacthesTips != null)
                                    fragmentAllMatches.setTips(allMacthesTips);
                                else fragmentAllMatches.setTips(new ArrayList<>());
                            }
                        });
                    }
                    if (allMacthesTips != null)
                        fragmentAllMatches.setTips(allMacthesTips);
                    return fragmentAllMatches;
                case 3:
                    if (fragmentProfile == null)
                        fragmentProfile = new FragmentProfile();
                    return fragmentProfile;
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}