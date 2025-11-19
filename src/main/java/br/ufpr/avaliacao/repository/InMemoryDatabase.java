/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryDatabase {

    // “Tabelas” em memória
    public static final Map<Long, Usuario> USUARIOS = new ConcurrentHashMap<>();
    public static final Map<Long, Aluno> ALUNOS = new ConcurrentHashMap<>();
    public static final Map<Long, Professor> PROFESSORES = new ConcurrentHashMap<>();
    public static final Map<Integer, Curso> CURSOS = new ConcurrentHashMap<>();
    public static final Map<Long, Formulario> FORMULARIOS = new ConcurrentHashMap<>();
    public static final Map<Long, Questao> QUESTOES = new ConcurrentHashMap<>();
    public static final Map<Long, QuestaoMultipla> QUESTOES_MULTIPLA = new ConcurrentHashMap<>();
    public static final Map<Long, Alternativa> ALTERNATIVAS = new ConcurrentHashMap<>();

    // Avaliações e participação
    public static final Map<Long, Avaliacao> AVALIACOES = new ConcurrentHashMap<>();
    public static final Set<String> PARTICIPACOES = ConcurrentHashMap.newKeySet();

    // >>> NOVO: armazenamento de respostas individuais
    public static final Map<Long, Resposta> RESPOSTAS = new ConcurrentHashMap<>();

    // Sequências
    private static final AtomicLong USER_SEQ = new AtomicLong(1);
    private static final AtomicLong FORM_SEQ = new AtomicLong(1);
    private static final AtomicLong QUEST_SEQ = new AtomicLong(1);
    private static final AtomicLong ALT_SEQ = new AtomicLong(1);
    private static final AtomicLong CURSO_SEQ = new AtomicLong(1);
    private static final AtomicLong AVAL_SEQ = new AtomicLong(1);
    // >>> NOVO: sequência de respostas
    private static final AtomicLong RESP_SEQ = new AtomicLong(1);

    // ---- Usuario ----
    public static Usuario saveUsuario(Usuario u) {
        if (u.getId() == null) u.setId(USER_SEQ.getAndIncrement());
        USUARIOS.put(u.getId(), u);
        return u;
    }
    public static List<Usuario> listUsuarios() { return new ArrayList<>(USUARIOS.values()); }
    public static Usuario findUsuario(Long id) { return USUARIOS.get(id); }
    public static void deleteUsuario(Long id) {
        USUARIOS.remove(id);
        ALUNOS.remove(id);
        PROFESSORES.remove(id);
    }

    public static void upsertAluno(Aluno a) { if (a != null) ALUNOS.put(a.getUsuarioId(), a); }
    public static Aluno findAluno(Long usuarioId) { return ALUNOS.get(usuarioId); }
    public static void upsertProfessor(Professor p) { if (p != null) PROFESSORES.put(p.getUsuarioId(), p); }
    public static Professor findProfessor(Long usuarioId) { return PROFESSORES.get(usuarioId); }

    // ---- Curso ----
    public static Curso saveCurso(Curso c) {
        if (c.getId() == null) c.setId((int) CURSO_SEQ.getAndIncrement());
        CURSOS.put(c.getId(), c);
        return c;
    }
    public static List<Curso> listCursos() { return new ArrayList<>(CURSOS.values()); }
    public static Curso findCurso(Integer id) { return CURSOS.get(id); }
    public static void deleteCurso(Integer id) { CURSOS.remove(id); }

    // ---- Formulário / Questões / Alternativas ----
    public static Formulario saveFormulario(Formulario f) {
        if (f.getId() == null) f.setId(FORM_SEQ.getAndIncrement());
        FORMULARIOS.put(f.getId(), f);
        return f;
    }
    public static List<Formulario> listFormularios() { return new ArrayList<>(FORMULARIOS.values()); }
    public static Formulario findFormulario(Long id) { return FORMULARIOS.get(id); }

    public static Questao saveQuestao(Questao q) {
        if (q.getId() == null) q.setId(QUEST_SEQ.getAndIncrement());
        QUESTOES.put(q.getId(), q);
        return q;
    }
    public static List<Questao> listQuestoesByFormulario(Long formularioId) {
        List<Questao> list = new ArrayList<>();
        for (Questao q : QUESTOES.values()) {
            if (Objects.equals(q.getFormularioId(), formularioId)) list.add(q);
        }
        list.sort(Comparator.comparingInt(Questao::getOrdem));
        return list;
    }
    public static void upsertQuestaoMultipla(QuestaoMultipla qm) {
        QUESTOES_MULTIPLA.put(qm.getQuestaoId(), qm);
    }
    public static QuestaoMultipla findQuestaoMultipla(Long questaoId) { return QUESTOES_MULTIPLA.get(questaoId); }

    public static Alternativa saveAlternativa(Alternativa a) {
        if (a.getId() == null) a.setId(ALT_SEQ.getAndIncrement());
        ALTERNATIVAS.put(a.getId(), a);
        return a;
    }
    public static List<Alternativa> listAlternativasByQuestao(Long questaoId) {
        List<Alternativa> list = new ArrayList<>();
        for (Alternativa a : ALTERNATIVAS.values()) {
            if (Objects.equals(a.getQuestaoId(), questaoId)) list.add(a);
        }
        list.sort(Comparator.comparingInt(Alternativa::getOrdem));
        return list;
    }

    // ---- Autenticação e avaliações ----
    public static Usuario findByLoginESenha(String login, String senha) {
        if (login == null || senha == null) return null;
        for (Usuario u : USUARIOS.values()) {
            if (login.equals(u.getLogin())
                    && senha.equals(u.getSenhaHash())
                    && Boolean.TRUE.equals(u.isAtivo())) {
                return u;
            }
        }
        return null;
    }

    public static List<Formulario> listarFormulariosDisponiveisParaUsuario(Long usuarioId) {
        List<Formulario> out = new ArrayList<>(FORMULARIOS.values());
        out.sort(Comparator.comparingLong(Formulario::getId));
        return out;
    }

    public static boolean jaRespondeu(Long formularioId, Long usuarioId) {
        if (formularioId == null || usuarioId == null) return false;
        return PARTICIPACOES.contains(formularioId + ":" + usuarioId);
    }

    public static void registrarParticipacao(Long formularioId, Long usuarioId) {
        if (formularioId == null || usuarioId == null) return;
        PARTICIPACOES.add(formularioId + ":" + usuarioId);
    }

    public static Avaliacao salvarAvaliacao(Avaliacao a) {
        if (a.getId() == null) a.setId(AVAL_SEQ.getAndIncrement());
        AVALIACOES.put(a.getId(), a);

        // >>> NOVO: persistir também cada Resposta individualmente
        if (a.getRespostas() != null) {
            for (Resposta r : a.getRespostas()) {
                // completa chaves de navegação antes de salvar
                r.setAvaliacaoId(a.getId());
                r.setFormularioId(a.getFormularioId());
                saveResposta(r);
            }
        }
        return a;
    }

    public static List<Avaliacao> listAvaliacoesByFormulario(Long formularioId) {
        List<Avaliacao> list = new ArrayList<>();
        for (Avaliacao a : AVALIACOES.values()) {
            if (Objects.equals(a.getFormularioId(), formularioId)) list.add(a);
        }
        return list;
    }

    // ====== NOVO BLOCO: Respostas individuais ======

    public static Resposta saveResposta(Resposta r) {
        if (r.getId() == null) r.setId(RESP_SEQ.getAndIncrement());
        RESPOSTAS.put(r.getId(), r);
        return r;
    }

    /** Lista todas as respostas registradas para uma questão específica. */
    public static List<Resposta> listRespostasByQuestao(Long questaoId) {
        List<Resposta> list = new ArrayList<>();
        for (Resposta r : RESPOSTAS.values()) {
            if (Objects.equals(r.getQuestaoId(), questaoId)) list.add(r);
        }
        return list;
    }

    /** (Opcional) Lista todas as respostas de um formulário — útil para relatórios gerais. */
    public static List<Resposta> listRespostasByFormulario(Long formularioId) {
        List<Resposta> list = new ArrayList<>();
        // Se preferir derivar das avaliações (mantém consistência):
        for (Avaliacao a : listAvaliacoesByFormulario(formularioId)) {
            if (a.getRespostas() != null) {
                list.addAll(a.getRespostas());
            }
        }
        return list;
    }

    // ---- Seeds ----
    static {
        if (USUARIOS.isEmpty()) {
            // ADMIN
            Usuario admin = new Usuario();
            admin.setNome("Admin");
            admin.setEmail("admin@teste.com");
            admin.setLogin("admin");
            admin.setSenhaHash("admin");
            admin.setAtivo(true);
            admin.setPerfis(EnumSet.of(Perfil.ADMIN, Perfil.COORDENADOR));
            saveUsuario(admin);

            // PROFESSOR
            Usuario prof = new Usuario();
            prof.setNome("Prof. Demo");
            prof.setEmail("prof@teste.com");
            prof.setLogin("prof");
            prof.setSenhaHash("prof");
            prof.setAtivo(true);
            prof.setPerfis(EnumSet.of(Perfil.PROFESSOR));
            saveUsuario(prof);

            Professor pModel = new Professor();
            pModel.setUsuarioId(prof.getId());
            pModel.setRegistro("REG-001");
            pModel.setDepartamento("Departamento X");
            upsertProfessor(pModel);

            // ALUNO
            Usuario aluno = new Usuario();
            aluno.setNome("Aluno Demo");
            aluno.setEmail("aluno@teste.com");
            aluno.setLogin("aluno");
            aluno.setSenhaHash("aluno");
            aluno.setAtivo(true);
            aluno.setPerfis(EnumSet.of(Perfil.ALUNO));
            saveUsuario(aluno);

            Aluno aModel = new Aluno();
            aModel.setUsuarioId(aluno.getId());
            aModel.setMatricula("2024X0001");
            upsertAluno(aModel);

            // Formulário exemplo
            Formulario f = new Formulario();
            f.setTitulo("Avaliação Exemplo - Disciplina X");
            f.setAnonimo(false);
            f.setInstrucoes("Responda com sinceridade.");
            saveFormulario(f);

            // Questão aberta
            Questao q1 = new Questao();
            q1.setFormularioId(f.getId());
            q1.setEnunciado("O que você achou da disciplina?");
            q1.setTipo(TipoQuestao.ABERTA);
            q1.setObrigatoria(false);
            q1.setOrdem(1);
            saveQuestao(q1);

            // Questão única
            Questao q2 = new Questao();
            q2.setFormularioId(f.getId());
            q2.setEnunciado("Avalie a didática do professor");
            q2.setTipo(TipoQuestao.UNICA);
            q2.setObrigatoria(true);
            q2.setOrdem(2);
            saveQuestao(q2);

            Alternativa a1 = new Alternativa();
            a1.setQuestaoId(q2.getId());
            a1.setTexto("Excelente"); a1.setPeso(5); a1.setOrdem(1);
            saveAlternativa(a1);

            Alternativa a2 = new Alternativa();
            a2.setQuestaoId(q2.getId());
            a2.setTexto("Bom"); a2.setPeso(4); a2.setOrdem(2);
            saveAlternativa(a2);

            Alternativa a3 = new Alternativa();
            a3.setQuestaoId(q2.getId());
            a3.setTexto("Regular"); a3.setPeso(3); a3.setOrdem(3);
            saveAlternativa(a3);

            Alternativa a4 = new Alternativa();
            a4.setQuestaoId(q2.getId());
            a4.setTexto("Ruim"); a4.setPeso(1); a4.setOrdem(4);
            saveAlternativa(a4);
        }
    }
}
