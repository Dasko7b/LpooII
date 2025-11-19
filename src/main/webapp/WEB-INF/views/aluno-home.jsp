<%-- 
    Document   : aluno-home
    Author     : Pedro, Gabi
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<h2>Formulários disponíveis</h2>

<c:if test="${empty formularios}">
  <div class="card">Nenhum formulário disponível no momento.</div>
</c:if>

<c:forEach var="f" items="${formularios}">
  <div class="card space-between">
    <div>
      <strong>${f.titulo}</strong>
      <div style="color:#475569; font-size:14px; margin-top:4px;">${f.instrucoes}</div>
      <div style="color:#2563eb; font-size:12px; margin-top:4px; font-weight:600;">
        <c:choose>
          <c:when test="${f.anonimo}">Modo: Anônimo</c:when>
          <c:otherwise>Modo: Identificado</c:otherwise>
        </c:choose>
      </div>
    </div>
    <div>
      <a class="btn" href="${pageContext.request.contextPath}/aluno/responder?id=${f.id}">Responder</a>
    </div>
  </div>
</c:forEach>

<%@ include file="_footer.jspf" %>