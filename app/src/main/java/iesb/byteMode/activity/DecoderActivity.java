package iesb.byteMode.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import iesb.byteMode.qrcodereaderview.QRCodeReaderView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import iesb.byteMode.constants.Contantes;

public class DecoderActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

  private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

  private ViewGroup mainLayout;

  private TextView resultTextView;
  private QRCodeReaderView qrCodeReaderView;
  private CheckBox flashlightCheckBox;
  private CheckBox enableDecodingCheckBox;
  private PointsOverlayView pointsOverlayView;
  private String naTela="";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_decoder);

    mainLayout = (ViewGroup) findViewById(R.id.main_layout);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      initQRCodeReaderView();
    } else {
      requestCameraPermission();
    }
  }

  @Override protected void onResume() {
    super.onResume();

    if (qrCodeReaderView != null) {
      qrCodeReaderView.startCamera();
    }
  }

  @Override protected void onPause() {
    super.onPause();

    if (qrCodeReaderView != null) {
      qrCodeReaderView.stopCamera();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
      return;
    }

    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
      initQRCodeReaderView();
    } else {
      Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
          .show();
    }
  }

  // Called when a QR is decoded
  // "text" : the text encoded in QR
  // "points" : points where QR control points are placed
  @Override public void onQRCodeRead(String text, byte[] rawData, PointF[] points) {
    StringBuilder sb = new StringBuilder();
    for (byte b : rawData) {
      sb.append(String.format("%02X ", b));
    }
    String stringResult = sb.toString().replaceAll(" ","");
    byte[] byteResult = hexStringToByteArray(stringResult);
    salvarArquivo(byteResult);
    resultTextView.setText("O arquivo "+naTela+" foi salvo em Download/App/Target");
  }

  private void salvarArquivo(byte[] byteResult) {
    String stringRecuperada = byteArrayToString(byteResult);
    stringRecuperada = stringRecuperada.substring(6);
    int tamanhoNomeArquivo = Integer.parseInt(stringRecuperada.substring(0, 3));
    String nomeArquivo = stringRecuperada.substring(3, tamanhoNomeArquivo + 3);
    naTela = nomeArquivo;
    String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1,nomeArquivo.lastIndexOf('.') + 4);
    stringRecuperada = stringRecuperada.substring(tamanhoNomeArquivo + 3);
    File newDir = new File(Contantes.PATHFILE);
    newDir.mkdirs();
    File file = new File(newDir, nomeArquivo);
    file.getParentFile().mkdirs();
    try {
      file.createNewFile();
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      byte[] bytes = recuperarArrayByte(stringRecuperada);
      bos.write(bytes);
      bos.flush();
      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
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

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }
  private byte[] recuperarArrayByte(String g) {
    byte[] bytesRecuperados = new byte[g.length()];

    for (int i = 0; i < g.length(); i++) {
      bytesRecuperados[i] = (byte) g.charAt(i) ;
    }
    return bytesRecuperados;
  }

  private void requestCameraPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
      Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
          Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
        @Override public void onClick(View view) {
          ActivityCompat.requestPermissions(DecoderActivity.this, new String[] {
              Manifest.permission.CAMERA
          }, MY_PERMISSION_REQUEST_CAMERA);
        }
      }).show();
    } else {
      Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
          Snackbar.LENGTH_SHORT).show();
      ActivityCompat.requestPermissions(this, new String[] {
          Manifest.permission.CAMERA
      }, MY_PERMISSION_REQUEST_CAMERA);
    }
  }

  private void initQRCodeReaderView() {
    View content = getLayoutInflater().inflate(R.layout.content_decoder, mainLayout, true);

    qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
    resultTextView = (TextView) content.findViewById(R.id.result_text_view);
    flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
    enableDecodingCheckBox = (CheckBox) content.findViewById(R.id.enable_decoding_checkbox);
    pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);

    qrCodeReaderView.setAutofocusInterval(2000L);
    qrCodeReaderView.setOnQRCodeReadListener(this);
    qrCodeReaderView.setBackCamera();
    flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        qrCodeReaderView.setTorchEnabled(isChecked);
      }
    });
    enableDecodingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        qrCodeReaderView.setQRDecodingEnabled(isChecked);
      }
    });
    qrCodeReaderView.startCamera();
  }
}