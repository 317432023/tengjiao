package com.tengjiao.tool.third.test.json;

import com.tengjiao.tool.third.json.JackJsonTool;
import org.junit.*;

import static org.junit.Assert.fail;

public class JackJsonToolTest {

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
  public void testJsonToPojo() {
    Object o = JackJsonTool.parse("{\"a\":1}", Object.class);
    System.out.println(o);
  }

  @Test
  public void testLib() {
    System.out.println(JackJsonTool.toJson(new java.util.Date()));
    java.util.Date now = JackJsonTool.parse("2019-07-31 17:17:20", java.util.Date.class);
    System.out.println(now);
  }

}
