package com.tengjiao.seed.admin.sys.config;

import com.tengjiao.part.springmvc.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author tengjiao
 * @create 2021/6/27 14:15
 */
@Configuration
public class SchedulingConfig {
    /**
     * 执行定时任务的线程池配置类，该类有一个 schedule(Runnable, CronTrigger) 方法 由于调度 定时任务线程 返回 ScheduledFuture
     *
     * @return
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        //定时任务执行线程池核心线程数
        taskScheduler.setPoolSize(4);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix("TaskSchedulerThreadPool-");
        return taskScheduler;
    }

    /**
     * 定时任务线程的 执行结果 ScheduledFuture包装类
     */
    public static final class ScheduledTask {
        /**
         * ScheduledFuture 是定时任务线程池 ScheduledExecutorService 的执行结果
         */
        volatile ScheduledFuture<?> future;

        /**
         * 取消定时任务
         */
        public void cancel() {
            ScheduledFuture<?> future = this.future;
            if (future != null) {
                future.cancel(true);
            }
        }

    }

    /**
     * 定时任务线程，被定时任务线程池调用，用来执行指定bean里面的方法
     */
    public static class SchedulingRunnable implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(SchedulingRunnable.class);

        private String beanName;

        private String methodName;

        private String params;

        public SchedulingRunnable(String beanName, String methodName) {
            this(beanName, methodName, null);
        }

        public SchedulingRunnable(String beanName, String methodName, String params) {
            this.beanName = beanName;
            this.methodName = methodName;
            this.params = params;
        }

        @Override
        public void run() {
            logger.info("定时任务开始执行- bean：{}，方法：{}，参数：{}", beanName, methodName, params);
            long startTime = System.currentTimeMillis();

            try {
                Object target = SpringContextHolder.getObject(beanName);

                Method method = null;
                if (StringUtils.isNotEmpty(params)) {
                    method = target.getClass().getDeclaredMethod(methodName, String.class);
                } else {
                    method = target.getClass().getDeclaredMethod(methodName);
                }

                ReflectionUtils.makeAccessible(method);
                if (StringUtils.isNotEmpty(params)) {
                    method.invoke(target, params);
                } else {
                    method.invoke(target);
                }
            } catch (Exception ex) {
                logger.error(String.format("定时任务执行异常- bean：%s，方法：%s，参数：%s ", beanName, methodName, params), ex);
            }

            long times = System.currentTimeMillis() - startTime;
            logger.info("定时任务执行结束- bean：{}，方法：{}，参数：{}，耗时：{}毫秒", beanName, methodName, params, times);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            SchedulingRunnable that = (SchedulingRunnable) o;
            if (params == null) {
                return beanName.equals(that.beanName) && methodName.equals(that.methodName) && that.params == null;
            }

            return beanName.equals(that.beanName) && methodName.equals(that.methodName) && params.equals(that.params);
        }

        @Override
        public int hashCode() {
            if (params == null) {
                return Objects.hash(beanName, methodName);
            }

            return Objects.hash(beanName, methodName, params);
        }
    }

    /**
     * 定时任务注册类，用来增加、删除定时任务
     */
    @Component
    public static class CronTaskRegistrar implements DisposableBean {

        /**
         * 该集合存储了 定时任务线程 和 定时任务线程的执行结果
         */
        private final Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

        @Autowired
        private TaskScheduler taskScheduler;

        public TaskScheduler getScheduler() {
            return this.taskScheduler;
        }

        public void addCronTask(Runnable task, String cronExpression) {
            addCronTask(new CronTask(task, cronExpression));
        }

        public void addCronTask(CronTask cronTask) {
            if (cronTask != null) {
                Runnable task = cronTask.getRunnable();
                if (this.scheduledTasks.containsKey(task)) {
                    removeCronTask(task);
                }

                this.scheduledTasks.put(task, scheduleCronTask(cronTask));
            }
        }

        public void removeCronTask(Runnable task) {
            ScheduledTask scheduledTask = this.scheduledTasks.remove(task);
            if (scheduledTask != null) {
                scheduledTask.cancel();
            }
        }

        public ScheduledTask scheduleCronTask(CronTask cronTask) {
            ScheduledTask scheduledTask = new ScheduledTask();
            scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());

            return scheduledTask;
        }


        @Override
        public void destroy() {

            for (ScheduledTask task : this.scheduledTasks.values()) {
                task.cancel();
            }

            this.scheduledTasks.clear();
        }
    }
}
