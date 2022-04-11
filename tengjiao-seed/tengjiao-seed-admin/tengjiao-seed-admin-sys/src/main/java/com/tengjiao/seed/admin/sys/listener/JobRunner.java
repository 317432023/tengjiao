package com.tengjiao.seed.admin.sys.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tengjiao.seed.admin.dao.sys.mapper.JobMapper;
import com.tengjiao.seed.admin.model.sys.entity.Job;
import com.tengjiao.seed.admin.sys.config.SchedulingConfig;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tengjiao
 */
@Component
@AllArgsConstructor
public class JobRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobRunner.class);

    private JobMapper jobMapper;
    private SchedulingConfig.CronTaskRegistrar cronTaskRegistrar;

    @Override
    public void run(String... args) {
        //初始加载数据库里状态为正常的定时任务
        List<Job> jobList = jobMapper.selectList(new QueryWrapper<Job>().eq("status", 1));
        if (CollectionUtils.isNotEmpty(jobList)) {
            for (Job job : jobList) {
                SchedulingConfig.SchedulingRunnable task = new SchedulingConfig.SchedulingRunnable(job.getBeanName(), job.getMethodName(), job.getMethodParams());
                cronTaskRegistrar.addCronTask(task, job.getCronExpression());
            }

            logger.info("定时任务已加载完毕...");
        }
    }
}