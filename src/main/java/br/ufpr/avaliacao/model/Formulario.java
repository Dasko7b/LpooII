/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

import java.time.LocalDateTime;

public class Formulario {
    private Long id;
    private Integer processoId;
    private String titulo;
    private boolean anonimo;
    private String instrucoes;
    private LocalDateTime inicioColeta;
    private LocalDateTime fimColeta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getProcessoId() { return processoId; }
    public void setProcessoId(Integer processoId) { this.processoId = processoId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public boolean isAnonimo() { return anonimo; }
    public void setAnonimo(boolean anonimo) { this.anonimo = anonimo; }
    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }
    public LocalDateTime getInicioColeta() { return inicioColeta; }
    public void setInicioColeta(LocalDateTime inicioColeta) { this.inicioColeta = inicioColeta; }
    public LocalDateTime getFimColeta() { return fimColeta; }
    public void setFimColeta(LocalDateTime fimColeta) { this.fimColeta = fimColeta; }
}
