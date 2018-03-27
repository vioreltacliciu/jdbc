package ro.db.api.em;

/**
 * Developer: Viorelt
 * <p>
 **/

public class NoEntityException extends RuntimeException {
    public NoEntityException(Class<?> entityClass) {
        super("Class "+entityClass.getSimpleName()+" must be annotated with @Table");
    }
}
