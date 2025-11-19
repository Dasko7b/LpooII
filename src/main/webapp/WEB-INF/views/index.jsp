<%-- 
    Document   : index
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<h2>Painel Administrativo</h2>
<p style="color:#64748b; margin-bottom: 24px;">Selecione uma opção para gerenciar o sistema:</p>

<div style="display: flex; flex-wrap: wrap; gap: 16px;">
    
  <a class="btn" href="${pageContext.request.contextPath}/admin/usuarios" 
     style="min-width: 200px; justify-content: center; height: 50px;">
      Gerenciar Usuários
  </a>
  
  <a class="btn" href="${pageContext.request.contextPath}/admin/cursos" 
     style="min-width: 200px; justify-content: center; height: 50px;">
      Gerenciar Cursos
  </a>
  
  <a class="btn" href="${pageContext.request.contextPath}/admin/formularios" 
     style="min-width: 200px; justify-content: center; height: 50px;">
      Gerenciar Formulários
  </a>
  
</div>

<%@ include file="_footer.jspf" %>