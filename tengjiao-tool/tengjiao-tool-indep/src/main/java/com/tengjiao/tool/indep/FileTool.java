package com.tengjiao.tool.indep;

import java.io.*;
import java.util.Scanner;

public final class FileTool {

  /**
   * 一次性读取文件内容
   * @param fileName 文件名
   * @return
   */
  public static String readToString(String fileName, String encoding) {

    File file = new File(fileName);
    Long filelength = file.length();
    byte[] fileContent = new byte[filelength.intValue()];
    try {
      FileInputStream in = new FileInputStream(file);
      in.read(fileContent);
      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      return new String(fileContent, encoding);
    } catch (UnsupportedEncodingException e) {
      System.err.println("The OS does not support " + encoding);
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * 读取文件最后一行（速度较快）
   * @param fileName
   * @param fileEnc 文件编码 UTF-8, GBK, ISO-8859-1
   * @return
   */
  public static String readLastLine(String fileName, String fileEnc) {
    RandomAccessFile rf = null;
    String line = null;
    try {
      rf = new RandomAccessFile(fileName, "r");
      long len = rf.length();
      long start = rf.getFilePointer();
      //System.out.println(start);
      long nextend = start + len - 1;
      rf.seek(nextend);
      int c = -1;
      while (nextend > start) {
        c = rf.read();
        if (c == '\n' || c == '\r') {
          line = rf.readLine();
          if(line!=null && !"null".equals(line)) {
            line = new String(line.getBytes("ISO-8859-1"), fileEnc);
            return line;
          }
          //System.out.println(line);
          nextend--;
        }
        nextend--;
        rf.seek(nextend);
        if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
          //System.out.println(rf.readLine());
//          line = rf.readLine();
//          if(line!=null) {
//            line = new String(line.getBytes("ISO-8859-1"), fileEnc);
//          }
//          System.out.println(line);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (rf != null)
          rf.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return line;
  }
  
  /**
   * 读取文件最后一行
   * @param fileName
   * @return
   */
  public static String readLastLine2(String fileName) {
    FileReader fileReader = null;
    try {
      fileReader = new FileReader(fileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Scanner sc = new Scanner(fileReader);
    String line = null;
    while((sc.hasNextLine() && (line=sc.nextLine())!=null)){
      if(!sc.hasNextLine()){
        //System.out.println(line);
        break;
      }
    }
    sc.close();
    return line;
  }
  /**
   * 文件的结尾追加记录(速度较快) 
   * @param fileName
   * @param line
   */
  public static void appendLine(String fileName, String line) {
    try {
      RandomAccessFile rf = new RandomAccessFile(fileName, "rw");
      long count = rf.length();
      rf.seek(count);
      
      //rf.writeBytes(line+"/r/n"); //只是写入字符的时候不会乱码，如果需要写入汉字，需要用下面这一行的方法
      
      String newline = new String(line.getBytes(), "ISO_8859-1"); 
      rf.writeBytes(newline + "\r\n");
      
      rf.close();

    } catch (Exception e) {
      System.err.println("读写出错");
    }
  }
  /**
   * 文件的结尾追加记录
   * @param fileName
   * @param line
   */
  public static void appendLine2(String fileName, String line) {
    try {
      // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
      FileWriter writer = new FileWriter(fileName, true);
      writer.write(line + "\r\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
