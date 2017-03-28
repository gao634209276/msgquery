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
    <link href="${pageContext.request.contextPath}/view/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <%--<script type="text/javascript" src="${pageContext.request.contextPath}/view/js/jquery-2.1.1.min.js"></script>--%>

    <script type="text/javascript" src="${pageContext.request.contextPath}/view/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../view/js/jquery.min.js"></script>
    <%--<link href="../view/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>--%>
    <%--<link type="text/css" href="http://code.jquery.com/ui/1.9.1/themes/smoothness/jquery-ui.css" rel="stylesheet" />--%>
    <link href="../view/css/jquery-ui.css" rel="stylesheet" type="text/css" />
    <link rel="shortcut icon" href="">
    <link rel="stylesheet" href="../view/css/combo.select.css">
    <link href="../view/css/jquery-ui-timepicker-addon.css" type="text/css" />
    <link href="../view/css/demos.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="../view/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="../view/js/knockout.js"></script>
    <script type="text/javascript" src="../view/js/jquery-ui.js"></script>
    <script src="../view/js/jquery.combo.select.js"></script>
    <script src="../view/js/jquery-ui-timepicker-addon.js" type="text/javascript"></script>
    <!--中文-->
    <script src="../view/js/jquery.ui.datepicker-zh-CN.js.js" type="text/javascript"></script>
    <script src="../view/js/jquery-ui-timepicker-zh-CN.js" type="text/javascript"></script>

    <script type="text/javascript">
        $(function () {
            // 时间设置
            $('#startTime').datetimepicker({
                timeFormat: "HH:mm:ss",
                dateFormat: "yy-mm-dd"
            });
            $('#endTime').datetimepicker({
                timeFormat: "HH:mm:ss",
                dateFormat: "yy-mm-dd"
            });
        });
    </script>

    <style>
        .ui-widget {
            font-family: Verdana,Arial,sans-serif/*{ffDefault}*/;
            font-size: 1.3rem/*{fsDefault}*/;
        }

    </style>

    <style>
        body{
            cursor:pointer;
        }
        .combo-input{
            width: 100%;
            font-size:1.1rem;
        }
        .combo-dropdown li {
            font-size: 1.2rem;
            list-style: outside none none;
            margin: 0;
            padding: 8px 1em;
        }
    </style>
</head>
<body >

<div class="form-inline" role="form" id="frm1">
    <div class="form-group">
        <div class="input-group">
            <span class="input-group-addon">起始时间：</span>
        <input type="text" id="startTime" class="form-control">
        </div>
        <div class="input-group">
            <span class="input-group-addon">结束时间：</span>
            <input type="text" id="endTime" class="form-control">
            </div>
        <div class="input-group">
            <span class="input-group-addon">渠道：</span>
                <select id="channel" class="form-control" name="channelName" onchange="change();" style="height: 30px;padding:0 12px";>
            <%--<option value="">请选择渠道</option>--%>
            <%--<option value="web mob sms mobwap mobstandard">全部</option>--%>
            <option value="web">网厅</option>
            <option value="mob">手厅客户端</option>
            <%--<option value="sms">短厅</option>--%>
            <option value="mobwap">手厅wap版</option>
            <option value="mobstandard">手厅标准版</option>
            </select>
        </div>

        <div class="input-group" id="interdiv">
       <span class="input-group-addon">业务名称：</span>
                <select id="inter"  class="form-control" name="interName" style="padding: 0;">
                    <option value="all">全部</option>
                    <c:forEach var="ainter" items="${inters}">
                        <c:choose>
                            <c:when test="${not empty interName and ainter eq interName}">
                                <option value="${ainter}" selected="selected">${ainter}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${ainter}">${ainter}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
            </select>
        </div>

        <div class="input-group">
            <span class="input-group-addon">环境选择：</span>
            <select id="huanjing" class="form-control" name="huanjing" style="height: 30px;padding:0 12px";>
                <%--<option value="">请选择渠道</option>--%>
                    <option value="product">生产环境</option>
                <option value="test">测试环境</option>
                <option value="pre">预发布环境</option>
            </select>
        </div>

        <div class="input-group">
            <span class="input-group-addon">关键字：</span>
        <input type="text" id="keywords"  class="form-control">
            </div>
    </div>
    <input id="tablesize" type="hidden"/>
    <input type="hidden" name="pageNo" id="pageNo" value="1"/>
    <input id="querybtn" type="submit" class="btn btn-default" value="查询"/>
</div>
<table class="table table-bordered table-hover" id="mytb" style="font-size: 1.4rem;font-size: 12px">
    <caption></caption>
    <thead>
    <tr>
        <th>报文时间</th>
        <th>渠道</th>
        <th>手机号</th>
        <th>业务编码</th>
        <th>业务名称</th>
        <th>流水号</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody id="tbody1">
    <!-- ko foreach:dataList -->
    <tr data-target="#msgtrackInfo" data-toggle="modal" >
        <td data-bind="text:timestamp" class="errorTime">&nbsp;</td>
        <td data-bind="text:type">&nbsp;</td>
        <td data-bind="text:mobile">&nbsp;</td>
        <td data-bind="text:intercode">&nbsp;</td>
        <td data-bind="text:inter">&nbsp;</td>
        <td data-bind="text:transid">&nbsp;</td>
        <td><button data-bind="click:function(){showDetail(this);}" class="btn btn-link">详情</button></td>
        <td data-bind="text:flag" style="display:none">&nbsp;</td>
        <td data-bind="text:detailmessage" style="display:none">&nbsp;</td>
        <td data-bind="text:message" style="display:none" class="highlight">&nbsp;</td>
        <td data-bind="text:serverip" style="display:none">&nbsp;</td>
        <td data-bind="text:path" style="display:none">&nbsp;</td>
        <td data-bind="text:smstype" style="display:none">&nbsp;</td>
    </tr>
    <!-- /ko -->
    </tbody>
</table>
    <p style="float: right; margin-right: 25px;  font-size: 15px;" id="pageP">
        <span id="pages"></span>页&nbsp;&nbsp;&nbsp;&nbsp;当前第<span id="pageNum"></span>页&nbsp;&nbsp;&nbsp;&nbsp;
        <a id="firstp" onclick="changePage(010086)">首页</a>&nbsp;&nbsp;
        <a id="prevp" onclick="changePage(01)">上一页</a>&nbsp;&nbsp;
        跳转至：<input type="number" id="goPage" style="width: 60px">
        <button id="gogo" onclick="goPageTo()">go</button>
        <a id="nextp" onclick="changePage(02)">下一页</a>&nbsp;&nbsp;
        <a id="lastp" onclick="changePage(03)">最后一页</a>&nbsp;&nbsp;
    </p>
<font style="font-size: 1rem ">由于某些原因我们过滤掉了部分报文，最后一页可能无数据展示</font>
<script type="text/javascript">
    function change() {
        if($("#channel").val()=="sms") {
//            alert("111");
         $("#interdiv").hide();
        }else{
            $("#interdiv").show();
        }
    }

    function goPageTo() {
        var goPage=$("#goPage").val();
        if(parseInt(goPage)<1) {
            alert("请输入合适的页数");
        }else if (parseInt(goPage)>parseInt($("#pages").text())) {
            alert("请输入合适的页数");
        }
        else {
            $("#pageNo").val(goPage);
            getTableData();
        }
    }
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
        }else if(pageNo == "010086"){
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
            data:{"transid":obj.transid,
                     "type":obj.type,
                    "smstype":obj.smstype,
                     'startDay': $("#startTime").val(),
                     'endDay': $("#endTime").val(),
                    "keywords":$("#keywords").val(),
                     "hightlight":$(".highlight").eq(0).text(),
                    "channels":$("#channel").val(),
                "huanjing":$("#huanjing").val(),
            },
            success: function(str_response) {
                var obj = window.open("about:blank");
                obj.document.write(str_response);
            }
        });
    }

    function getTableData(flag) {
            if($('#inter').siblings('input').val()==null||$('#inter').siblings('input').val().trim()=="") {
                var intervalue="all";
            }else{
                var intervalue=$("#inter").val();
            }

//        $("#startTime").val()==""||$("#endTime").val()==""||
            if ($("#keywords").val() == "") {
                alert("关键字必须填写");
                $('#querybtn').removeAttr("disabled");
                return;
            }
            if(!($("#startTime").val()==""||$("#endTime").val()=="")) {
                if($("#startTime").val()>$("#endTime").val()) {
                    alert("起始时间不能大于结束时间");
                    $('#querybtn').removeAttr("disabled");
                    return;
                }
            }

            $.ajax({
                type: "post",
                url: "${pageContext.request.contextPath}/msgquery/messageQuery.json",
                data: {
                    'startDay': $("#startTime").val(),
                    'endDay': $("#endTime").val(),
                    "pageNum": $("#pageNo").val(),
                    "keywords":$("#keywords").val(),
                    "channel":$("#channel").val(),
                    "huanjing":$("#huanjing").val(),
                    "inter":intervalue,
                },
                success: function (result) {
                    viewModel.dataList.removeAll();
                    if (result.error == 1||result=="") {
                        $("#pageP").hide();
                        $('#querybtn').removeAttr("disabled");
//                        alert(result.errordetail);
                        alert("暂无 数据");
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
                        $('#querybtn').removeAttr("disabled");
                        $("#tablesize").val(result.tablesize);
//                        sethighlight();
                    }
                }
            });
    }
</script>

<script type="text/javascript">
    var timeTicket;
    var viewModel=null;

    function initViewModel(){
        viewModel= {
            dataList: ko.observableArray()
        }
        ko.applyBindings(viewModel);
    }
    //当点击查询的时候
    $(document).ready(function() {


        $('#inter').comboSelect();
        //隐藏p标签
        $("#pageP").hide();
        initViewModel();
        $("#querybtn").click(function () {
//            $("#querybtn").attr({"disabled":"disabled"});

            //时间判断
//            var checkStartTime=$("#startTime").val();
//            var checkEndTime=$("#endTime").val();
            if(!($("#startTime").val()=="")) {
                str = $("#startTime").val().replace(/-/g,"/");
                var checkStartTime = new Date(str );
                var currTime = new Date();
                var myDate = new Date(); //获取今天日期
                if(checkStartTime>myDate) {
                    alert("起始时间不能大于当前时间");
                    return;
                }
            }
            if(!($("#endTime").val()=="")) {
                str1 = $("#endTime").val().replace(/-/g,"/");
                var checkEndTime = new Date(str1);
                var currTime = new Date();
                var myDate = new Date(); //获取今天日期
                if(checkEndTime>myDate) {
                    alert("结束时间不能大于当前时间");
                    return;
                }
            }
//            $("#querybtn").attr({"disabled":"disabled"});
//            document.getElementById("querybtn").setAttribute("disabled","");
            var flag=2;
            document.getElementById("querybtn").setAttribute("disabled","disabled");
            getTableData();
//            $("#querybtn").removeAttr("disabled");
            $("#pageNo").val(1);
//            $('#querybtn').removeAttr("disabled");
        })
    })
</script>
    <br/>
    <a href="${pageContext.request.contextPath}/editindex/openorclose.htm" target="_blank">操作索引</a>
    <br />
</body>
</html>
