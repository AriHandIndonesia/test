package com.hand.demo.app.job;

import com.hand.demo.app.service.InvoiceInfoQueueService;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@JobHandler("hexam-47835-scheduler")
public class ScheduleJob implements IJobHandler {

    @Autowired
    InvoiceInfoQueueService invoiceInfoQueueService;

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
        invoiceInfoQueueService.scheduleListener(map);
        System.out.println("Scheduler Executed");
        System.out.println(map);

        return ReturnT.SUCCESS;
    }
}
