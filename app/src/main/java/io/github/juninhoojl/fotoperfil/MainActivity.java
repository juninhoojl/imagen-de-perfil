package io.github.juninhoojl.fotoperfil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    ImageView imageViewFoto;
    String caminho = "";
    TextView mostratexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        imageViewFoto = findViewById(R.id.imageView);
        mostratexto = findViewById(R.id.textView);


        // Logo aqui verifica

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "mi_imagen.jpg" );
        if (file.exists()) {
            //Do action
            imageViewFoto.setImageURI(Uri.parse(file.getAbsolutePath()));

            mostratexto.setText("EXISTE"+file.getAbsolutePath());

        }else{

            int imageResource = getResources().getIdentifier("@drawable/foto_perfil", null, this.getPackageName());
            imageViewFoto.setImageResource(imageResource);
            mostratexto.setText("naoEXISTE"+file.getAbsolutePath());
        }


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tirarFoto();
            }

        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }

        });

    }


    public void tirarFoto(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Capturamos a imagem
        // Como vamos solicitar uma intent e vamos aguardar um resultado entao tambem precisamos
        // verificar esse resultado quando chegar esse resultado usamos por exmeplo request code 1
        startActivityForResult(intent,1);

    }

    private void pickFromGallery(){

        // Cria tentativa de selecionar imagem
        Intent intent=new Intent(Intent.ACTION_PICK);

        // Tipo imagem que pode ser selecionado
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};

        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);

        startActivityForResult(intent,2);

    }

    public void salvarFoto(){

        imageViewFoto.buildDrawingCache();
        Bitmap bitmap = imageViewFoto.getDrawingCache();

        OutputStream fileOutStream = null;
        Uri uri;

        try {

            File storageFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            File directorioImagenes = new File(storageFile, "mi_imagen.jpg");
            caminho = directorioImagenes.getAbsolutePath();

            mostratexto.setText(caminho);
            uri = Uri.fromFile(directorioImagenes);

            fileOutStream = new FileOutputStream(directorioImagenes);
        } catch (Exception e) {
            mostratexto.setText("ERRO1");
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream);
            fileOutStream.flush();
            fileOutStream.close();
        } catch (Exception e) {
            mostratexto.setText("ERRO2");
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK) { // se for 1 eh o que queremos e result ok ( usuario conseguiu tirar e confirmou a foto em questao)
            // entao podemos recuperar a imagem a partir da intent

            // aqui temos o extra ja
            Bundle extras = data.getExtras();
            // ela chega no tipo bitmap
            Bitmap imagem = (Bitmap) extras.get("data"); // que é a chave que representa minha imagem

            imageViewFoto.setImageBitmap(imagem);
            salvarFoto();

        }else if(requestCode ==2 && resultCode == RESULT_OK){

        Uri selectedImage = data.getData();
        imageViewFoto.setImageURI(selectedImage);

        salvarFoto();
    }


    }

}
