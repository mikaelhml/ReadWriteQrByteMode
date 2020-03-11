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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import iesb.byteMode.util.URIUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    Button btnScanQRCode, btnCriarQRCode;
    Spinner spinCorrecao,spinNiveis;
    ArrayAdapter adapterCorrecao,adapterNiveis;
    EditText edtVelocidadeReproducao;
    static final int[] CAPACITY_QR_CODE =



//      M1 M2 M3 M4  1    2   3  4    5    6    7    8    9   10    11  12   13   14   15   16   17   18   19   20   21   22    23    24    25     26    27    28     29    30    31    32    33    34    35    36     37    38    39    40

    {

        0, 0, 9, 15, 17, 32, 53, 78, 106, 134, 154, 192, 230, 271, 321, 367, 425, 458, 520, 586, 644, 718, 792, 858, 929, 1003, 1091, 1171, 1273, 1367, 1465,  1528, 1628, 1732, 1840, 1952, 2068, 2188, 2303, 2431,  2563, 2699, 2809, 2953,     // L

        0, 0, 7, 13, 14, 26, 42, 62, 84,  106, 122, 152, 180, 213, 251, 287, 331, 362, 412, 450, 504, 560, 624, 666, 711,  779,  857,  911,  997, 1059, 1125,  1190, 1264, 1370, 1452, 1538, 1628, 1722, 1809, 1911,  1989, 2099, 2213, 2331,     // M

        0, 0, 0, 9,  11, 20, 32, 46, 60,  74,  86,  108, 130, 151, 177, 203, 241, 258, 292, 322, 364, 394, 442, 482, 509,  565,  611,  661,  715,  751,  805,   868,  908,  982, 1030, 1112, 1168, 1228, 1283, 1351,  1423, 1499, 1579, 1663,     // Q

        0, 0, 0, 0,  7,  14, 24, 34, 44,  58,  64,  84,  98,  119, 137, 155, 177, 194, 220, 250, 280, 310, 338, 382, 403,  439,  461,  511,  535,  593,  625,   658,  698,  742,  790,  842,  898,  958,  983, 1051,  1093, 1139, 1219, 1273      // H

    };

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
        spinCorrecao = (Spinner) findViewById(R.id.spinCorrecao);
        spinNiveis = (Spinner) findViewById(R.id.spinNiveis);
        edtVelocidadeReproducao = findViewById(R.id.edtVelocidadeReproducao);
        adapterCorrecao = ArrayAdapter.createFromResource(this,R.array.qr_correcao,android.R.layout.simple_dropdown_item_1line);
        adapterNiveis = ArrayAdapter.createFromResource(this,R.array.qr_niveis,android.R.layout.simple_dropdown_item_1line);
        spinCorrecao.setAdapter(adapterCorrecao);
        spinNiveis.setAdapter(adapterNiveis);

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

                int capacidadeQR = calcularCapacidadeQR();
                Intent i = new Intent(MainActivity.this, GerarQrs.class);
                i.putExtra("arquivo", arquivoCodificado);
                i.putExtra("nomeArquivo", nomeArquivo);
                i.putExtra("capacidadeQR",capacidadeQR);
                i.putExtra("nivelCorrecao",spinCorrecao.getSelectedItem().toString());
                i.putExtra("velocidadeReproducao", edtVelocidadeReproducao.getText().toString());
                startActivity(i);
                finish();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int calcularCapacidadeQR() {
        String nivelCorrecao =  spinCorrecao.getSelectedItem().toString();
        int nivelQRCode = Integer.parseInt(spinNiveis.getSelectedItem().toString());
        int valorCorrecao = 0;
        if(nivelCorrecao.equalsIgnoreCase("L")){
            valorCorrecao = 3;
        }
        else if(nivelCorrecao.equalsIgnoreCase("M")){
            valorCorrecao = 47;
        }
        else if (nivelCorrecao.equalsIgnoreCase("Q")) {
            valorCorrecao = 91;
        }
        else if (nivelCorrecao.equalsIgnoreCase("H")){
            valorCorrecao = 135;
        }

        return CAPACITY_QR_CODE[nivelQRCode + valorCorrecao];
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
