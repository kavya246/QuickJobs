package ml.siddharthm.quickjobs_getthingsdone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileRegisterActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private CircleImageView profileImage;
    private ProgressDialog loadingBar;
    private TextView profilePhone;
    private EditText profileName,profileEmail,profileAddress;
    private Button createAccountButton;
   private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static int galleryPic = 1;
    private StorageReference UserProfileImageRef;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        createAccountButton = (Button)findViewById(R.id.profile_register_button);
        myToolbar = findViewById(R.id.my_profile_toolbar);
        if (myToolbar!=null) {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("Register");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            myToolbar.setNavigationIcon(R.drawable.back_btn);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        }

        profileImage = (CircleImageView)findViewById(R.id.profile_register_picture);
        profileName = (EditText)findViewById(R.id.profile_register_name);
        profileEmail = (EditText)findViewById(R.id.profile_register_email);
        profileAddress = (EditText)findViewById(R.id.profile_register_address);
        profilePhone = (TextView)findViewById(R.id.profile_register_phone);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPic);
            }
        });

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("phonenumber");
                profilePhone.setText(newString);
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("phonenumber");
            profilePhone.setText(newString);
        }

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });



    }




    private void CreateNewAccount() {

        if (TextUtils.isEmpty(profileEmail.getText().toString())){
            Toast.makeText(this,"Feild Cannot be empty",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(profileAddress.getText().toString())){
            Toast.makeText(this,"Feild Cannot be empty",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(profileName.getText().toString())){
            Toast.makeText(this,"Feild Cannot be empty",Toast.LENGTH_SHORT);
        }
        else{



           String currentUserId = mAuth.getCurrentUser().getUid();

            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",profileName.getText().toString());
          /*  profileMap.put("phone",profilePhone.getText().toString()); */
            profileMap.put("email",profileEmail.getText().toString());
            profileMap.put("address",profileAddress.getText().toString());
            RootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Intent registerIntent = new Intent(ProfileRegisterActivity.this,HomeActivity.class);
                        startActivity(registerIntent);
                        Toast.makeText(ProfileRegisterActivity.this,"Account Created Succesfully",Toast.LENGTH_SHORT);

                    }else {
                        String error = task.getException().toString();
                        Toast.makeText(ProfileRegisterActivity.this,error,Toast.LENGTH_SHORT);
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==galleryPic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {


                final Uri resultUri = result.getUri();
                StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileRegisterActivity.this, "Image uploaded succesfully...", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            RootRef.child("Users").child(currentUserId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileRegisterActivity.this, "Image saved in db succesfully", Toast.LENGTH_SHORT).show();
                                        ((CircleImageView) findViewById(R.id.profile_register_picture)).setImageURI(resultUri);

                                    } else {
                                        Toast.makeText(ProfileRegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ProfileRegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });
            }}
    }


}
