package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用接口
 */
@RestController
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;


    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传接口")
    public Result<String> upload(MultipartFile file){
        String url = null;
        try {
            url = aliOssUtil.upload(file);
        } catch (Exception e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.success(url);

    }

}
