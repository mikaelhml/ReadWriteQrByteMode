package iesb.byteMode.model;

import java.util.ArrayList;

import static iesb.byteMode.constants.Contantes.CAPACIDADEQR;


public class ArquivoString {
    private ArrayList<String> arrayString = new ArrayList<>();
    private int quantidadeQRGerado;

    public ArquivoString() {
    }

    public ArquivoString(String arquivo, String arquivoNome) {
        dividirString(arquivo, arquivoNome);
    }

    private void dividirString(String arquivo, String arquivoNome) {
        String tamanhoNomeArquivo = "00" + arquivoNome.length();
        tamanhoNomeArquivo = tamanhoNomeArquivo.substring(tamanhoNomeArquivo.length() - 3);
        arquivo = arquivoNome + arquivo;
        arquivo = tamanhoNomeArquivo + arquivo;
        quantidadeQRGerado = arquivo.length() / CAPACIDADEQR;
        quantidadeQRGerado += ((arquivo.length() % CAPACIDADEQR > 0) ? 1 : 0);
        String x;
        String y;
        int contador = 0;
        for (int i = 0; i < quantidadeQRGerado; i++) {
            x = "00" + i;
            x = x.substring(x.length() - 3);

            y = "00" + quantidadeQRGerado;
            y = y.substring(y.length() - 3);

            if (contador + CAPACIDADEQR > arquivo.length()) {
                arrayString.add(x + y + arquivo.substring(contador));
            } else {
                arrayString.add(x + y + arquivo.substring(contador, contador + CAPACIDADEQR));
                contador += CAPACIDADEQR;
            }
        }

    }

    public ArrayList<String> getArrayString() {
        return arrayString;
    }


}
