package com.learntodroid.androidqrcodescanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

public class MainActivity<ApiFuture> extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Button qrCodeFoundButton;
    private String qrCode;
    FirebaseDatabase firebaseDatabase; // entry point
    DatabaseReference databaseReference;
    DatabaseReference db,alimentaire,chimique,electronique;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.activity_main_previewView);
        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton);
        qrCodeFoundButton.setVisibility(View.INVISIBLE);

        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                    NotificationChannel ch = new NotificationChannel("myNotification","myNotification", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manage = getSystemService(NotificationManager.class);
                    manage.createNotificationChannel(ch);
                }
                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("UPDATE").setMessage("DO YOU WANT TO ADD NEW ITEM TO YOUR FIREBASE?").setCancelable(true).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // we will work here

            




                        addTodatabase("Products");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"myNotification");
                        builder.setContentTitle("Alert");
                        builder.setContentText("A product has been added");
                        builder.setSmallIcon(R.drawable.ic_launcher_background);
                        builder.setAutoCancel(true);

                        NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
                        manager.notify(1,builder.build());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                    }
                }).show();

            }
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
    };
    void addTodatabase(String ref){
        firebaseDatabase = FirebaseDatabase.getInstance("https://androidstudiopfe-default-rtdb.firebaseio.com/").getReference().getDatabase();
        db = firebaseDatabase.getReference(ref).push();
        db.setValue(qrCode);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater flater = getMenuInflater();
        flater.inflate(R.menu.settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LogintTo.class));
                break;
            case R.id.dashboard_menu:
                startActivity(new Intent(MainActivity.this,dashboard.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


// it's all about scanning the qrcode and provide camera permission to work (lovely)
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                qrCodeFoundButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void qrCodeNotFound() {
                qrCodeFoundButton.setVisibility(View.INVISIBLE);
            }
        }));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }
}