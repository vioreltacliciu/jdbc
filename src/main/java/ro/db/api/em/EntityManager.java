package ro.db.api.em;

/**
 * Developer: Viorelt
 * <p>
 **/

public interface EntityManager<T> {

    T findById(Long id);
}
