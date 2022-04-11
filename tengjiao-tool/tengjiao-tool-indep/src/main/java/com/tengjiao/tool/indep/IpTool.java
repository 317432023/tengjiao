package com.tengjiao.tool.indep;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * IP工具
 *
 * @author Administrator
 */
public final class IpTool {
  /**
   * 取真实ip地址，只读取WiFi或者有线地址<br>
   * Usage: getLocalHostLANAddress(intranetFirst).getHostAddress()
   * <pre>
   * 127.xxx.xxx.xxx 属于”loopback” 地址，即只能你自己的本机可见，就是本机地址，比较常见的有127.0.0.1； 
   * 192.168.xxx.xxx 属于private 私有地址(site local address)，属于本地组织内部访问，只能在本地局域网可见。同样10.xxx.xxx.xxx、从172.16.xxx.xxx 到 172.31.xxx.xxx都是私有地址，也是属于组织内部访问； 
   * 169.254.xxx.xxx 属于连接本地地址（link local IP），在单独网段可用 
   * 从224.xxx.xxx.xxx 到 239.xxx.xxx.xxx 属于组播地址 
   * 比较特殊的255.255.255.255 属于广播地址 
   * 除此之外的地址就是点对点的可用的公开IPv4地址
   * 也就是说InetAddress.getLocalHost().getHostAddress()的IP不一定是正确的IP
   * </pre>
   * @param intranetFirst 是否优先取内网(intranet)ip ，否则优先取外网(extranet)ip
   * @return
   * @throws UnknownHostException
   */
  public static InetAddress getLocalHostLANAddress(boolean intranetFirst) throws UnknownHostException {
    try {
        InetAddress localAddress = null;
        InetAddress candidateAddress = null;
        // 遍历所有的网络接口
        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            
            // 去除回环接口，子接口，未运行和接口
            if (iface.isLoopback() || iface.isVirtual() || !iface.isUp()) {
              continue;
            }
            
            // 只读取WiFi或者有线地址
            if (!iface.getDisplayName().contains("Intel") && !iface.getDisplayName().contains("Realtek")) {
              continue;
            }
            

            // 在所有的接口下再遍历IP
            for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                // 排除loopback类型地址
                if (inetAddr.isLoopbackAddress()) {
                    continue;
                }
                // 排除非ipv4
                if(!(inetAddr instanceof Inet4Address) || inetAddr.getHostAddress().contains(":")) {
                    continue;
                }
                
                if (inetAddr.isSiteLocalAddress()) {
                    // 如果是site-local地址，就是它了
                    if(intranetFirst) {
                      return inetAddr;
                    }
                    localAddress = inetAddr;
                } else if (candidateAddress == null) {
                    // site-local类型的地址未被发现，先记录候选地址
                    candidateAddress = inetAddr;
                    if(!intranetFirst) {
                      return candidateAddress;
                    }
                }
            }
        }
        
        if(intranetFirst) {
          if(localAddress != null) {
            return localAddress;
          }
          if(candidateAddress != null) {
            System.out.println("3");
            return candidateAddress;
          }
        }else{
          if(candidateAddress != null) {
            return candidateAddress;
          }
          if(localAddress != null) {
            return localAddress;
          }
        }
        
        // 如果没有发现 non-loopback地址.只能用最次选的方案
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        if (jdkSuppliedAddress == null) {
            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
        }

        System.out.println("4");
        return jdkSuppliedAddress;
    } catch (Exception e) {
        UnknownHostException unknownHostException = new UnknownHostException(
                "Failed to determine LAN address: " + e);
        unknownHostException.initCause(e);
        throw unknownHostException;
    }
  }

}
