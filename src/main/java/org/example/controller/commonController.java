package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class commonController {
    @Value("${reggie.path}")
    private String uploadPath;
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        System.out.println(file.toString());
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString()+suffix;
        File dir = new File(uploadPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(uploadPath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String fileName, HttpServletResponse response) {
        try {
            // 尝试不同的扩展名
            String[] extensions = {".jpeg", ".jpg", ".png", ".gif", ".bmp"};
            File file = null;
            
            // 首先尝试直接使用传入的文件名
            file = new File(uploadPath + fileName);
            if (!file.exists()) {
                // 如果不存在，尝试添加常见扩展名
                for (String ext : extensions) {
                    file = new File(uploadPath + fileName + ext);
                    if (file.exists()) {
                        break;
                    }
                }
            }
            
            // 检查文件是否存在
            if (!file.exists()) {
                throw new FileNotFoundException("文件不存在: " + uploadPath + fileName);
            }
            
            // 根据文件扩展名设置content-type
            String contentType = "image/jpeg";
            if (file.getName().endsWith(".png")) {
                contentType = "image/png";
            } else if (file.getName().endsWith(".gif")) {
                contentType = "image/gif";
            }
            
            //读取文件流
            FileInputStream fileInputStream = new FileInputStream(file);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType(contentType);
            int len = 0;
            byte[] buffer = new byte[1024];
            while((len = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}