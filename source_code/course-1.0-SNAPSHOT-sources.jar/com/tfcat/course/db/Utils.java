package com.tfcat.course.db;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.WordDictionary;
import org.omg.CORBA.ARG_IN;

import javax.print.StreamPrintService;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Utils {

    static class Tuple {
        String year;
        String province;
        String studentType;
        String batch;
        String college;
        String major;
        String maxormin;
    }

    private static final String JDBC_URL = "jdbc:mysql://49.233.197.95:3306/HP171?ServerTimezone=GMT";
    private static final String USER = "root";
    private static final String PASSWORD = "171shujukuxiaozu";
    private static Connection connection;
    private static HashSet<String> studentTypeSet = new HashSet<>();
    private static HashSet<String> batchSet = new HashSet<>();
    private static JiebaSegmenter segmenter = new JiebaSegmenter();

    private static void lines2Set(InputStream in, HashSet<String> set) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            set.add(line);
        }
    }

    private static void lines2UserDict(InputStream in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            WordDictionary.getInstance().loadDict();
        }
    }

    private static void init(String dictPath) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        WordDictionary.getInstance().loadUserDict(new File(dictPath).toPath());
        InputStream in = Utils.class.getResourceAsStream("/batch.txt");
        lines2Set(in, batchSet);

        in = Utils.class.getResourceAsStream("/student_type.txt");
        lines2Set(in, studentTypeSet);
    }

    /**
     * 查询2017年山东省理科本科批的分数线
     * 查询2017年山东大学在山东省的理科分数线
     * 查询2017年山东大学在山东省的计算机专业分数线
     *
     * 在init方法中已经将 所有省份名称、大学名称、专业名称、批次、考试类别加入结巴分词的词典中
     * 当然还需要完善以下条件判断语句 todo
     *
     * @return Tuple 这是一个元组信息，使用它来拼接sql语言，所以需要做的就是完善这个方法，增加模板，提高识别的准确率 todo
     */
    private static Tuple doParse(String line) {
        List<SegToken> list = segmenter.process(line, JiebaSegmenter.SegMode.SEARCH);
        String year = null;
        String province = null;
        String studentType = null;
        String batch = null;
        String college = null;
        String major = null;
        String maxormin=null;
        for (int i = 0; i < list.size(); i++) {
            String word = list.get(i).word;
            if ("年".equals(word)) year = list.get(i - 1).word;
            if (word.contains("省") || word.contains("市")) province = word.substring(0, word.length() - 1);
            if (studentTypeSet.contains(word)) studentType = word;
            if (batchSet.contains(word)) batch = word;
            if (word.contains("大学")||word.contains("学院")) college = word;
            if (word.contains("专业")) major = word.substring(0, word.length() - 2);
            if(word.contains("最高")||word.contains("最低"))maxormin=word;
        }
        Tuple tuple = new Tuple();
        tuple.year = year;
        tuple.province = province;
        tuple.studentType = studentType;
        tuple.batch = batch;
        tuple.college = college;
        tuple.major = major;
        tuple.maxormin=maxormin;
        return tuple;
    }

    /**
     * Utils类对外提供的方法
     *
     * 根据parse方法返回的元组信息进行sql语言的拼接
     * 目前覆盖的情况不是很完整以及规范性不是很强，所以需要完善 todo
     *
     * Modify Record：
     *  重构了对于输入自然语言的相应处理，使根据str输入所产生的那部分衍生sql语句具有变化。
     * Date: 2019年12月16日
     *
     * bug: 打包jar包的时候，dict.txt文件乱码
     * 所以需要传入dict文件的路径
     */
    public static ArrayList<String> parse(String dictPath,String str,String querysentence) throws Exception {
        try {
            init(dictPath);
        } catch (Exception e) {
            System.out.println("初始化失败");
            e.printStackTrace();
            return null;
        }
        Tuple tuple = doParse(str);
        /*String table;
        if (tuple.major != null) {
            table = "major";
        } else if (tuple.college != null) {
            table = "college";
        } else if (tuple.province != null) {
            table = "province";
        } else {
            return;
        }

        String sql = String.format("select t.score_line, e.batch, e.student_type, " + (tuple.major != null ? "t.major_name " : "") +
                        "from enrollment e, %s t " +
                        "where e.year=%s and e.province='%s' %s %s ",
                table, tuple.year, tuple.province,
                tuple.studentType != null ? "and e.student_type='" + tuple.studentType + "'" : "",
                tuple.batch != null ? "and e.batch like '%" + tuple.batch + "%'" : "");

        if ("major".equals(table)) {
            sql += "and t.college_name='" + tuple.college + "' ";
            sql += "and t.major_name like '%" + tuple.major + "%' ";
        } else if ("college".equals(table)) {
            sql += "and t.college_name='" + tuple.college + "' ";
        }

        sql += "and t.enrollment_id = e.id";
        */
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(querysentence);
        ArrayList<String> strResult=new ArrayList<>();
        ArrayList<String> sqlResult=new ArrayList<>();

        //rs.next();
        //int i=rs.getInt(1);
        //trResult.add(new Integer(i).toString());
        //return strResult;

        //模板1：用户输入查询xx年xx省xx类型xx批分数线
        //自然语言输入：查询2017年山东省理科本科批分数线
        //SQl输入：select p.score_line from enrollment e,province p where e.year=2017 and e.province='山东' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id
        //查询结果：在2017年山东省理科本科批的分数线是433分
        //衍生SQL语句：select p.score_line from enrollment e,province p where e.year=2018 and e.province='山东' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id
        //衍生SQL语句：select p.score_line from enrollment e,province p where e.year=2019 and e.province='山东' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id
        //衍生SQL语句：select p.score_line from enrollment e,province p where e.year=2017 and e.province='山西' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id
        //衍生SQL语句：select p.score_line from enrollment e,province p where e.year=2017 and e.province='北京' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id

        if(tuple.college==null&&tuple.major==null&&tuple.maxormin==null&&tuple.province!=null&&tuple.year!=null
            &&tuple.studentType!=null&&tuple.batch!=null){
            while (rs.next()) {
                int scoreLine = rs.getInt(1);
                String line = "在"+tuple.year + "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
                sqlResult.add(querysentence);
            }
            if(tuple.year.equals("2017")){
                sqlResult.add(querysentence.replace("2017","2018"));
                sqlResult.add(querysentence.replace("2017","2019"));
                rs=statement.executeQuery(sqlResult.get(1));
                rs.next();
                int scoreLine = rs.getInt(1);
                String line = "在"+2018+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
                rs=statement.executeQuery(sqlResult.get(2));
                rs.next();
                scoreLine = rs.getInt(1);
                line = "在"+2019+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
            }
            if(tuple.year.equals("2018")){
                sqlResult.add(querysentence.replace("2018","2017"));
                sqlResult.add(querysentence.replace("2018","2019"));
                rs=statement.executeQuery(sqlResult.get(1));
                rs.next();
                int scoreLine = rs.getInt(1);
                String line = "在"+2017+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
                rs=statement.executeQuery(sqlResult.get(2));
                rs.next();
                scoreLine = rs.getInt(1);
                line = "在"+2019+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
            }
            if(tuple.year.equals("2019")){
                sqlResult.add(querysentence.replace("2019","2018"));
                sqlResult.add(querysentence.replace("2019","2017"));
                rs=statement.executeQuery(sqlResult.get(1));
                rs.next();
                int scoreLine = rs.getInt(1);
                String line = "在"+2018+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
                rs=statement.executeQuery(sqlResult.get(2));
                rs.next();
                scoreLine = rs.getInt(1);
                line = "在"+2017+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
            }
        /*
            for(String tmp:strResult){
                System.out.println(tmp);
            }
            if(tuple.year.equals("2017")){
                sqlResult.add(querysentence.replace("2017","2018"));
                sqlResult.add(querysentence.replace("2017","2019"));
                rs=statement.executeQuery(sqlResult.get(1));
                rs.next();
                int scoreLine = rs.getInt(1);
                String line = "在"+2018+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
                rs=statement.executeQuery(sqlResult.get(2));
                rs.next();
                scoreLine = rs.getInt(1);
                line = "在"+2019+ "年" + tuple.province + "省"+tuple.studentType+tuple.batch+"的分数线是"+scoreLine+"分" ;
                strResult.add(line);
            }
            for(String tmp:strResult){
                System.out.println(tmp);
            }
            return strResult;
        }*/}
        return strResult;
        /*
        while (rs.next()) {
            int scoreLine = rs.getInt(1);
            String batch = rs.getString(2);
            String studentType = rs.getString(3);
            String major = "";
            if (tuple.major != null) {
                major = rs.getString(4);
            }
            String line = "在"+tuple.province + "省" + tuple.year + "年" + (tuple.studentType != null ? tuple.studentType : studentType)  + batch
                    + (tuple.college != null ? tuple.college : "") + "的" + (tuple.major != null ? major : "") + "专业的招生分数是" + scoreLine+"分";
            resultList.add(line);
        }
        for(String tmp:resultList){
            System.out.println(tmp);
        }
        System.out.println(tuple.maxormin);
        return resultList;
        */
    }

    /**
     * 用于测试
     * 打包的方法：maven clean => maven package
     */
    public static void main(String[] args) throws Exception {
        ArrayList<String> resultList=parse("C:\\Users\\tianw\\IdeaProjects\\course(1)\\course\\target\\classes\\dict.txt",
                "查询2019年山东省理科本科批分数线",
                "select p.score_line from enrollment e,province p where e.year=2019 and e.province='山东' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id;");
        System.out.println(resultList);
        for(String tmp:resultList){
            System.out.println(tmp);
        }

    }
}

