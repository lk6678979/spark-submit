package com.sziov.spark;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @描述:
 * @公司:
 * @作者: 刘恺
 * @版本: 1.0.0
 * @日期: 2019-05-14 13:56:10
 */
@Data
public class ExportJobSubmitVo implements Serializable {
    @ApiModelProperty(name = "taskId", value = "任务ID（需要保持全局唯一）", required = true)
    private String taskId;

    @ApiModelProperty(name = "waitForFinish", value = "是否等待任务执行完", required = true)
    private boolean waitForFinish;

    @ApiModelProperty(name = "vehicleType", value = "车型", required = true)
    private String vehicleType;

    @ApiModelProperty(name = "vin", value = "vin")
    private String vin;

    @ApiModelProperty(name = "start", value = "开始时间（时间戳）", required = true)
    private String start;

    @ApiModelProperty(name = "stop", value = "结束时间（时间戳）", required = true)
    private String stop;

    @ApiModelProperty(name = "signalValue", value = "信号参数的值（需要全部信号使用*）", required = true)
    private String signalValue;

    @ApiModelProperty(name = "signalOptionValue", value = "信号配置参数（没有配置则使用*）", required = true)
    private String signalOptionValue;
}
