package br.ufpr.avaliacao.dao;

import java.util.List;

public interface Dao<T, ID> {
    
    /** Persiste a entidade, retornando a entidade com o ID atualizado se for uma nova. */
    T save(T entity);
    
    /** Busca a entidade pelo seu identificador único. */
    T findById(ID id);
    
    /** Lista todas as entidades do tipo. */
    List<T> findAll();
    
    /** Remove a entidade pelo seu identificador. */
    void delete(ID id);
}
