<%-- 
    Document   : cursos-list
    Created on : 1 de out. de 2025, 09:12:44
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<div class="actions">
  <a class="btn" href="${pageContext.request.contextPath}/admin/cursos?action=form">Novo Curso</a>
</div>

<table class="table">
  <thead><tr><th>ID</th><th>Nome</th><th>Currículo</th><th>Ações</th></tr></thead>
  <tbody>
  <c:forEach var="c" items="${cursos}">
    <tr>
      <td data-label="ID">${c.id}</td>
      <td data-label="Nome">${c.nome}</td>
      <td data-label="Currículo">${c.curriculo}</td>
      <td data-label="Ações">
        <a class="link" href="${pageContext.request.contextPath}/admin/cursos?action=form&id=${c.id}">Editar</a> |
        <form action="${pageContext.request.contextPath}/admin/cursos" method="post" style="display:inline">
          <input type="hidden" name="action" value="delete"/>
          <input type="hidden" name="id" value="${c.id}"/>
          <button class="link danger" type="submit">Excluir</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>

<%@ include file="_footer.jspf" %>
