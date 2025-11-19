/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Questao {
    private Long id;
    private Long formularioId;
    private String enunciado;
    private boolean obrigatoria;
    private int ordem;
    private TipoQuestao tipo;

    public Questao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFormularioId() { return formularioId; }
    public void setFormularioId(Long formularioId) { this.formularioId = formularioId; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public boolean isObrigatoria() { return obrigatoria; }
    public void setObrigatoria(boolean obrigatoria) { this.obrigatoria = obrigatoria; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
    public TipoQuestao getTipo() { return tipo; }
    public void setTipo(TipoQuestao tipo) { this.tipo = tipo; }
}
