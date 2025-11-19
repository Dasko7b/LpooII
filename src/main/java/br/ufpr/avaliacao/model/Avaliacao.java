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
import java.util.Date;
import java.util.List;

public class Avaliacao {
    private Long id;
    private Long formularioId;
    private Long usuarioId;        // null se anônimo
    private Date dataSubmissao = new Date();
    private List<Resposta> respostas = new ArrayList<>();

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

    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getDataSubmissao() {
        return dataSubmissao;
    }
    public void setDataSubmissao(Date dataSubmissao) {
        this.dataSubmissao = dataSubmissao;
    }

    public List<Resposta> getRespostas() {
        return respostas;
    }
    public void setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
    }
}
