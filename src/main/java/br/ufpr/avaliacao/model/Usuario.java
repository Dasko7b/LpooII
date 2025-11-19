/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

import java.util.EnumSet;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senhaHash;
    private boolean ativo = true;
    private EnumSet<Perfil> perfis = EnumSet.noneOf(Perfil.class);

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String login, String senhaHash) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senhaHash = senhaHash;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public EnumSet<Perfil> getPerfis() { return perfis; }
    public void setPerfis(EnumSet<Perfil> perfis) { this.perfis = perfis; }
}