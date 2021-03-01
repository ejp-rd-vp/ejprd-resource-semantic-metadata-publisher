<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: dipo
  Date: 17/02/2021
  Time: 08:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<fieldset><legend><em>Direct Input</em></legend>
<textarea  style=\"margin:5px; width: 790px; height: 300px;\name=\"\\&quot;fileInJson\\&quot;\">
       <%=request.getAttribute("name") %>
</textarea>
</fieldset>

</body>
</html>
