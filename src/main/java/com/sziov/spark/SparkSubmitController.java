package com.sziov.spark;

import com.google.gson.Gson;
import com.sziov.common.entity.ResultVo;
import com.sziov.common.utils.ResultUtils;
import com.sziov.common.utils.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * @描述: Spark任务提交服务
 * @公司:
 * @作者: 刘恺
 * @版本: 1.0.0
 * @日期: 2019-05-05 08:55:24
 */
@RestController
@RequestMapping("/v1")
@Validated
@Api(value = "spark任务提交", description = "spark任务提交")
@Slf4j
public class SparkSubmitController {
    @Value("${spark.business.exportSign.jarPath}")
    private String jarPath;
    @Value("${spark.business.exportSign.mainClass}")
    private String mainClass;
    @Value("${spark.business.exportSign.master}")
    private String master;
    @Value("${spark.business.exportSign.deployMode}")
    private String deployMode;
    @Value("${spark.business.exportSign.appName}")
    private String appName;
    @Value("${spark.business.exportSign.queue}")
    private String queue;
    @Value("${spark.business.exportSign.driverMemory}")
    private String driverMemory;
    @Value("${spark.business.exportSign.executorMemory}")
    private String executorMemory;
    @Value("${spark.business.exportSign.kryoserializerBufferMax}")
    private String kryoserializerBufferMax;

    @ApiResponse(code = 200, message = "执行状态")
    @ApiOperation(value = "提交历史数据查询JOB", notes = "异步导出数据")
    @PostMapping("inside/spark/export-sign-job")
    public ResultVo<SparkSubmitDto> submitJob(@RequestBody @Validated @ApiParam(name = "任务信息", value = "传入json格式", required = true) ExportJobSubmitVo exportJobSubmitVo, String logPath) throws IOException, InterruptedException {
        final Gson gson = new Gson();
        final String paramJson = gson.toJson(exportJobSubmitVo);
        final Base64 base64 = new Base64();
        final byte[] textByte = paramJson.getBytes("UTF-8");
        final String encodedText = base64.encodeToString(textByte);
        String[] args = new String[]{encodedText};
        SparkLauncherProperties sparkLauncherProperties = new SparkLauncherProperties();
        sparkLauncherProperties.setKryoserializerBufferMax(kryoserializerBufferMax);
        sparkLauncherProperties.setExecutorMemory(executorMemory);
        sparkLauncherProperties.setDriverMemory(driverMemory);
        log.info("kryoserializerBufferMax:" + kryoserializerBufferMax);
        SparkSubmitDto sparkSubmitDto = SparkSubmitUtils.submitJob(queue, appName + exportJobSubmitVo.getTaskId(), jarPath, mainClass, master, deployMode, logPath, sparkLauncherProperties, args, exportJobSubmitVo.isWaitForFinish());
        return ResultUtils.success(sparkSubmitDto);
    }

    @ApiResponse(code = 200, message = "demo")
    @ApiOperation(value = "demo", notes = "demo")
    @PostMapping("inside/spark/demo-job")
    public ResultVo<SparkSubmitDto> submitDemo(String master, String jarPath, String mainClass, String deployMode, String logPath, boolean isWaitForFinish, String driverMemory) throws IOException, InterruptedException {
        SparkSubmitDto sparkSubmitDto = SparkSubmitUtils.submitJob("demo", "demo" + UUID.randomUUID().toString(), jarPath, mainClass, master, deployMode, logPath, null, null, isWaitForFinish);
        return ResultUtils.success(sparkSubmitDto);
    }
}
