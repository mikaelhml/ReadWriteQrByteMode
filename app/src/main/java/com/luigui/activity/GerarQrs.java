package com.luigui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.luigui.binary.BinaryQRCodeWriter;
import com.luigui.model.ArquivoString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressLint("Registered")
public class GerarQrs extends AppCompatActivity {
    private ImageView imgQr;
    private ArquivoString arquivoString;
    private Button btnAvancar, btnAnterior, btnVoltar;
    private TextView textContadorQR;
    private ArrayList<Bitmap> listaQR = new ArrayList<>();
    private int posicao=-1;
    private int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerar_qr);
        iniciaVariaveis();
        eventoClicks();
        init();
    }

    private void init() {
        for (String g : arquivoString.getArrayString()) {

            gerarQR(g);
            //gerarQR(recuperarArrayByte(g));
        }
        apresentarVideoQR(listaQR);
    }

    private void apresentarVideoQR(final ArrayList<Bitmap> listaQR) {

        new CountDownTimer(listaQR.size()*1000*10, 900) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here
            }

            public void onTick(long millisUntilFinished) {
                avancaPosicao();
            }
        }.start();

    }

    private byte[] recuperarArrayByte(String g) {
        byte[] bytesRecuperados = new byte[g.length()];

        for(int i=0;i<g.length();i++){
            bytesRecuperados[i] = (byte)g.charAt(i);
        }
        return bytesRecuperados;
    }

    private void gerarQR(String bytes) {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BinaryQRCodeWriter qrCodeWriter1 = new BinaryQRCodeWriter();
        String teste = toHex(bytes);

        int width = 512;
        int height = 512;
        try {
            BitMatrix byteMatrix = qrCodeWriter1.encode(bytes.getBytes(), BarcodeFormat.QR_CODE, width, height);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (!byteMatrix.get(x, y))
                        bmp.setPixel(x, y, Color.WHITE);
                    else
                        bmp.setPixel(x, y, Color.BLACK);
                }
            }
            //imageView.setImageBitmap(bmp);
            listaQR.add(bmp);
        } catch (WriterException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toHex(String arg) {
        return String.format("%02X", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    private void avancaPosicao(){
        if(posicao+1<listaQR.size()){
            imgQr.setImageBitmap(listaQR.get(posicao+1));
            posicao += 1;
            textContadorQR.setText("Atual: "+(posicao+1)+"      De: "+listaQR.size());

        }
        else{
            //Toast.makeText(GerarQrs.this,"Voce já esta no ultimo QR",Toast.LENGTH_SHORT).show();
            posicao=-1;
        }
    }

    private void eventoClicks() {
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GerarQrs.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posicao -1 >= 0){
                    imgQr.setImageBitmap(listaQR.get(posicao-1));
                    posicao -=1;
                    textContadorQR.setText("Atual: "+(posicao+1)+"      De: "+listaQR.size());
                }
                else{
                    Toast.makeText(GerarQrs.this,"Voce já está no primeiro QR",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posicao+1<listaQR.size()){
                    imgQr.setImageBitmap(listaQR.get(posicao+1));
                    posicao += 1;
                    textContadorQR.setText("Atual: "+(posicao+1)+"      De: "+listaQR.size());

                }
                else{
                    //Toast.makeText(GerarQrs.this,"Voce já esta no ultimo QR",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void iniciaVariaveis() {
        imgQr = (ImageView) findViewById(R.id.img_QR);
        arquivoString = new ArquivoString(getIntent().getStringExtra("string64"),getIntent().getStringExtra("nomeArquivo"));
        btnAnterior = (Button) findViewById(R.id.btnAnterior);
        btnAvancar = (Button) findViewById(R.id.btnProximo);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        textContadorQR = (TextView) findViewById(R.id.textContadorQR);

    }


}
