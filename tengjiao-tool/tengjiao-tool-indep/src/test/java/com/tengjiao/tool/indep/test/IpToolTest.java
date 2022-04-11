package com.tengjiao.tool.indep.test;

import com.tengjiao.tool.indep.IpTool;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class IpToolTest {

  /**
   * @param args
   * @throws UnknownHostException
   * @throws SocketException
   */
  public static void main(String[] args) throws UnknownHostException, SocketException {
    String ip = InetAddress.getLocalHost().getHostAddress();
    System.out.println(ip);

    System.out.println(IpTool.getLocalHostLANAddress(false).getHostAddress());
  }

}
