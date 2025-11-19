<%-- 
    Document   : usuarios-form
    Created on : 1 de out. de 2025, 09:12:25
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:set var="u" value="${usuario}"/>

<form action="${pageContext.request.contextPath}/admin/usuarios" method="post" class="form">
  <input type="hidden" name="action" value="save"/>
  <input type="hidden" name="id" value="${u.id}"/>

  <label>Nome
    <input type="text" name="nome" value="${u.nome}" required/>
  </label>

  <label>Email
    <input type="email" name="email" value="${u.email}" required/>
  </label>

  <label>Login
    <input type="text" name="login" value="${u.login}" required/>
  </label>

  <label>Senha
    <input type="password" name="senha" value="${u.senhaHash}" required/>
  </label>

  <label class="row">
    <input type="checkbox" name="ativo" <c:if test="${u.ativo}">checked</c:if> /> Ativo
  </label>

  <fieldset>
    <legend>Perfis</legend>
    <label><input type="checkbox" name="perfilAluno" <c:if test="${u.perfis != null && u.perfis.contains('ALUNO')}">checked</c:if>/> Aluno</label>
    <label><input type="checkbox" name="perfilProfessor" <c:if test="${u.perfis != null && u.perfis.contains('PROFESSOR')}">checked</c:if>/> Professor</label>
    <label><input type="checkbox" name="perfilCoord" <c:if test="${u.perfis != null && u.perfis.contains('COORDENADOR')}">checked</c:if>/> Coordenador</label>
    <label><input type="checkbox" name="perfilAdmin" <c:if test="${u.perfis != null && u.perfis.contains('ADMIN')}">checked</c:if>/> Admin</label>
  </fieldset>

  <fieldset>
    <legend>Dados de Aluno</legend>
    <label>Matrícula
      <input type="text" name="matricula" value="${aluno != null ? aluno.matricula : ''}"/>
    </label>
  </fieldset>

  <fieldset>
    <legend>Dados de Professor</legend>
    <label>Registro
      <input type="text" name="registro" value="${prof != null ? prof.registro : ''}"/>
    </label>
    <label>Departamento
      <input type="text" name="departamento" value="${prof != null ? prof.departamento : ''}"/>
    </label>
  </fieldset>

  <div class="actions">
    <button class="btn" type="submit">Salvar</button>
    <a class="btn ghost" href="${pageContext.request.contextPath}/admin/usuarios">Voltar</a>
  </div>
</form>


