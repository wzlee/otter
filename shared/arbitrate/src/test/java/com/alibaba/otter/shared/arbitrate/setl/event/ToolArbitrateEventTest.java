package com.alibaba.otter.shared.arbitrate.setl.event;

import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import com.alibaba.otter.shared.arbitrate.impl.setl.ToolArbitrateEvent;
import com.alibaba.otter.shared.arbitrate.model.RemedyIndexEventData;
import com.alibaba.otter.shared.arbitrate.model.SyncStatusEventData;
import com.alibaba.otter.shared.arbitrate.model.SyncStatusEventData.SyncStatus;

public class ToolArbitrateEventTest extends BaseArbitrateEventTest {

    private ToolArbitrateEvent toolEvent;

    @Test
    public void test_simple() {
        toolEvent = new ToolArbitrateEvent();
        SyncStatusEventData eventData = toolEvent.fetch(pipelineId);
        eventData.setPipelineId(pipelineId);
        eventData.addStatus(new SyncStatus(false, 1000));
        eventData.addStatus(new SyncStatus(true, 1001));
        toolEvent.single(eventData);

        eventData = toolEvent.fetch(pipelineId);
        want.object(eventData).notNull();
    }

    @Test
    public void test_remedy() {
        toolEvent = new ToolArbitrateEvent();

        RemedyIndexEventData data = new RemedyIndexEventData();
        Date now = new Date();
        Long start = now.getTime() - 24 * 3600 * 60;

        Long startProcessId = 100L;
        for (int i = 10; i >= 1; i--) {
            data.setPipelineId(pipelineId);
            data.setProcessId(startProcessId + i);
            data.setStartTime(start + startProcessId + i);
            data.setEndTime(start + startProcessId + 2 * i);
            toolEvent.addRemedyIndex(data);
        }

        List<RemedyIndexEventData> indexs = toolEvent.listRemedyIndexs(pipelineId);
        want.collection(indexs).sizeEq(10);

        for (RemedyIndexEventData index : indexs) {
            toolEvent.removeRemedyIndex(index);
        }

        indexs = toolEvent.listRemedyIndexs(pipelineId);
        want.collection(indexs).sizeEq(0);
    }
}