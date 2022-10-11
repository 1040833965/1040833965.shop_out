package com.itzm.shop.controller;

import com.itzm.shop.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author : 张金铭
 * @description : 通用控制层 ，文件的上传和下载
 * @create :2022-10-02 23:01:00
 */
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * TODO: 文件上传
     * file: 接收前端传的文件
     * @return
     */
    @PostMapping("/upload")
    public JsonResult<String> upload(MultipartFile file){
//        log.info("file：{}",file.toString());
        //获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名
        String fileName = UUID.randomUUID().toString()+substring;

        //创建file 目录对象
        File dir = new File(basePath);
        //判断存储路径是否存在，不存在则创建
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            //设置文件完整存储路径
            //讲文件存储到对应目录文件夹下
            file.transferTo(new File(dir, fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonResult.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //输出流，通过输出流将文件写回浏览器，在浏览器显示图片
            ServletOutputStream outputStream = response.getOutputStream();

            int len = 0;
            byte[] bytes = new byte[1042];

            while ( (len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
