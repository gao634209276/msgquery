<%--
  Created by IntelliJ IDEA.
  User: 小小科学家
  Date: 2016/12/26
  Time: 15:54
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>索引管理</title>
    <%@ include file="/view/include/head.jsp" %>
    <link href="${pageContext.request.contextPath}/view/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <%--<script type="text/javascript" src="${pageContext.request.contextPath}/view/js/jquery-2.1.1.min.js"></script>--%>

    <script type="text/javascript" src="${pageContext.request.contextPath}/view/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../view/js/jquery.min.js"></script>
    <%--<link href="../view/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>--%>
    <%--<link type="text/css" href="http://code.jquery.com/ui/1.9.1/themes/smoothness/jquery-ui.css" rel="stylesheet" />--%>
    <link href="../view/css/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <link rel="shortcut icon" href="">
    <%--<link rel="stylesheet" href="../view/css/combo.select.css">--%>
    <%--<link href="../view/css/jquery-ui-timepicker-addon.css" type="text/css" />--%>
    <%--<link href="../view/css/demos.css" rel="stylesheet" type="text/css" />--%>
    <script type="text/javascript" src="../view/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../view/js/knockout.js"></script>
    <script type="text/javascript" src="../view/js/jquery-ui.js"></script>
    <script src="../view/js/jquery.combo.select.js"></script>
    <script src="../view/js/laydate/laydate.js"></script>

    <%--<script src="../view/js/jquery-ui-timepicker-addon.js" type="text/javascript"></script>--%>
    <%--<!--中文-->--%>
    <%--<script src="../view/js/jquery.ui.datepicker-zh-CN.js.js" type="text/javascript"></script>--%>
    <%--<script src="../view/js/jquery-ui-timepicker-zh-CN.js" type="text/javascript"></script>--%>
</head>
<body>
<h2>临时开启索引</h2>
<div class="form-inline" role="form" id="frm1">
    <div class="form-group">
        <div class="input-group">
            <span class="input-group-addon">选择日期：</span>
            <input class="laydate-icon" id="thisDate" onclick="laydate()" width="">
        </div>
        <div class="input-group">
            <span class="input-group-addon">选择数据来源：</span>
            <select id="channel" class="form-control" name="channelName" onchange="change();"
                    style="height: 30px;padding:0 12px" ;>
                <%--<option value="">请选择渠道</option>--%>
                <%--<option value="web mob sms mobwap mobstandard">全部</option>--%>
                <option value="web">网厅</option>
                <option value="mob">手厅</option>
                <option value="smsm">短厅</option>
            </select>
        </div>

        <div class="input-group">
            <span class="input-group-addon">环境选择：</span>
            <select id="huanjing" class="form-control" name="huanjing" style="height: 30px;padding:0 12px" ;>
                <%--<option value="">请选择渠道</option>--%>
                <option value="product">生产</option>
                <option value="pre">预发布环境</option>
            </select>
        </div>
    </div>
    <input id="openIndex" onclick="openIndex()" class="btn btn-default" value="打开"/>
    <input id="closeIndex" onclick="closeIndex()" class="btn btn-default" value="关闭"/>
</div>
</body>
<script type="text/javascript">
    function openIndex() {
        document.getElementById("openIndex").setAttribute("disabled", "disabled");
        document.getElementById("closeIndex").setAttribute("disabled", "disabled");
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/editindex/openIndex.json",
            data: {
                "thisDate": $("#thisDate").val(),
                "channels": $("#channel").val(),
                "huanjing": $("#huanjing").val(),
            },
            success: function (result) {
                if (result == "") {
                    alert("异常");
                } else if (result.code == "000") {
                    alert("该索引不存在！");
                }
                else if (result.code == "0001") {
                    alert("操作成功");
                } else if (result.code == "0002") {
                    alert("操作失败");
                } else if (result.code == "0003") {
                    alert("测试集群");
                }
                $('#openIndex').removeAttr("disabled");
                $('#closeIndex').removeAttr("disabled");
//                var obj = window.open("about:blank");
//                obj.document.write(str_response);
            }
        });
    }

    function closeIndex() {
        document.getElementById("openIndex").setAttribute("disabled", "disabled");
        document.getElementById("closeIndex").setAttribute("disabled", "disabled");
//        $('#openIndex').removeAttr("disabled");
//        $('#closeIndex').removeAttr("disabled");
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/editindex/closeIndex.json",
            data: {
                "thisDate": $("#thisDate").val(),
                "channels": $("#channel").val(),
                "huanjing": $("#huanjing").val(),
            },
            success: function (result) {
                if (result == "") {
                    alert("异常");
                } else if (result.code == "000") {
                    alert("该索引不存在或已关闭！");
                }
                else if (result.code == "0001") {
                    alert("操作成功");
                } else if (result.code == "0002") {
                    alert("操作失败");
                } else if (result.code == "0003") {
                    alert("测试集群");
                }
                else if (result.code == "9999") {
                    alert("不允许关闭7天之内的索引！")
                }
                $('#openIndex').removeAttr("disabled");
                $('#closeIndex').removeAttr("disabled");
//                var obj = window.open("about:blank");
//                obj.document.write(str_response);
            }
        });
    }

</script>
</html>
