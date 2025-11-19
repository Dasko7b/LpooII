<%-- 
    Document   : login
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<div class="card" style="max-width:480px;margin:auto;">
  <h3>Entrar</h3>
  <c:if test="${not empty erro}">
    <p style="color:#dc2626; margin-top:0; background:#fee2e2; padding:10px; border-radius:8px;">${erro}</p>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/login" class="form">
    <label>Login
      <input type="text" name="login" required />
    </label>
    <label>Senha
      <input type="password" name="senha" required />
    </label>
    <button class="btn" type="submit">Entrar</button>
    
    <p style="color:#64748b; font-size:13px; margin:8px 0 0">
      Dicas para testes: <code>admin/admin</code> (ADMIN), <code>aluno/aluno</code> (ALUNO)
    </p>
  </form>
</div>