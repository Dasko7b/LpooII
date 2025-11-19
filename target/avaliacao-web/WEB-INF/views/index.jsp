<%-- 
    Document   : index
    Created on : 1 de out. de 2025, 09:12:06
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<h2>Protótipo de Telas (Etapa 1)</h2>
<ul>
  <li><a class="btn" href="${pageContext.request.contextPath}/admin/usuarios">Gerenciar Usuários</a></li>
  <li><a class="btn" href="${pageContext.request.contextPath}/admin/cursos">Gerenciar Cursos</a></li>
  <li><a class="btn" href="${pageContext.request.contextPath}/admin/formularios">Gerenciar Formulários</a></li>
</ul>

<%@ include file="_footer.jspf" %>
