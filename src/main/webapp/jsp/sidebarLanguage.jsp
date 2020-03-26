<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${locale}" scope="session"/>
<fmt:setBundle basename="pagecontent"/>
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
</head>
<body>
<div class="sidebar-block sidebar-block_border">
    <div class="sidebar-block_header">
        <h4><fmt:message key="sidebar.language.label"/></h4>
    </div>
    <div class="sidebar-block_content">
        <div>
            <a href="/Beerfest/controller?command=CHANGE_LOCALE&locale=ru" id="ru">
                <img src="img/lang/ru.jpg" class="lang_img"> Русский язык
            </a>

        </div>
        <div>
            <a href="/Beerfest/controller?command=CHANGE_LOCALE&locale=by" id="by">
                <img src="img/lang/by.jpg" class="lang_img"> Беларуская мова
            </a>

        </div>
        <div>
            <a href="/Beerfest/controller?command=CHANGE_LOCALE&locale=en" id="en">
                <img src="img/lang/en.jpg" class="lang_img">English language
            </a>

        </div>
    </div>
</div>
<script src="<c:url value="/js/change_locale.js" />"></script>
</body>
</html>
