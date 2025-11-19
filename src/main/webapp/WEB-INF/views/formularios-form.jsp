<%-- 
    Document   : formularios-form
    Created on : 1 de out. de 2025, 09:13:13
    Author     : Bruno, Dyego, Maria, Matheus, Thiago
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:set var="f" value="${formulario}"/>

<h3>Dados do Formulário</h3>
<form action="${pageContext.request.contextPath}/admin/formularios" method="post" class="form inline">
  <input type="hidden" name="action" value="saveForm"/>
  <input type="hidden" name="id" value="${f.id}"/>

  <label>Título
    <input type="text" name="titulo" value="${f.titulo}" required/>
  </label>

  <label>Instruções
    <textarea name="instrucoes" rows="2">${f.instrucoes}</textarea>
  </label>

  <label class="row">
    <input type="checkbox" name="anonimo" <c:if test="${f.anonimo}">checked</c:if> /> Anônimo
  </label>

  <button class="btn" type="submit">Salvar</button>
  <a class="btn ghost" href="${pageContext.request.contextPath}/admin/formularios">Voltar</a>
</form>

<c:if test="${f.id != null}">
  <hr/>
  <h3>Adicionar Questão</h3>
  <form action="${pageContext.request.contextPath}/admin/formularios" method="post" class="form">
    <input type="hidden" name="action" value="addQuestao"/>
    <input type="hidden" name="formularioId" value="${f.id}"/>

    <label>Enunciado
      <input type="text" name="enunciado" required/>
    </label>

    <label>Tipo
      <select name="tipo" id="tipoSelect">
        <option value="ABERTA">Aberta</option>
        <option value="UNICA">Múltipla (resposta única)</option>
        <option value="MULTIPLA">Múltipla (várias respostas)</option>
      </select>
    </label>

    <label>Ordem
      <input type="number" name="ordem" value="0"/>
    </label>

    <label class="row">
      <input type="checkbox" name="obrigatoria"/> Obrigatória
    </label>

    <div id="divMultipla" style="display:none">
      <label class="row">
        <input type="checkbox" name="permiteMultipla"/> Permitir múltiplas seleções
      </label>
    </div>

    <button class="btn" type="submit">Adicionar Questão</button>
  </form>

  <script>
    const tipoSel = document.getElementById('tipoSelect');
    const box = document.getElementById('divMultipla');
    function toggleBox(){ box.style.display = (tipoSel.value === 'ABERTA') ? 'none' : 'block'; }
    tipoSel.addEventListener('change', toggleBox); toggleBox();
  </script>

  <hr/>
  <h3>Questões do Formulário</h3>
  <c:forEach var="q" items="${questoes}">
    <div class="card">
      <div class="row space-between">
        <div><strong>Q${q.ordem}.</strong> ${q.enunciado} <small>(${q.tipo})</small>
          <c:if test="${q.obrigatoria}"><em> — obrigatória</em></c:if>
        </div>
      </div>

      <c:if test="${q.tipo != 'ABERTA'}">
        <form action="${pageContext.request.contextPath}/admin/formularios" method="post" class="form inline">
          <input type="hidden" name="action" value="addAlternativa"/>
          <input type="hidden" name="questaoId" value="${q.id}"/>

          <label>Alternativa
            <input type="text" name="texto" required/>
          </label>
          <label>Peso
            <input type="number" name="peso" value="1"/>
          </label>
          <label>Ordem
            <input type="number" name="ordem" value="0"/>
          </label>
          <button class="btn" type="submit">Adicionar Alternativa</button>
        </form>

        <%-- Listagem de alternativas (etapa 1, via scriptlet simples) --%>
        <%
          java.util.List<br.ufpr.avaliacao.model.Alternativa> list =
             br.ufpr.avaliacao.repository.InMemoryDatabase.listAlternativasByQuestao(
               ((br.ufpr.avaliacao.model.Questao)pageContext.getAttribute("q")).getId()
             );
        %>
        <ul class="alts">
          <% for (br.ufpr.avaliacao.model.Alternativa a : list) { %>
            <li>(ordem <%=a.getOrdem()%>) [peso <%=a.getPeso()%>] — <%=a.getTexto()%></li>
          <% } %>
        </ul>
      </c:if>
    </div>
  </c:forEach>
</c:if>


