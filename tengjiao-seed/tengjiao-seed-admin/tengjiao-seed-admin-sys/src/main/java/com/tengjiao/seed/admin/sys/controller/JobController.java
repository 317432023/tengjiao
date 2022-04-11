package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.model.sys.entity.Job;
import com.tengjiao.seed.admin.service.sys.JobService;
import com.tengjiao.seed.admin.sys.comm.LogRequired;
import com.tengjiao.seed.admin.sys.config.SchedulingConfig;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * 定时任务管理
 *
 * @author tengjiao
 * @date 2021-06-27
 */
@RestController
@ResponseResult
@RequestMapping("system/job")
@Api(tags = "定时任务 ")
public class JobController {
    @Autowired
    private JobService jobService;
    @Autowired
    private SchedulingConfig.CronTaskRegistrar cronTaskRegistrar;

    /**
     * <p>
     * 单表分页查询_[单列可排序NonCount]
     * </p>
     * 用于记录较少的表，大表查询禁止使用该方法！！！
     *
     * @param params /
     * @return /
     */
    @PostMapping("query/list/{current}/{size}")
    @ApiOperation("分页查询定时任务_[单表单列可排序NonCount]")
    public R pageJob(@PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
        IPage<Job> jobPage = jobService.page(new Page<>(current, size), params);
        return new R(Dict.create().set("total", jobPage.getTotal()).set("records", jobPage.getRecords()));
    }

    @PutMapping("mod")
    @ApiOperation("修改定时任务")
    @LogRequired("修改定时任务")
    public void modJob(@Validated @RequestBody Job job) {
        Job oldJob = jobService.getOne(job.getId());
        if( jobService.mod(job) ) {
            // 先移除再添加
            if (oldJob.getStatus() == 1) {
                SchedulingConfig.SchedulingRunnable task = new SchedulingConfig.SchedulingRunnable(oldJob.getBeanName(), oldJob.getMethodName(), oldJob.getMethodParams());
                cronTaskRegistrar.removeCronTask(task);
            }

            Job sysJob = jobService.getOne(job.getId());
            if(sysJob.getStatus() == 1){
                SchedulingConfig.SchedulingRunnable task=new SchedulingConfig.SchedulingRunnable(sysJob.getBeanName(),sysJob.getMethodName(),sysJob.getMethodParams());
                cronTaskRegistrar.addCronTask(task,sysJob.getCronExpression());
            }
        }
    }

    @PostMapping("add")
    @ApiOperation("添加定时任务")
    @LogRequired("添加定时任务")
    public void addJob(@Validated @RequestBody Job job) {
        boolean success = jobService.add(job);
        if( success ) {
            if (job.getStatus() == 1) {
                SchedulingConfig.SchedulingRunnable task = new SchedulingConfig.SchedulingRunnable(job.getBeanName(), job.getMethodName(), job.getMethodParams());
                cronTaskRegistrar.addCronTask(task, job.getCronExpression());
            }
        }

    }

    @DeleteMapping("del/{id}")
    @ApiOperation("删除定时任务")
    @LogRequired("删除定时任务")
    public void delJob(@PathVariable Serializable id) {
        Job existedSysJob = jobService.getOne(id);
        boolean success = jobService.del(id);
        if( !success ) {
            throw SystemException.create("删除定时任务失败");
        } else {
            if(existedSysJob.getStatus() == 1){
                SchedulingConfig.SchedulingRunnable task=new SchedulingConfig.SchedulingRunnable(existedSysJob.getBeanName(),existedSysJob.getMethodName(),existedSysJob.getMethodParams());
                cronTaskRegistrar.removeCronTask(task);
            }
        }
    }

    @GetMapping("query/one/{id}")
    @ApiOperation("查询一个定时任务")
    public Job queryJobById(@PathVariable Serializable id) {
        return jobService.getOne(id);
    }
}

