package veca.notepad.com.camerapro_pro;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import veca.notepad.com.camerapro_pro.Fragments.SalirDeAppDialogFragment;

public class MainActivity extends AppCompatActivity {
    ImageView imvPresentador;
    Button  btnGaleria;
    Button btnTomarFoto;
    private ContentValues values;
    private Uri imageUri;
    String imageurl;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //******Enlazando elementos del layout****
        imvPresentador = findViewById(R.id.imvPresentador);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //getContentResolver().delete(imageUri,null,null);
       //Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        File foto = new File(imageurl);
        if(foto.exists()){
           // Toast.makeText(this, "Existe la foto", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            SalirDeAppDialogFragment salirDeAppDialogFragment = new SalirDeAppDialogFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            salirDeAppDialogFragment.show(fragmentManager,"fragmentSalir");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void tomarFoto(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            permisoCamara();
            // Toast.makeText(this, "Permiso de memoria", Toast.LENGTH_SHORT).show();
        }else{
            permisoMemoria();
            //Toast.makeText(this, "1.- Sin permiso de memoria", Toast.LENGTH_SHORT).show();
            Log.e("1.-","Tomar foto");
        }
    }

    private void tomarFotoIntent() {
        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "MyPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //Toast.makeText(this, "Foto tomada", Toast.LENGTH_SHORT).show();
        Log.e("4.-","Preparado para tomar la foto");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try{
               // Bundle extras = data.getExtras();
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imvPresentador.setImageBitmap(imageBitmap);
                imageurl = getRealPathFromURI(imageUri);
            }catch(Exception e){
                Toast.makeText(this, "Error al obtener la imagen " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void permisoMemoria(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    private void permisoCamara() {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    //Toast.makeText(this, "Activa el permiso para tomar una foto y aplicar un filtro.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                   // Log.e("3.-","Permiso de camara");
                    //Toast.makeText(this, "3.- Permiso de camara?", Toast.LENGTH_SHORT).show();
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                //Toast.makeText(this, "Camara tiene permiso", Toast.LENGTH_SHORT).show();
                tomarFotoIntent();

            }
        }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
            switch (requestCode){
                case MY_PERMISSIONS_REQUEST_CAMERA:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Log.e("3.-","Permiso de camara");
                        tomarFotoIntent();

                        //Toast.makeText(this, "4.- Se otorgó permiso a la camara", Toast.LENGTH_SHORT).show();
                    }
                    return;
                case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        permisoCamara();
                        //Toast.makeText(this, "2.- Se otorgó permiso de memoria", Toast.LENGTH_SHORT).show();
                        Log.e("2.-","Permiso de memoria");
                    }
                    return;

        }
    }
}
