package com.tengjiao.seed.admin.sample;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCryptPasswordEncoderTest
 * @author administrator
 * @version 2020/11/18 22:38
 */
public class BCryptPasswordEncoderTest {
  public static void main(String[] args) {
    // 创建用户时设置的密码
    String password = "123456";
    // 进行加密
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String encode = encoder.encode(password);
    String encode1 = encoder.encode(password);
    System.out.println(encode);
    System.out.println(encode1);

    System.out.println("第一次加密密文是否验证通过: " + encoder.matches(password, encode));
    System.out.println("第二次加密密文是否验证通过: " + encoder.matches(password, encode1));

    //

  }
}
