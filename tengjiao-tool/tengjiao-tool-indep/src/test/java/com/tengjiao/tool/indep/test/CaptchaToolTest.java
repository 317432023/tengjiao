package com.tengjiao.tool.indep.test;

import com.tengjiao.tool.indep.web.CaptchaTool;

import java.io.File;
import java.io.IOException;

public class CaptchaToolTest {

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 10; i++) {
            File dir = new File("c:/users/administrator");
            int w = 200, h = 80;
            String captchaCode = CaptchaTool.generateCaptcha(4);
            File file = new File(dir, captchaCode + ".png");
            CaptchaTool.outputImage(w, h, file, captchaCode);
        }

    }
}
