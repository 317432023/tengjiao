package com.tengjiao.tool.third.test.http;

import com.tengjiao.tool.third.http.HttpClientTool;
import org.apache.http.client.ClientProtocolException;
import org.junit.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class HttpClientToolTest {

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
    public void testDoGetStringMapOfStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void doGetForStreamTest() throws ClientProtocolException, URISyntaxException, IOException{
        HttpClientTool util = HttpClientTool.getInstance();
        //InputStream in = util.doGet("https://kyfw.12306.cn/otn/leftTicket/init");
        InputStream in = util.doGetForStream("http://www.163.com");
        String retVal = HttpClientTool.readStream(in, "gbk");
        System.out.println(retVal);
    }
    @Test
    public void doPostForStringTest() throws ClientProtocolException, URISyntaxException, IOException{
        HttpClientTool util = HttpClientTool.getInstance();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("loTyp", "1");
        String s = util.doPostForString("http://localhost/lotto-api/open/web/curper", queryParams, null);
        System.out.println(s);
    }
}
