<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spr-s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spr-f" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>LDAP Password</title>
        <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
        <link href="bootstrap/css/bootstrap.css" rel="stylesheet"/>
    </head>
    <body>
        <h2>Zmień hasło w LDAP</h2>

        <spr-s:hasBindErrors name="upc">
            <c:forEach items="${errors.globalErrors}" var="errorMessage">
                <div class="alert alert-error">
                    <c:out value="${errorMessage.defaultMessage}" />
                </div>
            </c:forEach>
        </spr-s:hasBindErrors>

        <spr-f:form id="form" name="form" commandName="upc" method="POST" title="testowy formularz" cssClass="form-horizontal">
            <div class="control-group">
                <spr-f:label path="username" cssClass="control-label">Username:</spr-f:label>
                <div class="controls">
                    <spr-f:input path="username"/>
                    <spr-f:errors path="username"/>
                </div>
            </div>

            <div class="control-group">
                <spr-f:label path="oldPassword" cssClass="control-label">Old password:</spr-f:label>
                <div class="controls">
                    <spr-f:password path="oldPassword"/>
                    <spr-f:errors path="oldPassword"/>
                </div>
            </div>

            <div class="control-group">
                <spr-f:label path="password" cssClass="control-label">Password:</spr-f:label>
                <div class="controls">
                    <spr-f:password path="password"/>
                    <spr-f:errors path="password"/>
                </div>
            </div>

            <div class="control-group">
                <spr-f:label path="passwordConfirm" cssClass="control-label">Confirm password:</spr-f:label>
                <div class="controls">
                    <spr-f:password path="passwordConfirm"/>
                    <spr-f:errors path="passwordConfirm"/>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <input type="submit" value="Save" class="btn	" />
                </div>
            </div>
        </spr-f:form>
    </body>
</html>