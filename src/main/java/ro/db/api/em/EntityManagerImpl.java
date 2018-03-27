package ro.db.api.em;

import org.apache.log4j.Logger;

/**
 * Developer: Viorelt
 * <p>
 **/

public class EntityManagerImpl<T> implements EntityManager<T> {
    private Class<T> entityClass;
    public static final Logger LOG=Logger.getLogger(EntityManagerImpl.class);
    public EntityManagerImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T findById( Long id) {
        T ret=null;



        try{
            ret=entityClass.newInstance();
            QueryBuilder queryBuilder=EntityUtils.getQueryBuilder(entityClass);
            queryBuilder.setQueryType(QueryType.SELECT);

            ColumnInfo idColumn=EntityUtils.getPkColumn(entityClass);
            idColumn.setValue(id);
            queryBuilder.addCondition(SQLHelper.condition(idColumn));

        } catch (IllegalAccessException | InstantiationException e) {
            LOG.error(e);
        }


        return ret;
    }
}
