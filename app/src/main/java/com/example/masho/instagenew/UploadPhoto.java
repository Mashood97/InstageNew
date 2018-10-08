package com.example.masho.instagenew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPhoto extends AppCompatActivity {


    private Button btn;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private StorageReference mStorageref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        mStorageref = FirebaseStorage.getInstance().getReference();
        btn = (Button) findViewById(R.id.Uploadbtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageref = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 12);
            }
        });
        databaseReference = database.getInstance().getReference().child("Photos");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12 && resultCode == RESULT_OK) {
            final Uri file = data.getData();
            final StorageReference filepath = mStorageref.child("Photos").child(file.getLastPathSegment());
            filepath.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(UploadPhoto.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            DatabaseReference newPost=databaseReference.push();
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("Uid").setValue(user.getUid());
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadPhoto.this, "Image cant Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
