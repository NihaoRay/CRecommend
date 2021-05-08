package com.marsdl.recommand.io;

import com.alibaba.fastjson.JSON;
import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.entity.CustomRatingEntity;
import com.marsdl.recommand.entity.HotQuestion;
import com.marsdl.recommand.entity.XiaotiQuestionDbResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HandleLog {

    @Value("${mongodb.recommand.xiaoti_hotquestion}")
    private String xiaotiHotquestion;

    @Value("${mongodb.recommand.xiaoti_similarquestion}")
    private String xiaotiSimilarquestion;

    private static final String DB_COLLECTION = "xiaoti_question";

    @Resource
    private Dao dao;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

    //试题的id集合
    private Map<String, Long> titleIdCountMap = new HashMap<>();

    //指定路径按行读取
    public void readLog(final String filepath) throws Exception {
        //仔细查看
        dao.dropCollection(xiaotiHotquestion);

        long nowTime = System.currentTimeMillis();
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
        ) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                handleLogLine(line);
            }
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long wasteTime = System.currentTimeMillis() - nowTime;
        System.out.println("读取解析日志耗时(ms): " + wasteTime);

        for (Map.Entry<String, Long> entry : titleIdCountMap.entrySet()) {
            String titleId = entry.getKey();


            XiaotiQuestionDbResource dbResource = dao.findQuestion(titleId,
                    XiaotiQuestionDbResource.class, DB_COLLECTION);

            if (dbResource == null) {
                continue;
            }

            Long answerNum = entry.getValue();
            Double answerNumDouble = answerNum.doubleValue();

            long time = System.currentTimeMillis();
            long addTime = dbResource.getAddTime();
            //时长
            long delta = Math.abs(addTime - time);
            delta = delta == 0 ? 1 : delta;

            //天数
            double days = delta / (1000 * 60 * 60 * 24);
            if (delta < (1000 * 60 * 60 * 24)) {
                days = 0.5;
            }
            //计算热度值
            double hotValue = answerNumDouble / days;
            //封装存储数据
            HotQuestion hotQuestion = new HotQuestion();
            String titleContent = dbResource.getTitleContent();
            if (titleContent.length() > 14) {
                titleContent = titleContent.substring(0, 14);
            }
            hotQuestion.setTitleContent(titleContent);
            hotQuestion.setTitleId(titleId);
            hotQuestion.setHotValue(hotValue);
            hotQuestion.setCourse(dbResource.getCourse());

            System.out.println(JSON.toJSONString(hotQuestion));
            dao.saveEntity(hotQuestion, xiaotiHotquestion);
        }

    }


    public void handleLogLine(String line) {
        if (StringUtils.isBlank(line)) {
            return;
        }
        String[] itemArr = line.split("\\s\\s");
        if (StringUtils.isNotBlank(itemArr[1]) && itemArr[1].contains("xiaotiapp-behavior-answerResult")) {
            HotQuestion hotQuestion = new HotQuestion();
            for (String item : itemArr[1].trim().split("\\,")) {
                item = item.trim();
                String titleId = "0";
                if (item.startsWith("titleId:")) {
                    titleId = item.split("titleId:")[1];
                    hotQuestion.setTitleId(titleId);
                    Long count = titleIdCountMap.get(titleId);
                    if (count != null) {
                        titleIdCountMap.put(titleId, count + 1L);
                    } else {
                        titleIdCountMap.put(titleId, 1L);
                    }
                }
            }
        }
    }

    public long parseTimeByString(String time) {
        long logAddTime;
        try {
            logAddTime = dateFormat.parse(time).getTime();
        } catch (ParseException e) {
            logAddTime = System.currentTimeMillis();
            e.printStackTrace();
        }
        return logAddTime;
    }


    //数据格式化，用户-产品列表，产品-用户列表
    public void customItemUserListMap(CustomRatingEntity customRatingEntity, Map<String, List<String>> itemUserListMap,
                                      Map<String, List<String>> userItemListMap) {

    }

    public static void main(String[] args) {
        HandleLog handleLog = new HandleLog();
//        String line = "2021-05-08 09:46:27.883  INFO 20533: QuestionService-getLatestVersion, 查询版本信息, userId:4625138a2e538a87, osType:xiaomi";
        String line = "2021-05-08 01:07:08.44  INFO 20533: xiaotiapp-behavior-answerResult, userId:4625138a2e538a87, paperId:2022022015, titleId:2022022015001, correctAnswer:C , userAnswer:B ";
        handleLog.handleLogLine(line);

    }


}
