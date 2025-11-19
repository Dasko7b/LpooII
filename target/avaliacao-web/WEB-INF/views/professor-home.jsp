<%-- 
    Document   : professor-home
    Created on : 1 de out. de 2025, 13:45:53
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="_header.jspf" %>

<h2>Painel do Professor</h2>

<c:forEach var="f" items="${formularios}">
  <div class="card">
    <h3>${f.titulo}</h3>

    <c:forEach var="q" items="${questoesPorFormulario[f.id]}">
      <h4>${q.enunciado}</h4>

      <c:choose>
        <c:when test="${q.tipo == 'ABERTA'}">
          <ul>
            <c:forEach var="r" items="${respostasAbertas[q.id]}">
              <li>${r.textoResposta}</li>
            </c:forEach>
          </ul>
        </c:when>
        <c:otherwise>
          <table class="table">
            <thead>
              <tr>
                <th>Alternativa</th>
                <th>Votos</th>
                <th>%</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="item" items="${distQuestoes[q.id]}">
                <tr>
                  <td>${item.alternativa}</td>
                  <td>${item.votos}</td>
                  <td><fmt:formatNumber value="${item.percentual}" type="number" maxFractionDigits="1"/>%</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:otherwise>
      </c:choose>

    </c:forEach>
  </div>
</c:forEach>
