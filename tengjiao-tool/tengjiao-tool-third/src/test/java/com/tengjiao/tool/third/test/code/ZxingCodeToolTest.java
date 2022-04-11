package com.tengjiao.tool.third.test.code;

import com.tengjiao.tool.third.code.ZxingCodeTool;
import org.junit.*;

import java.io.File;

public class ZxingCodeToolTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEncodeQRCodeImage() {
        String text = "二维码名片：\n姓名：王稀\nQQ：851372808"
          + "\n电话：18995699027\n邮箱：qqicq2001@163.com\n博客：http://blog.csdn.net/qqicq2001";
        String imagePath = "C:/Users/Administrator/Desktop/二维码.png";
        String logoPath = "C:/Users/Administrator/Desktop/logo.png";
        ZxingCodeTool.encodeQRCodeImage(text, null, imagePath, 800, 800, logoPath);

        /* Using in HttpServlet
        BufferedImage image = ImageIO.read(new File(imagePath));
        ImageIO.write(image, "JPEG", response.getOutputStream());
         */
    }


    @Test
    public void testDecodeQRCodeImage() {
        String imagePath = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1567971889935&di=6b5bc411089e8aa9f292da7115ec3634&imgtype=0&src=http%3A%2F%2Fwechat.shwilling.com%2Fuploads%2Fueditor%2Fimage%2F20150921%2F1442829890828990.png";
        String text = ZxingCodeTool.decodeQRCodeImage(imagePath, null);
        System.out.println(text);
    }

    public static void main(String[] args) {
        System.out.println(new File("C:/Users/Administrator/Desktop/二维码.png").toPath());
    }
}
