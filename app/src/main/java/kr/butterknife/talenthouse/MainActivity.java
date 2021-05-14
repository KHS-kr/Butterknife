package kr.butterknife.talenthouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private String TAG = "MAIN_TAG";
    private BottomNavigationView bottomNavigationView;
    private MainFragment mainFrag;
    private MyPageFragment myPageFrag;
    private long BACK_PREESED_TIME = 2000L;
    private long cur = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFCM();
        getFirebaseToken();

        mainFrag = new MainFragment();

        bottomNavigationView = findViewById(R.id.main_bottomnavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.btmnavi_home);

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.btmnavi_home :
                if(getVisibleFragment(mainFrag)) {
                    clearBackStack();
                    mainFrag = new MainFragment();
                    replaceFragment(mainFrag, "Main");
                }
                return true;
            case R.id.btmnavi_favorite:
                return true;
            case R.id.btmnavi_menu :
                return true;
            case R.id.btmnavi_mypage :
                mainFrag.clearPlayer();
                myPageFrag = new MyPageFragment();
                replaceFragment(myPageFrag, "myPage");
                return true;
            case R.id.btmnavi_search :
                return true;
            default :
                Log.e(TAG, "bottom navigation view clicked");
        }
        return false;
    }

    public void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
            fragmentManager.popBackStack();
    }

    public void replaceFragment(Fragment fragment, String backstackName){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_ll, fragment);
        fragmentTransaction.addToBackStack(backstackName);
        fragmentTransaction.commit();
    }


    public void finishFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment).commit();
        fragmentManager.popBackStack();
    }

    public boolean getVisibleFragment(Fragment fragment) {
        for(Fragment f : getSupportFragmentManager().getFragments()) {
            if(f.isVisible()) {
                if(f.equals(fragment))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!getVisibleFragment(mainFrag)) {
            if(System.currentTimeMillis() - cur < BACK_PREESED_TIME) {
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                cur = System.currentTimeMillis();
            }
        }
        else {
            super.onBackPressed();
            FragmentManager fm = getSupportFragmentManager();
            switch(fm.getBackStackEntryAt(0).getName()) {
                case "Main" :
                    bottomNavigationView.setSelectedItemId(R.id.btmnavi_home);
                    break;
                case "myPage" :
                    bottomNavigationView.setSelectedItemId(R.id.btmnavi_mypage);
                    break;
                default :
                    break;
            }
        }
    }

    private void initFCM() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(
                    new NotificationChannel(channelId,
                            channelName, NotificationManager.IMPORTANCE_HIGH)
            );
        }
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();

                if(token != null) {
                    Util.INSTANCE.registerFCMToken(getApplicationContext(), token);
                    Log.d("FCM", token);
                }
            }
        });
    }
}