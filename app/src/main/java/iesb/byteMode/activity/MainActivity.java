package iesb.byteMode.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import iesb.byteMode.util.URIUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    Button btnScanQRCode, btnCriarQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciaVariaveis();
        eventoClicks();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void eventoClicks() {
        btnCriarQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarQRCode();
            }
        });

        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iniciaScan();
            }
        });
    }

    private void iniciaScan() {
        Intent i = new Intent(MainActivity.this, DecoderActivity.class);
        startActivity(i);
    }


    private void criarQRCode() {
        carregarImg();
    }

    private void carregarImg() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111);

    }

    private void iniciaVariaveis() {
        btnCriarQRCode = (Button) findViewById(R.id.btnCriarQRCode);
        btnScanQRCode = (Button) findViewById(R.id.btnScanQRCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("StaticFieldLeak")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 111) && (resultCode == RESULT_OK)) {
            Uri uri = data.getData();
            Uri uri2 = Uri.parse(getRealPathFromUri(uri));

            File file = new File(uri2.getPath());
            String arquivoCodificado;

            try {

                String nomeArquivo = getFileName(uri2);
                String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
                int size = (int) file.length();
                byte[] bytes = new byte[size];
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                arquivoCodificado = byteArrayToString(bytes);

                Intent i = new Intent(MainActivity.this, GerarQrs.class);
                i.putExtra("arquivo", arquivoCodificado);
                i.putExtra("nomeArquivo", nomeArquivo);
                startActivity(i);
                finish();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String byteArrayToString(byte[] bytes) {
        StringBuilder retorno = new StringBuilder();
        char c;
        for (byte b : bytes) {
            c = (char) b;
            retorno.append(c);

        }
        return retorno.toString();
    }

    public String getRealPathFromUri(Uri contentUri) {
        return URIUtil.getPath(MainActivity.this, contentUri);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
