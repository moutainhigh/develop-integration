<html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath(); 
 	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>高级查询</title>
</head>
<body>
	<h5 align="center">&nbsp;</h5>
	<div id="term" align="center">
		<div>
			<select class="chooseClass" style="width: 22.3%;height: 23px">
			</select>
			<button type="button" id="submitMulti" style="width: 48px">搜索</button>
		</div>
		<div>
			<select class="attributeClass" style="width: 8%;height: 23px">
			</select>
			<input class="qual" type="text" style="width: 14%"  onkeydown="EnterPressMulti()" >
			<button type="button" class="addDiv" style="width: 22px">+</button>
			<button type="button" class="deleteDiv" style="width: 22px">-</button>
		</div>
	</div>
	<br/>
	<div id="resultsMulti" align="center">
		
	</div>
	<br/>
	<div id="buttonArea" align="center">
		<input id="nextMulti" style="display: none;" value="下一页" type="button"/>
	</div>
<!-- 点击搜索后的背景显示 -->
<div id="background" class="all_backgroundcolor" align="center">
		<img class="background_img"  src="<%=basePath %>/img/backgroundcenter.gif">
	</div>	
<script type="text/javascript" src="<%=basePath %>/js/modules/multi/multi.js"></script>
</body>
</html>