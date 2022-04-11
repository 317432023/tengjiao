package com.tengjiao.tool.third.code;

import com.swetake.util.Qrcode;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public final class QrCodeTool {
  public static void encode(String content, HttpServletResponse response) {

    // img/jpeg
    response.setContentType("image/png");

    try {
      BufferedImage bufImg = draw(content);

      // 生成二维码QRCode图片
      ImageIO.write(bufImg, "png", response.getOutputStream());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void encode(String content, File file) {
    try {

      BufferedImage bufImg = draw(content);

      // 生成二维码QRCode图片
      ImageIO.write(bufImg, "png", file);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static BufferedImage draw(String content) throws UnsupportedEncodingException {
    Qrcode qrcode = new Qrcode();
    // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
    qrcode.setQrcodeErrorCorrect('M');
    qrcode.setQrcodeEncodeMode('B');
    // 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
    qrcode.setQrcodeVersion(7);

    byte[] contentBytes = content.getBytes("UTF-8");

    BufferedImage bufImg = new BufferedImage(139, 139, BufferedImage.TYPE_INT_RGB);

    Graphics2D gs = bufImg.createGraphics();

    gs.setBackground(Color.WHITE);
    gs.clearRect(0, 0, 200, 200);

    // 设定图像颜色：BLACK
    gs.setColor(Color.BLACK);

    // 设置偏移量 不设置肯能导致解析出错
    int pixoff = 2;
    // 输出内容：二维码
    if (contentBytes.length > 0 && contentBytes.length < 124) {
      boolean[][] codeOut = qrcode.calQrcode(contentBytes);
      for (int i = 0; i < codeOut.length; i++) {
        for (int j = 0; j < codeOut.length; j++) {
          if (codeOut[j][i]) {
            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
          }
        }
      }
    } else {
      System.err.println("QRCode&nbsp;content&nbsp;bytes&nbsp;length&nbsp;=&nbsp;" + contentBytes.length + "&nbsp;not&nbsp;in&nbsp;[&nbsp;0,120&nbsp;].&nbsp;");
    }

    gs.dispose();
    bufImg.flush();

    return bufImg;
  }

  public static String decode(File file) {
    QRCodeDecoder decoder = new QRCodeDecoder();

    BufferedImage image = null;

    try {
      image = ImageIO.read(file);

      String decodedData = new String(decoder.decode(new J2SEImage(image)), "UTF-8");

      return decodedData;
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    } catch (DecodingFailedException dfe) {
      System.err.println("Error: " + dfe.getMessage());
    }
    return null;
  }

  public static String decode(URL url) {
    QRCodeDecoder decoder = new QRCodeDecoder();

    BufferedImage image = null;

    try {
      image = ImageIO.read(url);

      String decodedData = new String(decoder.decode(new J2SEImage(image)), "UTF-8");

      return decodedData;
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    } catch (DecodingFailedException dfe) {
      System.err.println("Error: " + dfe.getMessage());
    }
    return null;
  }

  final static class J2SEImage implements QRCodeImage {
    BufferedImage image;

    public J2SEImage(BufferedImage image) {
      this.image = image;
    }

    @Override
    public int getWidth() {
      return image.getWidth();
    }

    @Override
    public int getHeight() {
      return image.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
      return image.getRGB(x, y);
    }

  }
}
