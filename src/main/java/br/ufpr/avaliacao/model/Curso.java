/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Curso {
    private Integer id;
    private String nome;
    private String curriculo;

    public Curso() {}
    public Curso(Integer id, String nome, String curriculo) {
        this.id = id; this.nome = nome; this.curriculo = curriculo;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCurriculo() { return curriculo; }
    public void setCurriculo(String curriculo) { this.curriculo = curriculo; }
}
