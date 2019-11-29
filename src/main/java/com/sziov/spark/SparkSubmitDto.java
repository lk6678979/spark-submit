package com.sziov.spark;

import lombok.Data;

/**
 * @描述:
 * @公司:
 * @作者: 刘恺
 * @版本: 1.0.0
 * @日期: 2019-05-07 09:33:57
 */
@Data
public class SparkSubmitDto {

    /**
     * APPID
     */
    private String appId;

    /**
     * 执行状态
     */
    private String state;
}
