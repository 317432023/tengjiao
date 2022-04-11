package com.tengjiao.tool.indep.test;

import com.tengjiao.tool.indep.FileTool;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FileToolTest {

  @Test
  public void testReadToString() {
    fail("Not yet implemented");
  }

  @Test
  public void testReadLastLine() {
    String lastLine = FileTool.readLastLine("test.txt", "utf-8");
    System.out.println(lastLine);
  }

  @Test
  public void testReadLastLine2() {
    String lastLine = FileTool.readLastLine2("test.txt");
    System.out.println(lastLine);
  }

  @Test
  public void testAppendLine() {
    FileTool.appendLine("test.txt", "你好");
  }

  @Test
  public void testAppendLine2() {
    FileTool.appendLine2("test.txt", "你好");
    
  }

}
