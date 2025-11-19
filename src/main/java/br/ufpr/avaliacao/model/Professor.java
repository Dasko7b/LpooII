/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Professor {
    private Long usuarioId;
    private String registro;
    private String departamento;

    public Professor() {}
    public Professor(Long usuarioId, String registro, String departamento) {
        this.usuarioId = usuarioId; this.registro = registro; this.departamento = departamento;
    }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
