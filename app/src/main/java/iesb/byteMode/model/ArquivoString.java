package iesb.byteMode.model;

import java.util.ArrayList;



public class ArquivoString {
    private ArrayList<String> arrayString = new ArrayList<>();
    private int quantidadeQRGerado;

    public ArquivoString() {
    }

    public ArquivoString(String arquivo, String arquivoNome,int capacidadeQR) {
        dividirString(arquivo, arquivoNome,capacidadeQR-2);
    }

    private void dividirString(String arquivo, String arquivoNome, int capacidadeQR) {
        String tamanhoNomeArquivo = "00" + arquivoNome.length();
        tamanhoNomeArquivo = tamanhoNomeArquivo.substring(tamanhoNomeArquivo.length() - 3);
        byte tamanhoNomeArquivoByte = (byte)Integer.parseInt(tamanhoNomeArquivo);
        arquivo = arquivoNome + arquivo;
        arquivo = (char)tamanhoNomeArquivoByte + arquivo;
        quantidadeQRGerado = arquivo.length() / capacidadeQR;
        quantidadeQRGerado += ((arquivo.length() % capacidadeQR > 0) ? 1 : 0);
        String x;
        String y;
        String arquivoTemp;
        int contador = 0;
        for (int i = 0; i < quantidadeQRGerado; i++) {
            x = "00" + i;
            x = x.substring(x.length() - 3);
            byte tamanhoXByte = (byte)Integer.parseInt(x);



            y = "00" + quantidadeQRGerado;
            y = y.substring(y.length() - 3);
            byte tamanhoYByte = (byte)Integer.parseInt(y);


            if (contador + capacidadeQR > arquivo.length()) {
                arquivoTemp = (char)tamanhoYByte + arquivo.substring(contador);
                arrayString.add((char)tamanhoXByte + arquivoTemp);
            } else {
                arquivoTemp = (char)tamanhoYByte + arquivo.substring(contador, contador + capacidadeQR);
                arrayString.add((char)tamanhoXByte + arquivoTemp);
                contador += capacidadeQR;
            }
        }

    }

    public ArrayList<String> getArrayString() {
        return arrayString;
    }


}
