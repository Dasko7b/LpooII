<%-- 
    Document   : formularios-list
    Created on : 1 de out. de 2025, 09:13:05
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<div class="actions">
  <a class="btn" href="${pageContext.request.contextPath}/admin/formularios?action=form">Novo Formulário</a>
</div>

<table class="table">
  <thead><tr><th>ID</th><th>Título</th><th>Modo</th><th>Ações</th></tr></thead>
  <tbody>
  <c:forEach var="f" items="${formularios}">
    <tr>
      <td data-label="ID">${f.id}</td>
      <td data-label="Título">${f.titulo}</td>
      <td data-label="Modo">
        <c:choose>
          <c:when test="${f.anonimo}">Anônimo</c:when>
          <c:otherwise>Identificado</c:otherwise>
        </c:choose>
      </td>
      <td data-label="Ações">
        <a class="link" href="${pageContext.request.contextPath}/admin/formularios?action=form&id=${f.id}">Editar</a>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>


