/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Alternativa {
    private Long id;
    private Long questaoId;
    private String texto;
    private int peso;
    private int ordem;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuestaoId() { return questaoId; }
    public void setQuestaoId(Long questaoId) { this.questaoId = questaoId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
}
