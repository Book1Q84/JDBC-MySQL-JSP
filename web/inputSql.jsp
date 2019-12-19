<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="strPost"%>


<link rel="stylesheet" href="./css/css1.css" type="text/css">

<html>
<body>

<%
	request.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=UTF-8");
%>
<form action="inputSql.jsp" method=post>
	<title>高考分数查询数据库系统</title>
	<br><h2>高考分数查询数据库系统</h2>
	<br><h4>支持查询近三年各省份、各学校、各专业的分数线！</h4>

	<br><br>
	<p class="style_1">
		请输入自然语言:
		<br><br>
		<input type="text" name="str" placeholder="在此输入自然语言" style="width:700px;height:30px " value="<%=request.getParameter("str")==null?"":request.getParameter("str")%>">

		<br><br>
		请输入sql语句:
		<br><br>
		<input type="text" name="sql" placeholder="在此输入sql语句"  style="width:700px;height:30px " value="<%=request.getParameter("sql")==null?"":request.getParameter("sql")%>">

		<br><br>
		<input type="submit" name="sub" value="提交" id ="sub">
	</p>
</form>

<%
	String str=request.getParameter("str");
	String sql=request.getParameter("sql");
%>
<strPost:NumberCondition strResult="<%=str%>" sqlResult="<%=sql%>"/>
<p class="style_2">
	<%--<%if(getResult.equals("T")==true){%>--%>
	自然表达结果：
</p>
<p class="style_3">
	<br><br><%=naturalResult%>
</p>
<p class="style_4">
	<br><br>
	衍生问题结果：
</p>
<p class="style_5">
	<br><br><%=queryResultByNumber%>
	<%--<%}else{%>
	<br><br>没有查询到结果！
	<%}%>--%>
</p>

</body>
</html>

