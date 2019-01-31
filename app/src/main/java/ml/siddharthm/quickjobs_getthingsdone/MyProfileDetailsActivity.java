package ml.siddharthm.quickjobs_getthingsdone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class MyProfileDetailsActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private EditText userName, userEmail, userAddress;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static int galleryPic = 1;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileImageRef;
    private Button updateAccountSettings;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_details);

        userName = (EditText) findViewById(R.id.my_profile_name);
        userEmail = (EditText)findViewById(R.id.my_profile_email);
        userAddress = (EditText) findViewById(R.id.my_profile_address);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_picture);
        loadingbar = new ProgressDialog(this);
        updateAccountSettings = (Button) findViewById(R.id.my_profile_button);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        myToolbar = findViewById(R.id.my_profile_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            myToolbar.setNavigationIcon(R.drawable.back_btn);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


            updateAccountSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdateSettings();
                }
            });
            RetriveUserInfo();

            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, galleryPic);
                }
            });
        }


    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPic && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("PLEASE WAIT...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                final Uri resultUri = result.getUri();
                StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyProfileDetailsActivity.this, "Image uploaded succesfully...", Toast.LENGTH_SHORT);
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            RootRef.child("Users").child(currentUserId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MyProfileDetailsActivity.this, "Image saved in db succesfully", Toast.LENGTH_SHORT);
                                        ((CircleImageView) findViewById(R.id.profile_register_picture)).setImageURI(resultUri);
                                        loadingbar.dismiss();
                                    } else {
                                        Toast.makeText(MyProfileDetailsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                                        loadingbar.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MyProfileDetailsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                            loadingbar.dismiss();

                        }
                    }
                });
            }
        }

    }






    private void RetriveUserInfo () {
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {
                    String retriveUserName = dataSnapshot.child("name").getValue().toString();
                    String retriveEmail = dataSnapshot.child("email").getValue().toString();
                    String retriveAddress = dataSnapshot.child("address").getValue().toString();
                    String retriveProfileImage = dataSnapshot.child("image").getValue().toString();

                    userName.setText(retriveUserName);
                    userEmail.setText(retriveEmail);
                    userAddress.setText(retriveAddress);
                    Picasso.get().load(retriveProfileImage).into(userProfileImage);


                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {

                    String retriveUserName = dataSnapshot.child("name").getValue().toString();
                    String retriveEmail = dataSnapshot.child("email").getValue().toString();
                    String retriveAddress = dataSnapshot.child("address").getValue().toString();

                    userName.setText(retriveUserName);
                    userEmail.setText(retriveEmail);
                    userAddress.setText(retriveAddress);


                } else {
                    Toast.makeText(MyProfileDetailsActivity.this, "Please update your info", Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void UpdateSettings () {
        String setUserName = userName.getText().toString();
        String setUserEmail = userEmail.getText().toString();
        String setUserAddress = userAddress.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your User name", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(setUserAddress)) {
            Toast.makeText(this, "Please write your Status", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(setUserEmail)) {
            Toast.makeText(this, "Please write your Status", Toast.LENGTH_SHORT);
        }
        else
         {

             HashMap<String,String> profileMap = new HashMap<>();
             profileMap.put("uid",currentUserId);
             profileMap.put("name",userName.getText().toString());
             /*  profileMap.put("phone",profilePhone.getText().toString()); */
             profileMap.put("email",userEmail.getText().toString());
             profileMap.put("address",userAddress.getText().toString());
            RootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyProfileDetailsActivity.this, "Profile Updated Succesfully", Toast.LENGTH_SHORT);
                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(MyProfileDetailsActivity.this, error, Toast.LENGTH_SHORT);
                    }
                }
            });
        }

    }

}

