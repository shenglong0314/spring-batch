package com.example.demo.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;


@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {


    Log log = LogFactory.getLog(this.getClass());

    long s;
    @Override
    public void afterJob(JobExecution jobExecution) {
        Long e = System.currentTimeMillis();
        log.info("总共用时为"+(e-s)/1000/60);

    }

    /* (non-Javadoc)
     * @see org.springframework.batch.core.domain.JobListener#beforeJob(org.springframework.batch.core.domain.JobExecution)
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {

         s = System.currentTimeMillis();


    }
}
