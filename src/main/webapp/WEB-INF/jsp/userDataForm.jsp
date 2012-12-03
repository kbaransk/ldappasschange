<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spr-s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spr-f" uri="http://www.springframework.org/tags/form"%>

<html>
    <head>
        <title>Spring MVC Example</title>
    </head>
    <body>
        <h2>Welcome to the Example Spring MVC page</h2>
        <h3>The message text is:</h3>
        <p>${message}</p>
        <spr-f:form id="form" name="form" action="updateUserPassword.htm">
            <spr-f:label path="upc.username">Username:</spr-f:label>
            <spr-f:input path="upc.username"/>
            <spr-f:errors path="upc.username"/>
        </spr-f:form>
    </body>
</html>