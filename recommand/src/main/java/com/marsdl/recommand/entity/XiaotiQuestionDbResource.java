package com.marsdl.recommand.entity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 小题up的保存数据库试题 bean
 */
public class XiaotiQuestionDbResource extends BaseEntity {

    /**
     * 题目id
     */
    private String titleId;
    /**
     * 题目类型
     */
    private String titleType;
    /**
     * 题目内容
     */
    private String titleContent;
    /**
     * 科目
     */
    private String course;
    /**
     * 科目Id
     */
    private String courseId;

    /**
     * 级别
     */
    private String examClass;

    /**
     * 级别Id
     */
    private String examClassId;

    /**
     * 试卷
     */
    private String paperName;

    /**
     * 试卷Id
     */
    private String paperId;

    /**
     * 答案
     */
    private List<String> answerOptionList;

    /**
     * 正确答案
     */
    private List<String> correctAnswerList;

    /**
     * 答案解析
     */
    private String correctAnswerExplain;

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getTitleType() {
        return titleType;
    }

    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }

    public String getTitleContent() {
        return titleContent;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getExamClass() {
        return examClass;
    }

    public void setExamClass(String examClass) {
        this.examClass = examClass;
    }

    public String getExamClassId() {
        return examClassId;
    }

    public void setExamClassId(String examClassId) {
        this.examClassId = examClassId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public List<String> getAnswerOptionList() {
        return answerOptionList;
    }

    public void setAnswerOptionList(List<String> answerOptionList) {
        this.answerOptionList = answerOptionList;
    }

    public List<String> getCorrectAnswerList() {
        return correctAnswerList;
    }

    public void setCorrectAnswerList(List<String> correctAnswerList) {
        this.correctAnswerList = correctAnswerList;
    }

    public String getCorrectAnswerExplain() {
        return correctAnswerExplain;
    }

    public void setCorrectAnswerExplain(String correctAnswerExplain) {
        this.correctAnswerExplain = correctAnswerExplain;
    }

    public static void main(String[] args) {
        Field[] fields = XiaotiQuestionDbResource.class.getDeclaredFields();

//        StringBuilder sb = new StringBuilder();
//        for (Field field : fields) {
//            String fieldName = field.getName();
//            sb.append("String ").append(fieldName).append(";\n");
//        }
//        sb.append("\n");
//
//        for (Field field : fields) {
//            String fieldName = field.getName();
//            sb.append("this.").append(fieldName).append(",\n");
//        }

//        System.out.println(sb.toString());

        StringBuilder sb = new StringBuilder();

        for (Field field : fields) {
            String fieldName = field.getName();
            sb.append(fieldName).append(":").append("json[\'").append(fieldName).append("\'").append("]").append(" as String, ");
            sb.append("\n");
        }
        System.out.println(sb.toString());
//
        for (Field field : fields) {
            StringBuilder sbb = new StringBuilder();

            String fieldName = field.getName();
            String lastName = fieldName.substring(1, fieldName.length());
            String firstChar = (fieldName.charAt(0) + "").toUpperCase();
            String standardName = firstChar + lastName;


            sbb.append("if (StringUtils.isNotBlank(xiaotiQuestionDbResource.get");
            sbb.append(standardName).append("())) {");
            sbb.append("\n");
            sbb.append("update.set(\"").append(fieldName).append("\", xiaotiQuestionDbResource.get").append(standardName);
            sbb.append("());");
            sbb.append("\n");
            sbb.append("}");

            System.out.println(sbb.toString());
        }
    }

}
