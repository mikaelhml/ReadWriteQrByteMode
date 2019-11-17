package iesb.google.zxing.binaryqrcode.binary;

import iesb.google.zxing.BarcodeFormat;
import iesb.google.zxing.BinaryBitmap;
import iesb.google.zxing.ChecksumException;
import iesb.google.zxing.FormatException;
import iesb.google.zxing.NotFoundException;
import iesb.google.zxing.Result;
import iesb.google.zxing.ResultPoint;
import iesb.google.zxing.common.DecoderResult;
import iesb.google.zxing.common.DetectorResult;
import iesb.google.zxing.qrcode.decoder.Decoder;
import iesb.google.zxing.qrcode.detector.Detector;

/**
 * This implementation can detect and decode QR Codes in an image into bytes.
 *
 * @author Thomas Skjolberg
 */
public class BinaryQRCodeReader{

  private final Decoder decoder = new Decoder();

  protected Decoder getDecoder() {
    return decoder;
  }

  /**
   * Locates and decodes a QR code in an image.
   *
   * @return a String representing the content encoded by the QR code
   * @throws NotFoundException if a QR code cannot be found
   * @throws FormatException if a QR code cannot be decoded
   * @throws ChecksumException if error correction fails
   */
  public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
      DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect();
      ResultPoint[] points;

      DecoderResult decode = decoder.decode(detectorResult.getBits());
      points = detectorResult.getPoints();
      Result result = new Result(decode.getText(), decode.getRawBytes(), points, BarcodeFormat.QR_CODE);

      return result;
  }

  public void reset() {
    // do nothing
  }


}
