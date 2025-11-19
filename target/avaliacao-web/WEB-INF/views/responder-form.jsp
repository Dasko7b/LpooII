<%-- 
    Document   : responder-form
    Created on : 1 de out. de 2025, 11:44:48
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:if test="${not empty erro}">
  <div class="card" style="color:#fecaca;">${erro}</div>
</c:if>

<c:set var="f" value="${form}"/>

<h2>${f.titulo}</h2>
<p style="color:#cbd5e1">${f.instrucoes}</p>
<p style="color:#93c5fd;font-size:12px;">
  <c:choose>
    <c:when test="${f.anonimo}">Este formulário é anônimo</c:when>
    <c:otherwise>Este formulário é identificado</c:otherwise>
  </c:choose>
</p>

<form method="post" action="${pageContext.request.contextPath}/aluno/responder" class="form">
  <input type="hidden" name="formularioId" value="${f.id}"/>

  <c:forEach var="q" items="${questoes}">
    <div class="card">
      <strong>Q${q.ordem}.</strong> ${q.enunciado}
      <c:if test="${q.obrigatoria}">
        <span style="color:#fca5a5"> *</span>
      </c:if>
      <div style="margin-top:10px;">
        <c:choose>
          <c:when test="${q.tipo.name() == 'ABERTA'}">
            <textarea name="q_${q.id}" rows="3" <c:if test="${q.obrigatoria}">required</c:if> ></textarea>
          </c:when>

          <c:when test="${q.tipo.name() == 'UNICA'}">
            <c:forEach var="a" items="${alternativas[q.id]}">
              <label class="row">
                <input type="radio" name="q_${q.id}" value="${a.id}"
                       <c:if test="${q.obrigatoria}">required</c:if> />
                ${a.texto}
              </label>
            </c:forEach>
          </c:when>

          <c:otherwise> <%-- MULTIPLA --%>
            <c:forEach var="a" items="${alternativas[q.id]}">
              <label class="row">
                <input type="checkbox" name="q_${q.id}" value="${a.id}" />
                ${a.texto}
              </label>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:forEach>

  <div class="actions">
    <button class="btn" type="submit">Enviar respostas</button>
    <a class="btn ghost" href="${pageContext.request.contextPath}/aluno">Cancelar</a>
  </div>
</form>


