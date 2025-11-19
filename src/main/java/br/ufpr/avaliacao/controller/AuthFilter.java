/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter(urlPatterns = {"/admin/*"})
public class AuthFilter implements Filter {

    private boolean isAdmin(Usuario u) {
        if (u == null) return false;
        Object perfisObj = u.getPerfis(); // deve ser Set<Perfil>
        if (perfisObj instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<Perfil> perfis = (Set<Perfil>) perfisObj;
            return perfis.contains(Perfil.ADMIN) || perfis.contains(Perfil.COORDENADOR);
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Usuario u = (Usuario) req.getSession().getAttribute("usuarioLogado");
        Boolean isAdminFlag = (Boolean) req.getSession().getAttribute("isAdmin");

        boolean autorizado = (isAdminFlag != null && isAdminFlag) || isAdmin(u);
        if (!autorizado) {
            // não logado → login; logado sem perfil → área do aluno
            if (u == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
            } else {
                resp.sendRedirect(req.getContextPath() + "/aluno");
            }
            return;
        }

        chain.doFilter(request, response);
    }
}

