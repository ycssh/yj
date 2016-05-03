<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head><meta http-equiv = "X-UA-Compatible" content = "IE=edge,chrome=1" /> 
<base href="<%=basePath%>">

<title>短训班/团青培训工作量统计</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<style type="text/css">
table td{
	font-size: 12px;
}
</style>
<link rel="stylesheet" type="text/css"
	href="<%=basePath %>static/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=basePath %>static/easyui/themes/icon.css">
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/easyui/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/common.js"></script>

<script type="text/javascript">
$(function(){

	initGrid();
	
	$("#search").bind("click",doSearch);
	$("#s_reset").bind("click",resetSearchBox);
	//导出
	  $("#export").bind("click",exportGZL);

});

function initGrid(){
	
	$('#mainGrid').datagrid({
	    url:'gzltj/showFindAll.do',
	    toolbar:"#toolbar",
	    queryParams:{
	    	filter:"type=1"
	    },
	    columns:[[
	    	{field:'deptId',title:'专业培训部',width:40},
	        {field:'projectName',title:'项目名称',width:80},
	        {field:'teacherTypeId',title:'教师类型',width:40},
	        {field:'teacherId',title:'教师姓名',width:40},
	        {field:'courseName',title:'课程类型',width:40},
	        {field:'gzl',title:'工作量',width:40}
	    ]],
	    singleSelect:false,
	    autoRowHeight:true,
		fitColumns : true,
	    pagination:true,
	    pageSize:20
	});
}

function doSearch(){
	
	var deptId = $.trim($("#s_deptId").combobox('getValue'));
	var teacherTypeId = $.trim($("#s_teacherTypeId").combobox('getValue'));
	var teacherId = $.trim($("#s_teacherId").val());
	var courseId = $.trim($("#s_courseId").combobox('getValue'));
	var startTime = $.trim($("#s_startTime").combobox('getValue'));
	var endTime = $.trim($("#s_endTime").combobox('getValue'));
	if(top.specialCharacter(teacherId))
	{
		alert("教师姓名中包含特殊字符，请重新输入.");
		return;
	}
	if(startTime>endTime){
		alert("开始时间不能大于结束时间");
		return ;
	}	
	$("#mainGrid").datagrid("load",{
		filter:"type=1&deptId="+deptId+"&teacherTypeId="+teacherTypeId+"&teacherId="+teacherId+"&courseId="+courseId+"&startTime="+startTime+"&endTime="+endTime
	});
}

function exportGZL(){
	var deptId = $.trim($("#s_deptId").combobox('getValue'));
	var teacherTypeId = $.trim($("#s_teacherTypeId").combobox('getValue'));
	var teacherId = $.trim($("#s_teacherId").val());
	var courseId = $.trim($("#s_courseId").combobox('getValue'));
	var startTime = $.trim($("#s_startTime").combobox('getValue'));
	var endTime = $.trim($("#s_endTime").combobox('getValue'));
	
	if(startTime>endTime){
		alert("开始时间不能大于结束时间");
		return ;
	}
	 var filter ="type=1&deptId="+deptId+"&teacherTypeId="+teacherTypeId+"&teacherId="+teacherId+"&courseId="+courseId+"&startTime="+startTime+"&endTime="+endTime;
	     filter = encodeURI(encodeURI(filter));
	 var path="gzltj/exportGzl_S?filter="+filter;
	 window.open(path);
}

function resetSearchBox(){
	
	$("#s_deptId").combobox('setValue',null);
	$("#s_teacherTypeId").combobox('setValue',null);
	$("#s_courseId").combobox('setValue',null);
	$("#s_teacherId").val('');
	$("#s_startTime").datebox('setValue',null);
	$("#s_endTime").datebox('setValue',null);
	
}


</script>
</head>

<body>

	<!-- Grid -->
	<div id="toolbar">
		<!-- <div>
			<div id="export" class="easyui-linkbutton"
				data-options="iconCls:'icon-ok',plain:true">导出</div>
		</div> -->
		<div>
			<table id="searchTable">
				<tr>
					<td>专业培训部：</td>
					<td><input id="s_deptId" name="s_deptId"  style="width: 150px" class="easyui-combobox" data-options="
        editable:false,valueField: 'value',
        textField: 'name',
        url: 'zbDict/findByType.do?type=dept',
        onLoadSuccess:function(rec){
        	$('#s_'+this.id).combobox('loadData',rec);
        }"></td>
					<td >教师类型：</td>
					<td><input id="s_teacherTypeId" name="s_teacherTypeId"  style="width: 150px" class="easyui-combobox" data-options="
        editable:false,valueField: 'name',
        textField: 'name',
        data:[{name:'专职培训师'},{name:'兼职培训师'}]"></td>
       				<td>教师姓名：</td>
					<td><input id="s_teacherId" name="s_teacherId" class="easyui-validatebox" style="width: 150px" ></td>
					
				</tr>
				<tr>
					<td>课程类型：</td>
					<td><input id="s_courseId" name="s_courseId"  style="width: 150px" class="easyui-combobox" data-options="
        editable:false,valueField: 'name',
        textField: 'name',
        data:[{name:'理论'},{name:'实训'}],
        onLoadSuccess:function(rec){
        }">
					</td>
					<td >开始时间：</td>
					<td ><input id="s_startTime" name="s_startTime" class="easyui-datebox" data-options="editable:false,formatter:function(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	if(m<10){m = '0'+m;}
	if(d<10){d = '0'+d;}
	return y+'-'+m+'-'+d;
}" style="width: 150px" > </td>
					<td >结束时间：</td>
					<td ><input id="s_endTime" name="s_endTime" class="easyui-datebox" data-options="editable:false,formatter:function(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	if(m<10){m = '0'+m;}
	if(d<10){d = '0'+d;}
	return y+'-'+m+'-'+d;
}" style="width: 150px" > <a id="search" href="#" class="easyui-linkbutton" iconCls="icon-search">检索</a>
					<a id="s_reset" href="#" class="easyui-linkbutton" iconCls="icon-reset">重置</a>
					<a id="export" href="#" class="easyui-linkbutton" iconCls="icon-template">导出</a></td>
				</tr>
			</table>
			
		</div>
		
	</div>
	<table id="mainGrid"></table>
	
	
</body>
</html>
