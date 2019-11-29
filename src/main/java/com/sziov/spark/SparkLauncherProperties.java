package com.sziov.spark;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @描述: 配置
 * @公司:
 * @作者: 刘恺
 * @版本: 1.0.0
 * @日期: 2019-05-06 15:41:36
 */
@Data
public class SparkLauncherProperties {
    /**
     * spark.master
     */
    private String sparkMaster;
    /**
     * spark.driver.memory
     */
    private String driverMemory;
    /**
     * spark.driver.extraClassPath
     */
    private String driverExtraClasspath;
    /**
     * spark.driver.extraJavaOptions
     */
    private String driverExtraJavaOptions;
    /**
     * spark.driver.extraLibraryPath
     */
    private String driverExtraLibraryPath;
    /**
     * spark.executor.memory
     */
    private String executorMemory;
    /**
     * spark.executor.extraClassPath
     */
    private String executorExtraClasspath;
    /**
     * spark.executor.extraJavaOptions
     */
    private String executorExtraJavaOptions;
    /**
     * spark.executor.extraLibraryPath
     */
    private String executorExtraLibraryPath;
    /**
     * spark.executor.cores
     */
    private String executorCores;
    /**
     * spark.launcher.childProcLoggerName
     */
    private String childProcessLoggerName;
    /**
     * spark-internal
     */
    private String noResource;
    /**
     * spark.launcher.childConectionTimeout
     */
    private String childConnectionTimeout;

    /**
     * spark.kryoserializer.buffer.max
     */
    private String kryoserializerBufferMax;

}
