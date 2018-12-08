package com.example.black.ragnarok2.Activities.FireBaseAuth;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.black.ragnarok2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class RegisterActivity extends AppCompatActivity {
    private Uri pickedImage;
    private static int PreqCode =1;
    private static int REQUESTCODE=1;
ProgressDialog progressDialog;
    private ImageView userPhoto;
    private EditText signupInputName, signupInputEmail, signupInputPassword, signupInputAge;
    private Button btnSignUp;
    private Button btnLinkLogin;
    private RadioGroup genderRadioGroup;
    private FirebaseAuth fAuth;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        signupInputName = findViewById(R.id.signup_input_name);
        signupInputEmail = findViewById(R.id.signup_input_email);
        signupInputPassword = findViewById(R.id.signup_input_password);
        signupInputAge = findViewById(R.id.signup_input_age);
        fAuth = FirebaseAuth.getInstance();
        btnSignUp = findViewById(R.id.btn_signup);
        btnLinkLogin = findViewById(R.id.btn_link_login);
        userPhoto = findViewById(R.id.avatar);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 22){
                        checkAndRequestForPermission();
                    }else {
                        openGallery();
                    }
                 }
             });


        genderRadioGroup = findViewById(R.id.gender_radio_group);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignUp.setVisibility(View.INVISIBLE);
                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                String gender;
                if(selectedId == R.id.female_radio_btn)
                    gender = "Female";
                else
                    gender = "Male";
                final String mail = signupInputEmail.getText().toString();
                final String name = signupInputName.getText().toString();
                final String age = signupInputAge.getText().toString();
                final String pw = signupInputPassword.getText().toString();
                if (mail.isEmpty() || name.isEmpty() || age.isEmpty() || pw.isEmpty()){
                    showMessage(String.valueOf(R.string.missingForm));
                    btnSignUp.setVisibility(View.VISIBLE);

                }else {
                    CreateUserAccount(mail,name,age,pw,gender);
                }
            }
        });
        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void CreateUserAccount(final String mail, final String name, final String age, String pw, final String gender) {
          fAuth.createUserWithEmailAndPassword(mail,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                    showMessage(String.valueOf(R.string.success));
                    updateUserInfo(fAuth.getCurrentUser(),name,age,gender,pickedImage);
                  }else{
                      showMessage(String.valueOf(R.string.register_failed)+task.getException().getMessage());
                      btnSignUp.setVisibility(View.VISIBLE);
                  }
              }
          });

    }

    private void updateUserInfo(final FirebaseUser currentUser, final String name, String age, String gender, Uri userPhoto) {
        StorageReference st = FirebaseStorage.getInstance().getReference().child("User_photo");
        final StorageReference imgFilePath = st.child(pickedImage.getLastPathSegment());
        imgFilePath.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                imgFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(pickedImage).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   showMessage(String.valueOf(R.string.success));
                                   updateUI();
                               }
                               else
                               {
                                   showMessage(String.valueOf(R.string.err_msg_dob));
                               }
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent login = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(login);
        finish();

    }


    private void showMessage(String st) {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode == REQUESTCODE && data != null){
           pickedImage = data.getData();
           userPhoto.setImageURI(pickedImage);
        }
    }

    private void openGallery() {
         Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
         galleryIntent.setType("Image/*");
         startActivityForResult(galleryIntent,REQUESTCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(  RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
            }
        }
    }




}