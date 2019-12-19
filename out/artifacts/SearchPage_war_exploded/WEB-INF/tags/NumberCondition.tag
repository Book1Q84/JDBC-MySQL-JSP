<%@ tag pageEncoding="utf-8" %>
<%@ tag import="java.sql.*" %>
<%@tag import="com.tfcat.course.db.Utils"%>
<%@ tag import="java.util.ArrayList" %>
<%@ attribute name="strResult" required="true" %>
<%@ attribute name="sqlResult" required="true" %>
<%@ variable name-given="naturalResult" scope="AT_END" %>
<%@ variable name-given="queryResultByNumber" scope="AT_END" %>
<%@ variable name-given="getResult" scope="AT_END" %>
<%
	request.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=UTF-8");
    StringBuffer result=new StringBuffer();
    ArrayList<String> strList=new ArrayList<>();
	    if(strResult==""||sqlResult==""){
	        jspContext.setAttribute("naturalResult","未输入查询语句");
	        jspContext.setAttribute("queryResultByNumber","未输入查询语句");
        }
    try {
        // 解析出来的tuple有问题吧
        // 这届返回null了 这样子调试就可以看到问题了 对比一下 前面的那个
        strList=com.tfcat.course.db.Utils.parse("C:\\Users\\tianw\\IdeaProjects\\course(1)\\course\\target\\classes\\dict.txt",strResult,sqlResult);
        //如果想要输入的话，上面有strResult和sqlResult
        // 异常说的是你返回的list为空
            //不管是你输入也好，还是指定字符串，parse这个函数都不能返回任何值，strList永远是null
        jspContext.setAttribute("naturalResult",strList.get(0));
        /*
        * log: 2019年12月17日
        * 问题1：在Utils类中，还不能实现将输入进来的自然语言，进行处理后，改变其中1-2项属性值，来生成衍生的sql语句
        * 问题2：在tag文件中，还不能实现查询多项结果后以form表单的形式呈现。
        * 问题3：表单中的格式有待于完善。
        *   FIXED DATE:2019年12月17日
        * */
        result.append("<table border=1>");
        for(int i=1;i<strList.size();i++){
            result.append("<tr>");
            result.append("<td>"+strList.get(i)+"</td>");
            result.append("</tr>");
        }
        jspContext.setAttribute("queryResultByNumber",new String(result));
    } catch (Exception e) {
        e.printStackTrace();
    }
    /*
    try{
        result.append("<table border=1>");
		String driver = "com.mysql.jdbc.Driver";
        String uri="jdbc:mysql://49.233.197.95:3306/HP171";
        Class.forName(driver);
		con=DriverManager.getConnection(uri,"root","171shujukuxiaozu");
        stmt=con.createStatement();
		stmt2=con.createStatement();

        DatabaseMetaData metadata=con.getMetaData();
        String sql="select * from college where college_name='"+strResult+"'" +"limit 100";
		String sql2="select * from college limit 100";
        rs=stmt.executeQuery(sql);
		rs2=stmt2.executeQuery(sql2);
		
		int k=0;
        while(rs.next()){
			rs2.next();
			k++;
            result.append("<tr>");
            for(int j=1;j<=2;j++){
				if(j==1)
					result.append("<td>"+String.valueOf(k)+"</td>");
				else{
					result.append("<td>"+rs2.getString(2)+"在xxxx年的理科本科批次的分数线xxx"+"</td>");
				}

				bool = "T";
            }
            result.append("</tr>");         
        }
        result.append("</table>");
        rs.close();
        stmt.close();
		stmt2.close();
        con.close();
    }
    catch(SQLException e){
        result.append(e);
    }
	jspContext.setAttribute("naturalResult",strResult+"在xxxx年的理科本科批次的分数线xxx");
	jspContext.setAttribute("getResult",bool);
    jspContext.setAttribute("queryResultByNumber",new String(result));
    */

%>