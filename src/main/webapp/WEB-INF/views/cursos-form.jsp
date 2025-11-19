<%-- 
    Document   : cursos-form
    Created on : 1 de out. de 2025, 09:12:54
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<form action="${pageContext.request.contextPath}/admin/cursos" method="post" class="form">
  <input type="hidden" name="action" value="save"/>
  <input type="hidden" name="id" value="${curso.id}"/>

  <label>Nome
    <input type="text" name="nome" value="${curso.nome}" required/>
  </label>

  <label>Currículo
    <textarea name="curriculo" rows="3">${curso.curriculo}</textarea>
  </label>

  <div class="actions">
    <button class="btn" type="submit">Salvar</button>
    <a class="btn ghost" href="${pageContext.request.contextPath}/admin/cursos">Voltar</a>
  </div>
</form>


