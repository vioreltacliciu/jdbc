package ro.db.api.em;

import org.apache.log4j.Logger;
import ro.db.api.database.DBManager;
import ro.db.appl.domain.Location;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
            ResultSet resultSet = stmt.executeQuery(queryBuilder.createQuery());
            if (resultSet.next()) {
                ret = entityClass.newInstance();
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

    @Override
    public T update(T entity) {
        //obtin query builder populat cu List<ColumnInfo> si table name
        QueryBuilder queryBuilder = EntityUtils.getQueryBuilder(entityClass);

        //precizez tipul de query ->UPDATE
        queryBuilder.setQueryType(QueryType.UPDATE);

        try(Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            //iterez List<ColumnInfo> pentru a
            //  1. actualiza valoare din columnInfo
            //  2. pentru a determina un obiect de tip Condition
            for (ColumnInfo columnInfo : queryBuilder.getQueryColumns()) {
                String fieldName = columnInfo.getColumnName();
                Field field = entityClass.getDeclaredField(fieldName);

                //pentru a accesa proprietatea private trebuie sa o facem accesibila
                field.setAccessible(true);
                //obtin valoarea proprietatii
                Object propertyVal=field.get(entity);
                columnInfo.setValue(propertyVal);

                //construim un Condition pe baza cheii primare si il adaug la queryBuilder
                if(columnInfo.isId()){
                    queryBuilder.addCondition(SQLHelper.condition(columnInfo));

                }
            }
            System.out.println(queryBuilder.createQuery());
            int rows=stmt.executeUpdate(queryBuilder.createQuery())  ;

            //rows =1 -> s-a executat update-ul altfel nu
            if(rows>0){
                return entity;
            }

        } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
            LOG.error(e);
        }


        throw new RuntimeException("Nu s-a executat!!!");
    }

    @Override
    public void delete(T entity) {
        //obtin query builder populat cu List<ColumnInfo> si table name
        QueryBuilder queryBuilder = EntityUtils.getQueryBuilder(entityClass);

        //precizez tipul de query ->DELETE
        queryBuilder.setQueryType(QueryType.DELETE);
        try(Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            //iterez List<ColumnInfo> pentru a
            //  1. actualiza valoare din columnInfo
            //  2. pentru a determina un obiect de tip Condition
            for (ColumnInfo columnInfo : queryBuilder.getQueryColumns()) {


                //construim un Condition pe baza cheii primare si il adaug la queryBuilder
                if(columnInfo.isId()){
                    String fieldName = columnInfo.getColumnName();
                    Field field = entityClass.getDeclaredField(fieldName);

                    //pentru a accesa proprietatea private trebuie sa o facem accesibila
                    field.setAccessible(true);
                    //obtin valoarea proprietatii
                    Object propertyVal=field.get(entity);
                    columnInfo.setValue(propertyVal);
                    queryBuilder.addCondition(SQLHelper.condition(columnInfo));
                    break;

                }
            }
            System.out.println(queryBuilder.createQuery());
            int rows=stmt.executeUpdate(queryBuilder.createQuery())  ;
           if(rows==0){
               throw new RuntimeException("Nu s-a executat");
           }

        } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
            LOG.error(e);
        }

    }

    @Override
    public List<T> findByParams(Map<String, Object> params) {
        List<T> ret=new ArrayList<>();
        QueryBuilder queryBuilder = EntityUtils.getQueryBuilder(entityClass);
        queryBuilder.setQueryType(QueryType.SELECT);
        for (String col : params.keySet()) {
            Condition condition=new Condition();
            condition.setColumnName(col);
            condition.setValue(params.get(col));
            queryBuilder.addCondition(condition);
        }

        try(Connection con = DBManager.getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs=stmt.executeQuery(queryBuilder.createQuery());
            while(rs.next()){
                T inst=entityClass.newInstance();
                for (ColumnInfo columnInfo : queryBuilder.getQueryColumns()) {
                    Object dbVal=rs.getObject(columnInfo.getDbColumnName());
                    Object javaVal=EntityUtils.castFromSqlType(dbVal,columnInfo.getColumnType());
                    Field field=entityClass.getDeclaredField(columnInfo.getColumnName());
                    field.setAccessible(true);
                    field.set(inst,javaVal);
                }

                ret.add(inst);
            }

        } catch (NoSuchFieldException | IllegalAccessException | SQLException | InstantiationException e) {
            LOG.error(e);
        }
        return ret;

    }


}
