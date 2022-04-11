package com.tengjiao.assembly.sample.upload;

import com.tengjiao.assembly.upload.config.UploadProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tengjiao
 */
@Controller
public class TestController {
    private UploadProperties uploadProperties;

    public TestController(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @RequestMapping("")
    @ResponseBody
    public UploadProperties test() {
        return uploadProperties;
    }
}
