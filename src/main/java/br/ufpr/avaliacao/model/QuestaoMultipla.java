/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */


public class QuestaoMultipla {
    private Long questaoId;
    private boolean permiteMultiplaSelecao;

    public Long getQuestaoId() { return questaoId; }
    public void setQuestaoId(Long questaoId) { this.questaoId = questaoId; }
    public boolean isPermiteMultiplaSelecao() { return permiteMultiplaSelecao; }
    public void setPermiteMultiplaSelecao(boolean permiteMultiplaSelecao) {
        this.permiteMultiplaSelecao = permiteMultiplaSelecao;
    }
}
