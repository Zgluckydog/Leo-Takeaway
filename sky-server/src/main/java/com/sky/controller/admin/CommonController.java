package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 * */

@RestController
@Slf4j
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     * */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            // 获取原始文件名
            String originalFileName = file.getOriginalFilename();
            // 获取文件后缀.jpg/.png等
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 使用UUID生成不重复的文件名+后缀
            String newFileName = UUID.randomUUID().toString() + extension;
            // 文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
