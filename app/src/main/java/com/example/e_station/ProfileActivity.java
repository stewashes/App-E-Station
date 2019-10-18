package com.example.e_station;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static String TAG = "ProfileActivity";

    // Posso settare qualsiasi numero
    private static final int CHOOSE_IMAGE = 101;
    private CircularImageView profilePic;
    private TextView username;
    private TextView email;
    private ProgressBar progressBar;
    private FloatingActionButton modifica;
    private Button reset;
    private Button delete;

    String profilePicUrl;

    Uri uriProfilePic;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profilo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        profilePic = (CircularImageView) findViewById(R.id.profilepic);
        username = (TextView) findViewById(R.id.us);
        email = (TextView) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        modifica = (FloatingActionButton) findViewById(R.id.modifica);
        reset = (Button) findViewById(R.id.reset_pwd);
        delete = (Button) findViewById(R.id.delete_acc);


        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.GREEN), PorterDuff.Mode.MULTIPLY);

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectivityHelper.controllaConnessione(getApplicationContext())) {
                    showImageChoser();
                } else {
                    Toast.makeText(getApplicationContext(), "Non connesso ad internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (ConnectivityHelper.controllaConnessione(this)) {
            loadUserInformation();
        } else {
            Toast.makeText(this, "Non connesso ad internet", Toast.LENGTH_LONG).show();
        }


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mAuth.getCurrentUser();

                if (user != null && user.getEmail() != null) {
                    // AlertDialog Builder class
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                            .setIcon(R.drawable.ic_attenzione)
                            .setTitle("Reset Password")
                            .setMessage(("Cliccando su 'Conferma' verrà inviata una email per " +
                                    "aggiornare la sua password" ))
                            .setPositiveButton("CONFERMA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAuth.sendPasswordResetEmail((user.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Controlla la tua email...", Toast.LENGTH_LONG).show();
                                            } else {
                                                String errorMessage = task.getException().getMessage();
                                                Toast.makeText(getApplicationContext(), "Errore: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("ANNULLA", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                            .setIcon(R.drawable.ic_attenzione)
                            .setTitle("Eliminazione Account")
                            .setMessage(("Cliccando su 'Conferma' verrà definitivamente eliminato l' account" +
                                    ". Dopo verrai reindirizzato al Login" ))
                            .setPositiveButton("CONFERMA", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Account eliminato correttamente", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(ProfileActivity.this , StartActivity.class);

                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "L'account non può esser eliminato... ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });}
                            })
                            .setNegativeButton("ANNULLA" , null);

                    AlertDialog dialog = builder.create();
                    dialog.show();



                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // controllo
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }
    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {

                String url = user.getPhotoUrl().toString();

                Glide.with(this)
                        .load(url)
                        .fitCenter()
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .into(profilePic);
            }
            if (user.getDisplayName() != null) {
                username.setText(user.getDisplayName());
            }
            if (user.getEmail() != null) {
                email.setText(user.getEmail());
            }
        }


    }

    private void saveUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profilePicUrl != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profilePicUrl))
                    .build();

            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profilo aggiornato", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check se requestCode è uguale a chose_image
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // ritorna l uri dell immagine
            uriProfilePic = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfilePic);
                profilePic.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("immaginiprofilo/" + System.currentTimeMillis() + "." + getFileExtension(uriProfilePic));

        if (uriProfilePic != null) {
            progressBar.setVisibility(View.VISIBLE);

            profileImageRef.putFile(uriProfilePic)

                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // return storageReference.getDownloadUrl();
                            return profileImageRef.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                profilePicUrl = task.getResult().toString();

                                Log.e(TAG, "then: " + uriProfilePic.toString());

                                saveUserInformation();

                            } else {
                                Toast.makeText(ProfileActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showImageChoser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Seleziona l' immagine del profilo: "), CHOOSE_IMAGE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
