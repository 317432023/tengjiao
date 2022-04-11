package com.tengjiao.part.springboot.controller;

import cn.hutool.core.net.NetUtil;
import com.tengjiao.tool.third.ip.HutoolIpTool;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShutDownController implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    /**
     * 程序执行完后最后会输出：Process finished with exit code 0，给JVM一个SIGNAL
     * @return
     */
    @PostMapping("/shutDownContext")
    public String shutDownContext(HttpServletRequest request) {
        // 本机ip地址
        String localIp = NetUtil.getLocalhost().getHostAddress();
        // 访问者ip地址
        String accessorIp = HutoolIpTool.getClientIpAddress(request);

        // TODO 提取密码判断，决定是否允许shutdown

        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
        new Thread(() -> {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 方法一
            ctx.close();
            // 方法二
            //exitApplication(ctx);
        }).start();

        return "context will shutdown 500ms later, localIp="+localIp+", accessorIp="+accessorIp;
    }

    public static void exitApplication(ConfigurableApplicationContext ctx) {
        int exitCode = SpringApplication.exit(ctx,
          (ExitCodeGenerator) () -> 0);// 把所有PreDestroy的方法执行并停止，传递退出码给所有Context
        System.exit(exitCode);// 将错误码也传给JVM
    }
}
