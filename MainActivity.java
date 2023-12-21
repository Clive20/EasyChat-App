package com.example.easychat.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.easychat.Adapters.TabsAccessorAdapter;
import com.example.easychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager myviewPager;
    private TabLayout mytabLayout;
    private TabsAccessorAdapter mytabsAccessorAdapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;
    SharedPreferences sharedPreferences;
    boolean isDarkModeOn;
    SharedPreferences.Editor editor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");

        myviewPager = findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(mytabsAccessorAdapter);

        mytabLayout = findViewById(R.id.main_tabs);
        mytabLayout.setupWithViewPager(myviewPager);
        sharedPreferences
                = getSharedPreferences(
                "sharedPrefs", MODE_PRIVATE);
        editor1 = sharedPreferences.edit();
        isDarkModeOn
                = sharedPreferences
                .getBoolean(
                        "isDarkModeOn", false);

        // When user reopens the app
        // after applying dark/light mode
        if (isDarkModeOn) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);

        } else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            updateUserStatus("online");
            VerifyUserexistance();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUserStatus("offline");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUserStatus("offline");
        }
    }

    private void VerifyUserexistance() {
        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    //Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                } else {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToLoginActivity() {
        Intent loginintent = new Intent(MainActivity.this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    private void sendUserToSettingsActivity() {
        Intent settingsintent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsintent);
    }

    private void sendUserToFindFriendsActivity() {
        Intent friendsintent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(friendsintent);
    }

    private void updateUserStatus(String state) {
        String savecurrentTime, savecurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        savecurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        savecurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", savecurrentTime);
        hashMap.put("date", savecurrentDate);
        hashMap.put("state", state);

        currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentUserId).child("userState").updateChildren(hashMap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences shared = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();

        int id = item.getItemId();
        if (id == R.id.main_find_friends_menu) {
            sendUserToFindFriendsActivity();
        } else if (id == R.id.myswitch) {
            if (isDarkModeOn) {

                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_NO);
                Toast.makeText(this, "Theme Changed", Toast.LENGTH_SHORT).show();

                editor1.putBoolean(
                        "isDarkModeOn", false);
                editor1.apply();
                
                // change text of Button

            } else {


                Toast.makeText(this, "Theme Changed", Toast.LENGTH_SHORT).show();

                // if dark mode is off
                // it will turn it on
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_YES);

                // it will set isDarkModeOn
                // boolean to true
                editor1.putBoolean(
                        "isDarkModeOn", true);
                editor1.apply();

                // change text of Button

            }
        }

        else if(id ==R.id.main_settings_menu)

    {
        sendUserToSettingsActivity();
    } else if(id ==R.id.main_logout_menu)

    {
        updateUserStatus("offline");
        firebaseAuth.signOut();
        sendUserToLoginActivity();
        Toast.makeText(MainActivity.this, "User logged out succuessfully...", Toast.LENGTH_SHORT).show();
    } else if(id ==R.id.main_create_group_menu)

    {
        RequestNewGroup();
    }else if (id == R.id.s21)
        {
            editor.clear();
            editor.putString("size", "small");
            editor.commit();
        }
        else if (id == R.id.s22)
        {
            editor.clear();
            editor.putString("size", "medium");
            editor.commit();
        }
        else if (id == R.id.s33)
        {
            editor.clear();
            editor.putString("size", "large");
            editor.commit();
        }
        return true;
}


    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name: ");

        final EditText groupnamefield = new EditText(MainActivity.this);
        groupnamefield.setHint("e.g. Family members");
        builder.setView(groupnamefield);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname = groupnamefield.getText().toString();

                if (TextUtils.isEmpty(groupname)) {
                    Toast.makeText(MainActivity.this, "Please write a group name...", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(groupname);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(final String groupname) {
        databaseReference.child("Groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(MainActivity.this, groupname + " group is created Successfully...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        showCustomDialog_back();
    }

    private void showCustomDialog_back() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.privacy);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        (dialog.findViewById(R.id.button)).setOnClickListener(v -> dialog.dismiss());

        (dialog.findViewById(R.id.button2)).setOnClickListener(v -> {
            finishAffinity();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
