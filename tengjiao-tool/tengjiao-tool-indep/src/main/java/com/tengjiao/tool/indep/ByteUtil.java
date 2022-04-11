package com.tengjiao.tool.indep;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

/**
 * 字节数组工具类
 * @author
 */
public final class ByteUtil {
    /**
     * 字节数组转十六进制字符串.<br>
     * Converts a byte array into a hexadecimal string.
     * @param array the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    public static String bytesToHex(byte[] array) {
        // 方法一
        /*
        if (array == null || array.length <= 0) {
          return null;
        }
        
        StringBuilder strBud = new StringBuilder(100);
        for (int i = 0; i < array.length; i++) {
          // 之所以用byte和0xff相与，是因为int是32位，与0xff相与后就舍弃前面的24位，只保留后8位
          String str = Integer.toHexString(array[i] & 0xFF);
          if (str.length() < 2) { // 不足两位要补0
              strBud.append(0);
          }
          strBud.append(str);
        }
        return strBud.toString();
        */
        
        // 方法二
        /*
        if (src == null || src.length <= 0) {
          return null;
        }
        
        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int i = 0, j = 0; i < src.length; i++) {
          res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
          res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }
        
        return new String(res);
        */

        // 方法三
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    /**
     * 十六进制字符串转字节数组
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex) {
        if ((hex == null) || (hex.equals(""))) {
            return null;
        }

        int length = hex.length() / 2;
        char[] hexChars = hex.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            int h = hexDigits.indexOf(hexChars[pos]) << 4;
            int l = hexDigits.indexOf(hexChars[(pos + 1)]);
            if ((h == -1) || (l == -1)) {
                return null;
            }
            bytes[i] = ((byte)(h | l));
        }
        return bytes;
    }

    /**
     * 转 int 为 字节数组<br>
     * @param value
     * @param inverse 是否反转 false(默认) - 低位在前，高位在后；true- 高位在前，低位在后
     * @return
     */
    public static byte[] intToBytes(int value, boolean inverse)
    {
        // 1个 int 有 4 个字节，每个字节8位，4*8 = 32 位
        return makeBytes(4, value, inverse);
    }

    /**
     * 转 long 为 字节数组<br>
     * @param value
     * @param inverse 是否反转 false(默认) - 低位在前，高位在后；true- 高位在前，低位在后
     * @return
     */
    public static byte[] longToBytes(long value, boolean inverse)
    {
        // 1个 long 有 8 个字节，每个字节8位，刚好 8*8 = 64 位
        return makeBytes(8, value, inverse);
    }

    private static byte[] makeBytes(int bLen, long value, boolean inverse) {
        byte[] bytes = new byte[bLen];
        if(!inverse) {
            for(int i = 0; i < bLen; i++) {
                bytes[i] = ((byte)(value >> (i*8) & 0xFF));
            }
        } else {
            for(int i = 0; i < bLen; i++) {
                bytes[i] = ((byte)(value >> ( (bLen-(i+1))*8) & 0xFF));
            }
        }
        return bytes;
    }

    /**
     * 转字节数组为 int
     * @param bytes
     * @param offset 从字节数组某个下标开始转
     * @param inverse 是否反转 false(默认) - 低位在前，高位在后；true- 高位在前，低位在后
     * @return
     */
    public static int bytesToInt(byte[] bytes, int offset, boolean inverse)
    {
        if(!inverse) {
            return bytes[offset] & 0xFF |
                    (bytes[(offset + 1)] & 0xFF) << 8 |
                    (bytes[(offset + 2)] & 0xFF) << 16 |
                    (bytes[(offset + 3)] & 0xFF) << 24;
        } else {
            return (bytes[offset] & 0xFF) << 24 |
                    (bytes[(offset + 1)] & 0xFF) << 16 |
                    (bytes[(offset + 2)] & 0xFF) << 8 |
                    bytes[(offset + 3)] & 0xFF;
        }
    }

    /**
     * 合并两个字节数组<br>
     *     使用 System.arrayCopy 该方法是一个 native 方法，线程不安全，并发操作情况下操作共享数据需要加锁<br>
     *         特别是 data1 和 data2 是共享变量的情况而且又有多线程并发操作的情况下要加锁
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] merge(byte[] data1, byte[] data2)
    {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    /**
     * 截取byte数组   不改变原数组<br>
     *     使用 System.arrayCopy 该方法是一个 native 方法，线程不安全，并发操作情况下操作共享数据需要加锁<br>
     * @param b 原数组
     * @param off 偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length){
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }
    /**
     * GBK/2 转 Unicode 编码并输出<br>
     * GBK 亦采用双字节表示，总体编码范围为 8140-FEFE 之间，首字节在 81-FE 之间，尾字节在 40-FE 之间，剔除 XX7F 一条线。
     * 汉字区 包括
     * GBK/2：OXBOA1-F7FE, 收录 GB2312 汉字 6763 个，按原序排列；
     * GBK/3：OX8140-AOFE，收录 CJK 汉字 6080 个；
     * GBK/4：OXAA40-FEAO，收录 CJK 汉字和增补的汉字 8160 个。
     * 图形符号区 包括
     * GBK/1：OXA9FE-A1A1，除 GB2312 的符号外，还增补了其它符号
     * GBK/5：OXA840-A9AO，扩除非汉字区。
     * 用户自定义区
     * 即 GBK 区域中的空白区，用户可以自己定义字符。
     */
    public static void gbk2ToUniCode(String outputFile, int segStart, int segEnd, int charStart, int charEnd) throws Exception {

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

        //int count = 0;

        for (int segIndex = segStart; segIndex <= segEnd; segIndex++) {// 高字节
            for (int charIndex = charStart; charIndex <= charEnd; charIndex++) {// 低字节
                byte[] gbkBytes = new byte[] { (byte) (segIndex), (byte) charIndex };
                byte[] unicodeBytes;
                String str = new String(gbkBytes, "GBK");

                unicodeBytes = str.getBytes("unicode");
                if (unicodeBytes.length == 4) {
                    //count++;
                    String buffer = "";
                    for (int i = 0; i < gbkBytes.length; i++) {
                        buffer += (int) (0x00ff & gbkBytes[i]) + " ";
                    }

                    // unicodeBytes有四个元素，前两个不知是做什么用的，可能与字符串本身的结构有关，接下来的两个字节才是真正的unicode码。但这两个字节是倒序的，要从最后一个字节开始取，之所以这样是与big_endian和little_endian有关的
                    for (int i = 3; i > 1; i--) {
                        buffer += (int) (0x00ff & unicodeBytes[i]) + " ";
                    }
                    buffer += " ";
                    outputStreamWriter.write(buffer);
                    outputStreamWriter.write("\n");

                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        final String ssbs = "00 6A 00 09 00 00 00 23 FD 00 00 00 00 00 00 00 00 00 04 04 B0 00 00 00 00 00 00 00 00 07 D0 00 00 04 4C 00 00 B8 00 00 00 00 00 00 00 00 00 00 00 00 E2 60 D6 B1 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 FC FC";
        byte[] bs = hexToBytes(ssbs.replaceAll(" ", "").toLowerCase());
        String s = bytesToHex(bs);
        System.out.println(s);
        System.out.println(new String(bs, "GBK"));

        gbk2ToUniCode("unicode2.txt", 0xb0, 0xf7, 0xa1, 0xfe);
        gbk2ToUniCode("unicode3.txt", 0x81, 0xa0, 0xa1, 0xfe);
        gbk2ToUniCode("unicode4.txt", 0xaa, 0xfe, 0x40, 0xa0);
        gbk2ToUniCode("unicode1.txt", 0xa9, 0xa1, 0xa1, 0xfe);
        gbk2ToUniCode("unicode5.txt", 0xa8, 0xa9, 0x40, 0xa0);

    }
}
