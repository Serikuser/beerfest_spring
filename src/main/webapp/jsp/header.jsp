<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${locale}" scope="session"/>
<fmt:setBundle basename="pagecontent"/>
<html>
<body>
<div class="header header_absolute">
    <div class="header_main">
        <div class="header_inner">
            <div class="header_item header_logo">
                <div class="logo">
                    <a href="/Beerfest"> <img src="/Beerfest/img/logo.png" class="logo_img"></a>
                </div>
            </div>
            <div class="header_item">
                <div class="header-menu">
                    <div class="header-menu_item">
                        <a href="/Beerfest/controller?command=feed_update" id="news"><fmt:message key="header.label.news"/></a>
                    </div>
                    <div class="header-menu_item">
                        <a href="javascript: void(0)" id="gallery"><fmt:message key="header.label.gallery"/></a>
                    </div>
                    <div class="header-menu_item">
                        <a href="/Beerfest/controller?command=participant_list_update" id="participants"><fmt:message
                                key="header.label.participants"/></a>
                    </div>
                    <div class="header-menu_item">
                        <a href="javascript: void(0)" id="rules"><fmt:message key="header.label.rules"/></a>
                    </div>
                </div>

            </div>
            <div class="header_item">
                <div class="header-right-menu" style="align-items: center">
                    <c:if test="${userRole != 'UNAUTHORIZED'}">
                        <div class="header-right_menu_item">
                            <a href="/Beerfest/controller?command=profile" id="profile" class="user_avatar">
                                <img data-src="${userAvatarUrl}" src="${userAvatarUrl}"
                                     class="avatar avatar_medium" alt="...">
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
