package com.marsdl.recommand.remmand.common;

import com.alibaba.fastjson.JSON;
import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.entity.RelateCountMatrixEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Queue;

public class Recommand {
    private static final Logger logger = LogManager.getLogger(Recommand.class);

    protected void comsumer(Dao dao, Queue<RelateCountMatrixEntity> queue, final String collectionName) {
        while (true) {
            RelateCountMatrixEntity entity = queue.poll();
            if (entity == null) {
                try {
                    logger.info("queue data is null，waiting 1000ms...");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    //
                }
            }
            //判断队列中是否是最后一条记录 determine if the queue is the last record
            if (entity != null && entity.getCount() == -1) {
                break;
            }
            if (entity != null) {
                dao.saveEntity(entity, collectionName);
                String result = JSON.toJSONString(entity);
                logger.info(result);
            }
        }
    }

    //关联程度的分子，两个列表相同的数量 the same number of two lists
    protected int countListTotalSame(List<String> primaryItemList, List<String> secondaryItemList) {
        int index = 0;
        for (String primaryItem : primaryItemList) {
            if (secondaryItemList.contains(primaryItem)) {
                index++;
            }
        }
        return index;
    }

}
