package com.example.easychat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_password extends AppCompatActivity {

    private Button changePass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //initializes an instance of FirebaseAuth and retrieves the current user using the `getCurrentUser()` method.
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();

        //associates the `changePass` button with its corresponding UI element. It also retrieves the input fields for the old password and new password.
        changePass=findViewById(R.id.btnChange);

        EditText oldPass=findViewById(R.id.old_pass);
        EditText NewPass=findViewById(R.id.new_pass);

        //The click listener for the `changePass` button checks if the old password and new password fields are empty. If either of them is empty, it sets an error message and returns.
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldString=oldPass.getText().toString().trim();
                String newString=NewPass.getText().toString().trim();

                if(TextUtils.isEmpty(oldString)){
                    oldPass.setError("Required Field..",null);
                    return;
                }
                if(TextUtils.isEmpty(newString)){
                    NewPass.setError("Required Field..",null);
                    return;
                }

                //If the new password is less than 6 characters, it sets an error message and returns.
                if(newString.length()<6)
                {
                    NewPass.setError("Must contain at least 6 characters..",null);
                    return;
                }

                //The code then reauthenticates the user using their email and old password to confirm their identity.
                if(mAuth.getCurrentUser()!=null) {
                    AuthCredential credential= EmailAuthProvider.getCredential(user.getEmail(),oldString);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // If the reauthentication is successful, it updates the user's password with the new password.
                            if(task.isSuccessful())
                            {
                                user.updatePassword(newString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //If the password update is successful, a Toast message is displayed to inform the user and the user is redirected to the home screen.
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(change_password.this,"Password changed successfully..",Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(change_password.this,LoginActivity.class);
                                            startActivity(intent);
                                        }
                                        // If either the reauthentication or password update fails, a Toast message is displayed to inform the user.
                                        else
                                        {
                                            Toast.makeText(change_password.this,"Something went wrong. Please try again later..",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        //When the back arrow is pressed, the `onBackPressed()` method is called to return to the previous screen.
        ImageView back_arrow=findViewById(R.id.back);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}