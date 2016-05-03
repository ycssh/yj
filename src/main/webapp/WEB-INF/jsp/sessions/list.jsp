<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String version="1.3.5";
String contextPath = request.getContextPath();
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="ycfn" uri="/madmin-functions" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
<title>用户监测</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv = "X-UA-Compatible" content = "IE=edge,chrome=1" /><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" /> 
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="easyui">
<meta http-equiv="description" content="easyui">

<link rel="stylesheet" type="text/css" href="<%=basePath%>static/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>static/easyui/themes/icon.css">
<script type="text/javascript" src="<%=basePath%>static/js/jquery-1.11.0.min.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=basePath%>static/easyui/jquery.easyui.min.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=basePath%>static/js/system.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=basePath%>static/easyui/easyui-lang-zh_CN.js" charset="utf-8"></script>
		
</head>
<body>

	<c:if test="${not empty msg}">
    <div  style="padding-top:8px;padding-left:3px;font-weight: bolder;font-size: 15.5px;color:red;">${msg}</div>
	</c:if>
	
	<table id="dg" class="easyui-datagrid" title="当前在线人数：${sessionCount}人" style="width:700px;height:250px" 
											data-options="singleSelect:true,collapsible:true">
		<thead>
			<tr>				
				<th data-options="field:'1',align:'center',resizable:false" width="25%">会话ID</th>
				<th data-options="field:'2',align:'center',resizable:false" width="15%">用户名</th>
				<th data-options="field:'3',align:'center',resizable:false" width="15%">主机地址</th>
				<th data-options="field:'4',align:'center',resizable:false" width="15%">最后访问时间</th>
				<th data-options="field:'5',align:'center',resizable:false" width="15%">已强制退出</th>
				<th data-options="field:'6',align:'center',resizable:false" width="15%">操作</th>
			</tr>
		</thead>
		
		<tbody>
        <c:forEach items="${sessions}" var="session">
            <tr>
                <td>${session.id}</td>
                <td>${ycfn:principal(session)}</td>
                <td>${session.host}</td>
                <td><fmt:formatDate value="${session.lastAccessTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>${ycfn:isForceLogout(session) ? '是' : '否'}</td>
                <td>
                    <c:if test="${not ycfn:isForceLogout(session)}">
                        <a href="${pageContext.request.contextPath}/sessions/${session.id}/forceLogout">强制退出</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </tbody>		
	</table>

</body>
</html>