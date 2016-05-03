<%@page import="org.springframework.util.StringUtils"%>
<%@page import="cn.yc.ssh.admin.base.entity.Resource"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Resource r = (Resource)request.getAttribute("resource");

if(r==null){
	out.print("<font color='red'>发生错误：未找到待编辑的资源信息！</font>");
	return;
}
%>
<html>
	<head><meta http-equiv = "X-UA-Compatible" content = "IE=edge,chrome=1" /> 
	    <title></title>
	    

	</head>
	
<body>
	<div id="dlg-toolbar" class="easyui-linkbutton-backcolor" style="padding:2px 0;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" id="saveButton">保存</a>
	</div>
	<form action="<%=basePath%>resource/save" id="saveform" method="post" enctype="multipart/form-data">
    	<input type="hidden" value="${resource.id }" name="id" id="id">
    	<input type="hidden" value="${resource.parentId }" name="parentId" id="parentId">
       <table>
       	<tr><td>名称</td><td><input name="name" value="${resource.name }" id="name" style="width:250px;"> </td></tr>
       	<tr><td>url</td><td><input name="url" value="${resource.url}" id="url" style="width:250px;"></td></tr>
       	<tr><td>权限字符串</td><td><input name="permission" value="${resource.permission}" id="permission" style="width:250px;"></td></tr>
       	<tr><td>类型</td><td>
       		<select name="type" id="type" style="width:250px;">
       			<option value="menu">菜单</option>
       			<option value="button">按钮</option>
       		</select>
       		</td></tr>
       	<tr><td>是否展示在前台</td><td>
       		<select name="showInFront" id="showInFront" style="width:250px;">
       			<option value="1">是</option>
       			<option value="0" selected="selected">否</option>
       		</select>
       		</td></tr>
       		
       	<%
       		if(StringUtils.hasLength(r.getPicName())){
       	 %>
       	 
       	 		<tr><td>图标</td><td>
		       		<img alt="" src="<%=basePath%>resource/uploadPic/showPic?resId=<%=r.getId()%>">
		       		</td></tr>
       	 <%} %>
       	
       </table>
    </form>
	<script type="text/javascript" src="<%=basePath%>static/js/jquery.form.js"></script>
	<script type="text/javascript">
	$(function(){
		$("#type").val("${resource.type}");
		
		$("#showInFront").val('<%=r.getShowInFront()%>');
		$("#saveButton").bind("click",function(){
		
		
			$("#saveform").ajaxSubmit({
				type : "post",
				//dataType:"json",
				success : function(data) {
					alert("保存成功");	
					$("#dlg").dialog("close");
				}
			})
		})
	})
		
	</script>


</body>

</html>