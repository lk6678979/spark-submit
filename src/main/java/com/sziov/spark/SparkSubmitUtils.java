package com.sziov.spark;

import com.sziov.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @描述: Spark的Job提交工具类
 * @公司:
 * @作者: 刘恺
 * @版本: 1.0.0
 * @日期: 2019-05-07 16:26:31
 */
@Component
@Slf4j
public class SparkSubmitUtils {

    @Autowired
    private Environment environment;

    private static String javaHome;

    private static String sparkHome;

    private static String hadoopConf;

    @PostConstruct
    public void init() {
        javaHome = environment.getProperty("spark.javaHome", "");
        sparkHome = environment.getProperty("spark.sparkHome", "");
        hadoopConf = environment.getProperty("spark.hadoopConf", "");

    }

    /**
     * @描述: 提交Job
     * @入参: queue 执行队列
     * @入参: appName app名称
     * @入参: jarPath 执行jar的路径
     * @入参: classPath   启动主方法类路径
     * @入参: master  master
     * @入参: deployMode  发布模式，如果master为local则不需要提供，如果master为yarn，需要选择client或者cluster
     * @入参: outFilePath 日志输出路径
     * @入参: sparkLauncherProperties 其他参数
     * @入参: waitForFinish   是否等待job完成
     * @返回值:
     * @作者: 刘恺
     * @日期: 2019/5/7 16:08
     */
    public static SparkSubmitDto submitJob(String queue, String appName, String jarPath, String classPath, String master, String deployMode, String outFilePath, SparkLauncherProperties sparkLauncherProperties, String[] appArgs, boolean waitForFinish) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SparkAppHandle handle = null;
        final String[] appId = {""};
        try {
            HashMap env = new HashMap();
            //如果使用java -jar 的方式启动这个服务，可以在下面使用setJavaHome和setSparkHome设置
            //如果使用service启动，需要使用如下方式配置
            env.put("JAVA_HOME", javaHome);
            env.put("SPARK_HOME", sparkHome);
//            env.put("HADOOP_CONF_DIR", hadoopConf);
//            env.put("HADOOP_HOME","/opt/cloudera/parcels/CDH-5.13.0-1.cdh5.13.0.p0.29/lib/hadoop");
//            env.put("SPARK_DIST_CLASSPATH","$(hadoop classpath)");
            SparkLauncher sparkLauncher = new SparkLauncher(env)
                    .setAppName(appName)
                    .setAppResource(jarPath)
                    .setMainClass(classPath)
                    .setMaster(master);
            sparkLauncher.setConf("spark.queue", queue);
            //使用service启动服务，这2个参数是无效的，可以使用上面2个配置指定
//            sparkLauncher.setJavaHome(javaHome);
//            sparkLauncher.setSparkHome(sparkHome);
            sparkLauncher.redirectToLog("org.slf4j.Logger");
            if (appArgs != null && appArgs.length > 0) {
                sparkLauncher.addAppArgs(appArgs);
            }
            if (StringUtils.isNotBlank(deployMode)) {
                sparkLauncher.setDeployMode(deployMode);
            }
            if (StringUtils.isNotBlank(outFilePath)) {
                sparkLauncher.redirectOutput(new File(outFilePath));
            }
            //参数
            if (sparkLauncherProperties != null) {
                //sparkArg参数
//                if (sparkLauncherProperties.getSparkArg() != null && !sparkLauncherProperties.getSparkArg().isEmpty()) {
//                    for (Map.Entry<String, String> arg : sparkLauncherProperties.getSparkArg().entrySet()) {
//                        sparkLauncher.addSparkArg(arg.getKey(), arg.getValue());
//                    }
//                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getKryoserializerBufferMax())) {
                    log.info("参数【kryoserializerBufferMax】:" + sparkLauncherProperties.getKryoserializerBufferMax());
                    sparkLauncher.setConf("spark.kryoserializer.buffer.max", sparkLauncherProperties.getKryoserializerBufferMax());
                }
                //设置master
                if (StringUtils.isNotBlank(sparkLauncherProperties.getSparkMaster())) {
                    sparkLauncher.setConf(SparkLauncher.SPARK_MASTER, sparkLauncherProperties.getSparkMaster());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getDriverMemory())) {
                    log.info("参数【driverMemory】:" + sparkLauncherProperties.getDriverMemory());
                    sparkLauncher.setConf(SparkLauncher.DRIVER_MEMORY, sparkLauncherProperties.getDriverMemory());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getDriverExtraClasspath())) {
                    sparkLauncher.setConf(SparkLauncher.DRIVER_EXTRA_CLASSPATH, sparkLauncherProperties.getDriverExtraClasspath());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getDriverExtraJavaOptions())) {
                    sparkLauncher.setConf(SparkLauncher.DRIVER_EXTRA_JAVA_OPTIONS, sparkLauncherProperties.getDriverExtraJavaOptions());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getExecutorExtraLibraryPath())) {
                    sparkLauncher.setConf(SparkLauncher.DRIVER_EXTRA_LIBRARY_PATH, sparkLauncherProperties.getExecutorExtraLibraryPath());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getExecutorMemory())) {
                    log.info("参数【executorMemory】:" + sparkLauncherProperties.getExecutorMemory());
                    sparkLauncher.setConf(SparkLauncher.EXECUTOR_MEMORY, sparkLauncherProperties.getExecutorMemory());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getExecutorExtraClasspath())) {
                    sparkLauncher.setConf(SparkLauncher.EXECUTOR_EXTRA_CLASSPATH, sparkLauncherProperties.getExecutorExtraClasspath());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getExecutorExtraJavaOptions())) {
                    sparkLauncher.setConf(SparkLauncher.EXECUTOR_EXTRA_JAVA_OPTIONS, sparkLauncherProperties.getExecutorExtraJavaOptions());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getExecutorExtraLibraryPath())) {
                    sparkLauncher.setConf(SparkLauncher.EXECUTOR_EXTRA_LIBRARY_PATH, sparkLauncherProperties.getExecutorExtraLibraryPath());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getExecutorCores())) {
                    sparkLauncher.setConf(SparkLauncher.EXECUTOR_CORES, sparkLauncherProperties.getExecutorCores());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getChildProcessLoggerName())) {
                    sparkLauncher.setConf(SparkLauncher.CHILD_PROCESS_LOGGER_NAME, sparkLauncherProperties.getChildProcessLoggerName());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getNoResource())) {
                    sparkLauncher.setConf(SparkLauncher.NO_RESOURCE, sparkLauncherProperties.getNoResource());
                }
                if (StringUtils.isNotBlank(sparkLauncherProperties.getChildConnectionTimeout())) {
                    sparkLauncher.setConf(SparkLauncher.CHILD_CONNECTION_TIMEOUT, sparkLauncherProperties.getChildConnectionTimeout());
                }
            }

            handle = sparkLauncher.startApplication(new SparkAppHandle.Listener() {
                /**
                 *  UNKNOWN(false),
                 *  CONNECTED(false),
                 *  SUBMITTED(false),
                 *  UNNING(false),
                 *  FINISHED(true),
                 *  FAILED(true),
                 *  KILLED(true),
                 *  LOST(true);
                 */
                @Override
                public void stateChanged(SparkAppHandle handle) {
                    SparkAppHandle.State state = handle.getState();
                    //获取AppId
                    if (StringUtils.isBlank(appId[0])) {
                        appId[0] = handle.getAppId();
                    }
                    //不需要等到结束，拿到appId或者结束后就退出监听
                    if (!waitForFinish && StringUtils.isNotBlank(appId[0])) {
                        countDownLatch.countDown();
                        return;
                    }
                    //执行完成后退出
                    if (state.isFinal()) {
                        countDownLatch.countDown();
                        return;
                    }
                }

                @Override
                public void infoChanged(SparkAppHandle handle) {
                }
            });
            //等待
            countDownLatch.await(Long.MAX_VALUE, TimeUnit.MINUTES);
            SparkSubmitDto sparkSubmitDto = new SparkSubmitDto();
            sparkSubmitDto.setAppId(appId[0]);
            if (SparkAppHandle.State.UNKNOWN.equals(handle.getState())) {
                sparkSubmitDto.setState("UNKNOWN");
            } else if (SparkAppHandle.State.CONNECTED.equals(handle.getState())) {
                sparkSubmitDto.setState("CONNECTED");
            } else if (SparkAppHandle.State.SUBMITTED.equals(handle.getState())) {
                sparkSubmitDto.setState("SUBMITTED");
            } else if (SparkAppHandle.State.RUNNING.equals(handle.getState())) {
                sparkSubmitDto.setState("RUNNING");
            } else if (SparkAppHandle.State.FINISHED.equals(handle.getState())) {
                sparkSubmitDto.setState("FINISHED");
            } else if (SparkAppHandle.State.FAILED.equals(handle.getState())) {
                sparkSubmitDto.setState("FAILED");
            } else if (SparkAppHandle.State.KILLED.equals(handle.getState())) {
                sparkSubmitDto.setState("KILLED");
            } else if (SparkAppHandle.State.LOST.equals(handle.getState())) {
                sparkSubmitDto.setState("LOST");
            }
            //不用等待Job完成
            if (!waitForFinish) {
                return sparkSubmitDto;
            }
            //等待job完成且是FINISHED，调用rest接口去查询执行是成功还是失败
            if ("FINISHED".equals(sparkSubmitDto.getState())) {
                //TODO 使用rest接口去查询是否成功
                String dealResult = "";
            }
            return sparkSubmitDto;
        } catch (Exception e) {
            log.error("发布JOB失败", e);
            SparkSubmitDto sparkSubmitDto = new SparkSubmitDto();
            sparkSubmitDto.setState("ERROR");
            return sparkSubmitDto;
        }
    }
}
