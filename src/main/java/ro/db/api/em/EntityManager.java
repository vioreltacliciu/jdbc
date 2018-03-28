package ro.db.api.em;

import java.util.List;
import java.util.Map;

/**
 * Developer: Viorelt
 * <p>
 **/

public interface EntityManager<T> {

    T findById(Long id);

    Long getNextIdVal(String tableName, String columnIdName);

    T insert(T entity);

    List<T> findAll();

    T update(T entity);

    void delete(T entity);

    List<T> findByParams(Map<String,Object> params);

}
