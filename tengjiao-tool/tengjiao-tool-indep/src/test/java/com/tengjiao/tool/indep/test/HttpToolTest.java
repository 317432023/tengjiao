package com.tengjiao.tool.indep.test;

import com.tengjiao.tool.indep.HttpTool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpToolTest {
    public static void main(String[] args) throws IOException {
        // 测试get请求
        String content = HttpTool.get("https://28ki.com/canada28", 2000, 2000);
        System.out.println(content);

        // 测试文件上传
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("myFileName", "C:\\Users\\Administrator\\Desktop\\TODO.txt");
        String response = HttpTool.formUpload("http://localhost:8080/upload",
          null, fileMap, 2000, 3000);
        System.out.println(response);
    }
}
