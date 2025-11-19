/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro
 */

public class Turma {
    private Integer id;
    private Integer disciplinaId;
    private String anoSemestre; // ex: 2025-1
    private String codigo;      // ex: T01

    public Turma() {}
    public Turma(Integer id, Integer disciplinaId, String anoSemestre, String codigo) {
        this.id = id; this.disciplinaId = disciplinaId; this.anoSemestre = anoSemestre; this.codigo = codigo;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getDisciplinaId() { return disciplinaId; }
    public void setDisciplinaId(Integer disciplinaId) { this.disciplinaId = disciplinaId; }
    public String getAnoSemestre() { return anoSemestre; }
    public void setAnoSemestre(String anoSemestre) { this.anoSemestre = anoSemestre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
