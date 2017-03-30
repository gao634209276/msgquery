<%--
  Created by IntelliJ IDEA.
  User: 小小科学家
  Date: 2016/7/11
  Time: 10:57
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>报文查询</title>
    <%@ include file="/view/include/head.jsp"%>
    <link href="../view/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link type="text/css" href="http://code.jquery.com/ui/1.9.1/themes/smoothness/jquery-ui.css" rel="stylesheet" />

    <link href="../view/css/jquery-ui-timepicker-addon.css" type="text/css" />
    <link href="../view/css/demos.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="../view/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../view/js/knockout.js"></script>
    <script src="../view/js/jquery-2.1.1.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="../view/js/jquery-ui.js"></script>
    <script src="../view/js/jquery-ui-timepicker-addon.js" type="text/javascript"></script>

    <!--中文-->
    <script src="../view/js/jquery.ui.datepicker-zh-CN.js.js" type="text/javascript"></script>
    <script src="../view/js/jquery-ui-timepicker-zh-CN.js" type="text/javascript"></script>
    <script type="text/javascript">
        jQuery(function () {
            // 时间设置
            jQuery('#startTime').datetimepicker({
                minDate:"-1W",
                maxDate:0,
                timeFormat: "HH:mm:ss",
                dateFormat: "yy-mm-dd"
            });
            jQuery('#endTime').datetimepicker({
                minDate:"-1W",
                maxDate:0,
                timeFormat: "HH:mm:ss",
                dateFormat: "yy-mm-dd"
            });
        });
    </script>
    <style>
        body{
            cursor:pointer;
        }
    </style>
</head>
<body>
<div class="form-inline" role="form" id="frm1">
    <div class="form-group">
        起始时间：<input type="text" id="startTime">
        结束时间：<input type="text" id="endTime">
        渠道: <select id="channel" class="form-control" name="channelName" style="height: 30px";>
        <option value="">请选择渠道</option>
        <option value="web mob sms">全部</option>
        <option value="web">网厅</option>
        <option value="mob">手厅</option>
        <option value="sms">短厅</option>
    </select>
        关键字：<input type="text" id="keywords">
    </div>
    <input id="tablesize" type="hidden"/>
    <input type="hidden" name="pageNo" id="pageNo" value="1"/>
    <input id="querybtn" type="submit" class="btn btn-default" value="查询"/>
</div>
<table class="table table-bordered table-hover" id="mytb">
    <caption></caption>
    <thead>
    <tr>
        <th>报文时间</th>
        <th>渠道</th>
        <th>报文</th>
        <th>服务器</th>
        <th>节点</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody id="tbody1">
    <!-- ko foreach:dataList -->
    <tr data-target="#msgtrackInfo" data-toggle="modal" >
        <td data-bind="text:flag" style="display:none">&nbsp;</td>
        <td data-bind="text:timestamp" class="errorTime">&nbsp;</td>
        <td data-bind="text:type">&nbsp;</td>
        <td data-bind="text:message" class="highlight">&nbsp;</td>
        <td data-bind="text:host">&nbsp;</td>
        <td data-bind="text:path">&nbsp;</td>
        <td><button data-bind="click:function(){showDetail(this);}" class="btn btn-link">详情</button></td>
    </tr>
    <!-- /ko -->
    </tbody>
</table>
<p style="float: right; margin-right: 25px;" id="pageP">
    共<span id="rows"></span>条&nbsp;&nbsp;&nbsp;&nbsp;<span id="pages"></span>页&nbsp;&nbsp;&nbsp;&nbsp;当前第<span id="pageNum"></span>页&nbsp;&nbsp;&nbsp;&nbsp;
    <a id="firstp" onclick="changePage(10086)">首页</a>&nbsp;&nbsp;
    <a id="prevp" onclick="changePage(01)">上一页</a>&nbsp;&nbsp;
    <a id="nextp" onclick="changePage(02)">下一页</a>&nbsp;&nbsp;
    <a id="lastp" onclick="changePage(03)">最后一页</a>&nbsp;&nbsp;
</p>

<script type="text/javascript">
    function changePage(pageNo){
        if(pageNo == "01"){
            $("#prevp").css("color","red");
            if(parseInt($("#pageNo").val())-1<=0) {
                alert("当前页为首页！");
            }else{
                $("#pageNo").val(parseInt($("#pageNo").val())-1);
            }
        }else if(pageNo == "02"){
            $("#nextp").css("color","red");
            if(parseInt($("#pageNo").val())+1>$("#pages").text()) {
                alert("当前页为最后一页!");
            }else{
                $("#pageNo").val(parseInt($("#pageNo").val())+1);
            }
        }else if(pageNo == "10086"){
            $("#firstp").css("color","red");
            $("#pageNo").val(1);
        }else if(pageNo == "03"){
            $("#lastp").css("color","red");
            $("#pageNo").val($("#pages").text());
        }
        getTableData();
    }

    function showDetail(obj) {
//$.post("${pageContext.request.contextPath}/echarts/errorDetail.htm",{'IF_BIZNAME':$("#interName").val(),'APPLICATION_KEY':$("#channel").val(),"date":obj.innerHTML})
        <%--window.open("${pageContext.request.contextPath}/msgquery/messageDetail.htm?message="+obj.message);--%>
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/msgquery/messageDetail.htm",
            data:{"message":obj.message},
            success: function(str_response) {
                var obj = window.open("about:blank");
                obj.document.write(str_response);
            }
        });
    }

    function getTableData() {
        if($("#startTime").val()==""||$("#endTime").val()==""||$("#keywords").val()==""||$("#channel").val()==""||($("#startTime").val()>$("#endTime").val())){
            alert("请确认时间、关键字、渠道填写正确");
            return;
        }
        $.ajax({
            type: "post",
            url: "${pageContext.request.contextPath}/msgquery/messageQuery.json",
            data: {
                'startDay': $("#startTime").val(),
                'endDay': $("#endTime").val(),
                "pageNum": $("#pageNo").val(),
                "keywords":$("#keywords").val(),
                "channels":$("#channel").val()
            },
            success: function (result) {

                viewModel.dataList.removeAll();
                if (result == "") {
                    $("#pageP").hide();
                    alert("数据查询失败")
                } else {
                    for (var i = 0; i < result.listPage.length; i++) {
                        viewModel.dataList.push(result.listPage[i]);
                    }
                    $("#prevp").css("color", "#23527c");
                    $("#nextp").css("color", "#23527c");
                    $("#firstp").css("color", "#23527c");
                    $("#lastp").css("color", "#23527c");
                    $("#rows").text(result.rows);
                    $("#pages").text(result.maxPage);
                    $("#pageNum").text(result.pageNo);
//                    initViewModel();
                    if (result.rows > 0) {
                        $("#pageP").show();
                    } else {
                        $("#pageP").hide();
                        alert("暂无数据！");
                    }
                    $("#tablesize").val(result.tablesize);
                    sethighlight();
                }
            }
        });
    }
</script>

<script type="text/javascript">
    var timeTicket;
    var viewModel=null;
    //高亮
    function sethighlight() {
        var key=$("#keywords").val().split(' ')[0];
        for(var i=0;i<$("#tablesize").val();i++){
            var myhighlight = $(".highlight").eq(i).text();
            var lzfhighlight = myhighlight.replace("\n","");
            var startindex = myhighlight.indexOf(key);
            if(startindex>24&& myhighlight.length-(startindex+key.length)>24) {
                var prefixStr=myhighlight.substring(startindex-24,startindex);
                var suffixStr=myhighlight.substring(startindex+key.length,startindex+key.length+24);
                myhighlight = prefixStr + key + suffixStr;
            }else if (startindex<=24&&myhighlight.length-(startindex+key.length)>24) {
                var prefixStr = myhighlight.substring(0, startindex);
                var suffixStr = myhighlight.substring(startindex + key.length, startindex + key.length + 24);
                myhighlight = prefixStr + key + suffixStr;
            }else if(startindex>24&&myhighlight.length-(startindex+key.length)<=24){
                var prefixStr=myhighlight.substring(startindex-24,startindex);
                var suffixStr=myhighlight.substring(startindex+key.length);
                myhighlight = prefixStr + key + suffixStr;
            }
            myhighlight = myhighlight.replace(/</g, "&lt;").replace(/>/g, "&gt;");
            var mmhighlight = myhighlight.replace(key, '<font color=red>' + key + '</font>');
            $(".highlight").eq(i).html(mmhighlight);
        }
    }

    function initViewModel(){
        viewModel= {
            dataList: ko.observableArray()
        }
        ko.applyBindings(viewModel);
    }
    //当点击查询的时候
    $(document).ready(function() {
        //隐藏p标签
        $("#pageP").hide();
        initViewModel();
        $("#querybtn").click(function () {
            getTableData();
            $("#pageNo").val(1);
        })

    })
</script>
</body>
</html>
