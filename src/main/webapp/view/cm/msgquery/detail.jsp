<%--
  Created by IntelliJ IDEA.
  User: 小小科学家
  Date: 2016/7/12
  Time: 17:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>详情</title>
    <script src="../view/js/jquery.min.js" type="text/javascript"></script>
    <link rel="shortcut icon" href="">
    <script>
        function post(path, params, method) {
            method = method || "post"; // Set method to post by default if not specified.

            // The rest of this code assumes you are not using a library.
            // It can be made less wordy if you use one.
            var form = document.createElement("form");
            form.setAttribute("method", method);
            form.setAttribute("action", path);
            form.setAttribute("accept-charset","utf-8")

            for(var key in params) {
                if(params.hasOwnProperty(key)) {
                    var hiddenField = document.createElement("input");
                    hiddenField.setAttribute("type", "hidden");
                    hiddenField.setAttribute("name", key);
                    hiddenField.setAttribute("value", params[key]);

                    form.appendChild(hiddenField);
                }
            }

            document.body.appendChild(form);
            form.submit();
        }
        function txtexport(){
            //===================下载文件
            post('${pageContext.request.contextPath}/msgquery/gettxt.json',{detail: $("#downloaddetail").text()});
        }
    </script>
</head>
<body>
<div>
    <%--<a href="${pageContext.request.contextPath}/msgquery/gettxt.json?detail="+encodeURI(encodeURI(_detail))>导出</a>--%>
    <%--<a id="export" href="${pageContext.request.contextPath}/msgquery/gettxt.json">导出</a>--%>
    <button type="button" onclick="txtexport()">导出</button>
</div>

<font id="detail">${messageDetail}</font>
<font id="downloaddetail" style=display:none>${downloadDetail}</font>
<%--<font id="detail">"哈哈哈"</font>--%>
</body>
</html>
