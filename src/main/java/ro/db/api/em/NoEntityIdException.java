package ro.db.api.em;

/**
 * Developer: Viorelt
 * <p>
 **/

public class NoEntityIdException extends RuntimeException {
    public NoEntityIdException(Class<?> entityClass) {
        super("Class "+entityClass.getSimpleName()+" must have a field annotated with @Id");
    }
}
