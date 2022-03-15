package com.atguigu.gmall1213.product.controller;

import com.atguigu.gmall1213.common.result.Result;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/15 15:27
 */
@RestController
@RequestMapping("admin/product")
public class FileUploadController {
    //引入图片服务器地址
    @Value("${fileServer.url}")
    private String fileUrl;

    @RequestMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws IOException, MyException {
        //读取配置tracker.conf
        String configFile=this.getClass().getResource("tracker.conf").getFile();
        //返回的路径
        String path=null;

        if (configFile != null) {
            //初始化数据
            ClientGlobal.init(configFile);
            // 创建trackerClient,trackerService
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer=trackerClient.getConnection();
            //创建创建storageClient1
            StorageClient1 storageClient1=new StorageClient1(trackerServer, null);
            //准备上传文件
            //获取文件后缀名
            String extension=FilenameUtils.getExtension(file.getOriginalFilename());
            path=storageClient1.upload_appender_file1(file.getBytes(), extension, null);
        }
        // 拼接上传之后的文件路径
        return Result.ok(fileUrl + path);
    }


}
