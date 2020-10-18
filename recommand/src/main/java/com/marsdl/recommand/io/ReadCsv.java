package com.marsdl.recommand.io;

import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.entity.CustomRatingEntity;
import com.marsdl.recommand.entity.ItemUserList;
import com.marsdl.recommand.entity.UserItemList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReadCsv {

    @Value("${mongodb.recommand.user_item_list_collection}")
    private String userItemListCollection;
    @Value("${mongodb.recommand.item_user_list_collection}")
    private String itemUserListCollection;

    @Resource
    private Dao dao;

    private CellProcessor[] getProcessors() {
        return new CellProcessor[]{
                new Optional(),
                new Optional(),
                new Optional(new ParseDouble()),
                new Optional(new ParseLong())
        };
    }

    //csv用户打分数据按行读取（生产条件下可以只读取打分高的数据，这样才有推荐意义）
    public void readWithCsvBeanReader(final String csvFilePath) throws Exception {
        long nowTime = System.currentTimeMillis();
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(csvFilePath), CsvPreference.STANDARD_PREFERENCE);

            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            Map<String, List<String>> itemUserListMap = new HashMap<>();
            Map<String, List<String>> userItemListMap = new HashMap<>();

            int index = 0;
            CustomRatingEntity customer;
            while ((customer = beanReader.read(CustomRatingEntity.class, header, processors)) != null) {
                customItemUserListMap(customer, itemUserListMap, userItemListMap);
                System.out.println(index);
                index++;
            }

            dao.savaMap(itemUserListMap, ItemUserList.class, itemUserListCollection);
            dao.savaMap(userItemListMap, UserItemList.class, userItemListCollection);
        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }
        long wasteTime = System.currentTimeMillis() - nowTime;
        System.out.println("读取解析csv耗时(ms): " + wasteTime);
    }

    //数据格式化，用户-产品列表，产品-用户列表
    public void customItemUserListMap(CustomRatingEntity customRatingEntity, Map<String, List<String>> itemUserListMap,
                                      Map<String, List<String>> userItemListMap) {

        //用户下的产品列表
        List<String> itemList = userItemListMap.get(customRatingEntity.getUserId());
        if (CollectionUtils.isEmpty(itemList)) {
            itemList = new ArrayList<>();
            itemList.add(customRatingEntity.getMovieId());
            userItemListMap.put(customRatingEntity.getUserId(), itemList);
        }
        if (!itemList.contains(customRatingEntity.getMovieId())) {
            itemList.add(customRatingEntity.getMovieId());
        }

        //物品到用户的倒排表
        List<String> userList = itemUserListMap.get(customRatingEntity.getMovieId());
        if (CollectionUtils.isEmpty(userList)) {
            userList = new ArrayList<>();
            userList.add(customRatingEntity.getUserId());
            itemUserListMap.put(customRatingEntity.getMovieId(), userList);
            return;
        }
        if (!userList.contains(customRatingEntity.getUserId())) {
            userList.add(customRatingEntity.getUserId());
        }
    }


}
