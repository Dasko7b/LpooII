/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Disciplina {
    private Integer id;
    private Integer cursoId;
    private String nome;
    private Integer cargaHoraria;

    public Disciplina() {}
    public Disciplina(Integer id, Integer cursoId, String nome, Integer cargaHoraria) {
        this.id = id; this.cursoId = cursoId; this.nome = nome; this.cargaHoraria = cargaHoraria;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }
}
