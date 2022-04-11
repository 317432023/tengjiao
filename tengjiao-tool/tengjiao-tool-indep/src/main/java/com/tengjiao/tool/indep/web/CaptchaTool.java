package com.tengjiao.tool.indep.web;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

/**
 * 生成验证码图片<br>
 * Usage:<br>
 * // 服务端<br>
 * // 设置禁止缓存
 * response.setHeader("Pragma", "No-cache");<br>
 * response.setHeader("Cache-Control", "no-cache");<br>
 * response.setDateHeader("Expires", 0);<br>
 * response.setContentType("image/jpeg");<br>
 * <p>
 * String sRand = generateCaptcha(size, sources);<br>
 * <p>
 * // 法1：验证码存储到会话中<br>
 * HttpSession session = request.getSession(true);<br>
 * session.removeAttribute("rand");<br>
 * session.setAttribute("rand", sRand);<br>
 * <p>
 * // 法2：验证码存储到redis缓存中<br>
 * String captchaToken = UUID.randomUUID().toString();<br>
 * redisStringTemplate.set("captcha_token:" + captchaToken, sRand, 300, TimeUnit.SECONDS);
 * CookieTool.setCookie(request, response, "captcha_token", captchaToken);
 * <p>
 * CaptchaTool.outputImage(width, height, outputStream, sRand)<br>
 *
 * @author Administrator
 */
public class CaptchaTool {

    /**
     * 默认字符源
     */
    private static final String VERIFY_CODES = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private static final Random random = new Random();

    /**
     * 验证码宽高(单位像素)
     */
    public static final int W = 200, H = 80;

    /**
     * 计算最终的合理的宽高
     *
     * @param width
     * @param height
     * @return
     */
    public static int[] getWh(Integer width, Integer height) {

        // 宽高比例
        float whRatio = W / H;

        if (width != null && height != null) {
            if (width > 0 && height > 0) {
                return new int[]{width, height};
            }
            if (width > 0) {
                return new int[]{width, (int) (width / whRatio)};
            }
            if (height > 0) {
                return new int[]{(int) (height * whRatio), height};
            }
            return new int[]{W, H};
        }

        if (width != null) {
            return width > 0 ? new int[]{width, (int) (width / whRatio)} : new int[]{W, H};
        }
        if (height != null) {
            return height > 0 ? new int[]{(int) (height * whRatio), height} : new int[]{W, H};
        }
        return new int[]{W, H};
    }

    /**
     * 使用系统默认字符源生成验证码
     *
     * @param captchaSize 验证码长度
     * @return
     */
    public static String generateCaptcha(int captchaSize) {
        return generateCaptcha(captchaSize, VERIFY_CODES);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param captchaSize 验证码长度
     * @param sources     验证码字符源
     * @return
     */
    public static String generateCaptcha(int captchaSize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codesLen = sources.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder captchaCode = new StringBuilder(captchaSize);
        for (int i = 0; i < captchaSize; i++) {
            captchaCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
        }
        return captchaCode.toString();
    }

    /**
     * 生成随机验证码文件,并返回验证码值
     *
     * @param w
     * @param h
     * @param outputFile
     * @param captchaSize
     * @param sources
     * @return
     * @throws IOException
     */
    public static String outputCaptchaImage(int w, int h, File outputFile, int captchaSize, String sources) throws IOException {
        String captchaCode = generateCaptcha(captchaSize, sources);
        outputImage(w, h, outputFile, captchaCode);
        return captchaCode;
    }

    /**
     * 输出随机验证码图片流,并返回验证码值
     *
     * @param w
     * @param h
     * @param os
     * @param captchaSize
     * @return
     * @throws IOException
     */
    public static String outputCaptchaImage(int w, int h, OutputStream os, int captchaSize, String sources) throws IOException {
        String captchaCode = generateCaptcha(captchaSize, sources);
        outputImage(w, h, os, captchaCode);
        return captchaCode;
    }

    /**
     * 转验证码为字节数组
     *
     * @param width
     * @param height
     * @param code
     * @return
     * @throws IOException
     */
    public static byte[] convertCodeToBytes(int width, int height, String code) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CaptchaTool.outputImage(width, height, baos, code);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }

    /**
     * 生成指定验证码图像文件
     *
     * @param w
     * @param h
     * @param outputFile
     * @param code
     * @throws IOException
     */
    public static void outputImage(int w, int h, File outputFile, String code) throws IOException {
        if (outputFile == null) {
            return;
        }
        File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            outputImage(w, h, fos, code);
            fos.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 输出指定验证码图片流
     *
     * @param w    宽度
     * @param h    高度
     * @param os   输出流
     * @param code 验证码
     * @throws IOException
     */
    public static void outputImage(int w, int h, OutputStream os, String code) throws IOException {

        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // 创建用于绘制二维图形的Graphic2D对象
        Graphics2D g2 = image.createGraphics();
        // 去除锯齿状，可以得到细腻的图形（平滑处理）
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 画笔固定颜色【灰色】填充大背景
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, w, h);
        // 画笔随机颜色填充验证码主区域背景
        Color c = getRandColor(200, 250);
        g2.setColor(c);
        g2.fillRect(0, 2, w, h - 4);

        // 画笔随机颜色绘制干扰线20条
        g2.setColor(getRandColor(160, 200));
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        // 使边框扭曲
        shear(g2, w, h, c);

        // <!-- begin 画验证码

        // 画笔颜色随机 使用到Algerian字体，系统里没有的话需要安装字体，字体只显示大写
        g2.setColor(getRandColor(100, 160));
        int fontSize = h - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);

        Random rand = new Random();
        int captchaSize = code.length();
        char[] chars = code.toCharArray();
        for (int i = 0; i < captchaSize; i++) {

            // 保形变换同时旋转随机角度【 平移（Translation）、缩放（Scale）、翻转（Flip）、旋转（Rotation）和剪切（Shear）】
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (w / captchaSize) * i + fontSize / 2, h / 2);
            g2.setTransform(affine);

            // 画验证码字符
            g2.drawChars(chars, i, 1, ((w - 10) / captchaSize) * i + 5, h / 2 + fontSize / 2 - 10);
        }
        // end -->

        // 销毁图形资源
        g2.dispose();

        // 图像写入输出流
        ImageIO.write(image, "png", os);// jpg->image/jpeg; png->image/png
    }

    /**
     * 计算随机颜色
     *
     * @param fc 颜色基数
     * @param bc 用于计算随机偏差值
     * @return
     */
    private static Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 随机颜色
     * @return
     */
    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    /**
     * 随机颜色
     * @return
     */
    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    /**
     * 使边框扭曲
     *
     * @param g
     * @param w1
     * @param h1
     * @param color
     */
    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }
    }

}