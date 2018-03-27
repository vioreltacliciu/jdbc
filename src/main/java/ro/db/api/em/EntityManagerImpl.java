package ro.db.api.em;

import org.apache.log4j.Logger;
import ro.db.api.database.DBManager;
import ro.db.appl.domain.Location;

import javax.swing.plaf.nimbus.State;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Developer: Viorelt
 * <p>
 **/

public class EntityManagerImpl<T> implements EntityManager<T> {
    private Class<T> entityClass;
    public static final Logger LOG = Logger.getLogger(EntityManagerImpl.class);

    public EntityManagerImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T findById(Long id) {
        T ret = null;
        QueryBuilder queryBuilder = EntityUtils.getQueryBuilder(entityClass);
        queryBuilder.setQueryType(QueryType.SELECT);
        ColumnInfo idColumn = EntityUtils.getPkColumn(entityClass);
        idColumn.setValue(id);
        queryBuilder.addCondition(SQLHelper.condition(idColumn));
        try (Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            ret = entityClass.newInstance();
            ResultSet resultSet = stmt.executeQuery(queryBuilder.createQuery());
            if (resultSet.next()) {
                for (ColumnInfo columnInfo : queryBuilder.getQueryColumns()) {
                    Object dbVal = resultSet.getObject(columnInfo.getDbColumnName());
                    Object javaVal = EntityUtils.castFromSqlType(dbVal, columnInfo.getColumnType());
                    Field field = entityClass.getDeclaredField(columnInfo.getColumnName());
                    field.setAccessible(true);
                    field.set(ret, javaVal);
                }
            }

        } catch (IllegalAccessException | InstantiationException | SQLException | NoSuchFieldException e) {
            LOG.error(e);
        }

        return ret;
    }

    @Override
    public Long getNextIdVal(String tableName, String columnIdName) {
        try (Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("select max(" + columnIdName + ") from " + tableName);
            if (rs.next()) {
                return rs.getLong(1)+1;
            }
        } catch (SQLException e) {
            LOG.error(e);
        }

        return null;

    }

    @Override
    public T insert(T entity) {
        String tableName = EntityUtils.getTableName(entityClass);
        ColumnInfo pkColumn = EntityUtils.getPkColumn(entityClass);
        String pkName = pkColumn.getDbColumnName();
        QueryBuilder queryBuilder = EntityUtils.getQueryBuilder(entityClass);
        queryBuilder.setQueryType(QueryType.INSERT);
        Long nextId = getNextIdVal(tableName, pkName);
        try (Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            Field pkField = entityClass.getDeclaredField(pkColumn.getColumnName());
            pkField.setAccessible(true);
            pkField.set(entity,nextId);
            for (ColumnInfo columnInfo : queryBuilder.getQueryColumns()) {
                Field fieldCol = entityClass.getDeclaredField(columnInfo.getColumnName());
                fieldCol.setAccessible(true);

                if (columnInfo.isId()) {
                    columnInfo.setValue(nextId);
                } else {
                    Object javaVal = fieldCol.get(entity);
                    columnInfo.setValue(javaVal);
                }
                queryBuilder.addCondition(SQLHelper.condition(columnInfo));
            }
            int exec=stmt.executeUpdate(queryBuilder.createQuery());
            if(exec>0){
                return entity;
            }

        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            LOG.error(e);
        }

        return entity;
    }

    @Override
    public List<T> findAll() {
        List<T> ret=new ArrayList<>();
        QueryBuilder queryBuilder = EntityUtils.getQueryBuilder(entityClass);
        queryBuilder.setQueryType(QueryType.SELECT);
        try (Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs=stmt.executeQuery(queryBuilder.createQuery());
            while(rs.next()){
                T row=entityClass.newInstance();
                for (ColumnInfo columnInfo : queryBuilder.getQueryColumns()) {
                    Field field=entityClass.getDeclaredField(columnInfo.getColumnName());
                    Object dbValue=rs.getObject(columnInfo.getDbColumnName());
                    Object javaValue=EntityUtils.castFromSqlType(dbValue,columnInfo.getColumnType());
                    field.setAccessible(true);
                    field.set(row,javaValue);
                }
                ret.add(row);
            }

        } catch (SQLException | NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            LOG.error(e);
        }
        return ret;
    }
}
