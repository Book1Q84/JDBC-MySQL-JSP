package com.tianwujun.servlet;

import java.util.ArrayList;

/**
 * jsp中的tuple少了studentType和batch
 * 那命名输入都一样，为什么会产生不一样的结果呢？
 * 会不会是因为jieba的问题就是我刚才qq给你截图的那个异常
 *
 * 可以对比一下：
 * jsp中的分词结果
 *
 * Test中的分词结果
 * [[查询, 0, 2], [2017, 2, 6], [年, 6, 7], [山东省, 7, 10], [理科, 10, 12], [本科批, 12, 15], [分数线, 15, 18]]
 * [[查询, 0, 2], [2017, 2, 6], [年, 6, 7], [山东省, 7, 10], [理科, 10, 12], [本科批, 12, 15], [分数线, 15, 18]]
 *
 * 到这里就不一样了
 * 所以出现在for语句中
 * 乱码了 ....
 * 这就是问题所在了
 * 那province的呢
 * 因为batch和studentType依赖那个set来判断 如果set存放的是乱码 那么就无法判断出来了
 *
 * 应该是tomcat的编码问题
 * 那我仅tomcat的conf里面改改？
 * 你打开你的tomcat lib目录
 *
 * 文件本身没乱码 是tomcat解析的时候没有按照utf8编码来解析
 * 所以你只需要改tomcat的编码即可
 * 我看下我的server文件
 */
public class Test {
    public static void main(String[] args) throws Exception {// 这里没问题

        ArrayList<String> list = com.tfcat.course.db.Utils.parse("C:\\Users\\tianw\\IdeaProjects\\course(1)\\course\\target\\classes\\dict.txt","查询2017年山东省理科本科批分数线",
                "select p.score_line from enrollment e,province p where e.year=2017 and e.province='山东' and e.student_type='理科' and e.batch='本科批' and e.id=p.enrollment_id;");
        System.out.println(list);
        //这里就没问题 跟你在jsp中的一样
        //对，就是这个问题，我就很奇怪不知道是什么原因，我给你看一下哈

    }
}
