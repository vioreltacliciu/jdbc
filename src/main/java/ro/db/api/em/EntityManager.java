package ro.db.api.em;

import java.util.List;

/**
 * Developer: Viorelt
 * <p>
 **/

public interface EntityManager<T> {

    T findById(Long id);

    Long getNextIdVal(String tableName, String columnIdName);

    T insert(T entity);

    List<T> findAll();

}
