package com.cameltest.fileOperations;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzScheduler {

    public static void main(String[] args) {

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();


            JobDetail scheduledJob = newJob(CopyFilesScheduled.class)
                    .withIdentity("copyFilesScheduled")
                    .build();

            SimpleTrigger filesCopyTrigger = newTrigger().withIdentity("filesCopyTrigger")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3)
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(scheduledJob, filesCopyTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
