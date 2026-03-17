package com.medhub.controller.admin;

import com.medhub.annotation.AutoFill;
import com.medhub.constant.MessageConstant;
import com.medhub.enumeration.OperationType;
import com.medhub.result.Result;
import com.medhub.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共相关接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     */
    @RequestMapping("/upload")
    @AutoFill(value = OperationType.INSERT)
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传，文件名称：{}",file.getOriginalFilename());
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成新文件名
            String newFileName = System.currentTimeMillis() + extension;
            String url = aliOssUtil.upload(file.getBytes() , newFileName);
            return Result.success(url);
        } catch (IOException e) {
            log.error(MessageConstant.UPLOAD_FAILED,e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
