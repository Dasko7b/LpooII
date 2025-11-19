<%-- 
    Document   : usuarios-list
    Created on : 1 de out. de 2025, 09:12:12
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<div class="actions">
  <a class="btn" href="${pageContext.request.contextPath}/admin/usuarios?action=form">Novo Usuário</a>
</div>

<table class="table">
  <thead>
    <tr>
      <th>ID</th><th>Nome</th><th>Email</th><th>Login</th><th>Perfis</th><th>Ações</th>
    </tr>
  </thead>
  <tbody>
  <c:forEach var="u" items="${usuarios}">
    <tr>
      <td data-label="ID">${u.id}</td>
      <td data-label="Nome">${u.nome}</td>
      <td data-label="Email">${u.email}</td>
      <td data-label="Login">${u.login}</td>
      <td data-label="Perfis">${u.perfis}</td>
      <td data-label="Ações">
        <a class="link" href="${pageContext.request.contextPath}/admin/usuarios?action=form&id=${u.id}">Editar</a>
        |
        <form action="${pageContext.request.contextPath}/admin/usuarios" method="post" style="display:inline">
          <input type="hidden" name="action" value="delete"/>
          <input type="hidden" name="id" value="${u.id}"/>
          <button class="link danger" type="submit">Excluir</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>


