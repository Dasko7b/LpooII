/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

import java.util.ArrayList;
import java.util.List;

public class Resposta {
    private Long id;                 // << necessário para salvar no InMemory
    private Long formularioId;       // facilita consultas/relatórios
    private Long avaliacaoId;        // vínculo com a avaliação (quem submeteu)
    private Long questaoId;          // a que questão esta resposta pertence

    // Para questões abertas
    private String textoResposta;

    // Para questões de múltipla/única escolha
    private List<Long> alternativasIds = new ArrayList<>();

    // === Getters/Setters ===
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFormularioId() {
        return formularioId;
    }
    public void setFormularioId(Long formularioId) {
        this.formularioId = formularioId;
    }

    public Long getAvaliacaoId() {
        return avaliacaoId;
    }
    public void setAvaliacaoId(Long avaliacaoId) {
        this.avaliacaoId = avaliacaoId;
    }

    public Long getQuestaoId() {
        return questaoId;
    }
    public void setQuestaoId(Long questaoId) {
        this.questaoId = questaoId;
    }

    public String getTextoResposta() {
        return textoResposta;
    }
    public void setTextoResposta(String textoResposta) {
        this.textoResposta = textoResposta;
    }

    public List<Long> getAlternativasIds() {
        return alternativasIds;
    }
    public void setAlternativasIds(List<Long> alternativasIds) {
        this.alternativasIds = alternativasIds;
    }
}
