package com.tengjiao.tool.third.test.code;

import com.tengjiao.tool.third.code.QrCodeTool;
import org.junit.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class QrCodeToolTest {
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
  public void testEncode() {
    final String content = "李四";
    QrCodeTool.encode(content, new File("c:\\_tmp\\qrcode.png"));
  }
  @Test
  public void testDecodeFile() {
    final String content = QrCodeTool.decode(new File("C:\\Users\\Administrator\\Desktop\\微信图片_20191002214759.jpg"));
    System.out.println(content);
  }
  @Test
  public void testDecodeURL() throws MalformedURLException {
    final String content = QrCodeTool.decode(new URL("file:///c:/_tmp/qrcode.png"));
    System.out.println(content);
  }
}
