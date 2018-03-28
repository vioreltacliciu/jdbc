package ro.db.api.em;

import java.util.ArrayList;
import java.util.List;

/**
 * Developer: Viorelt
 * <p>
 **/

public class QueryBuilder {

    private String tableName;
    private List<ColumnInfo> queryColumns=new ArrayList<>();
    private QueryType queryType;
    private List<Condition> conditions=new ArrayList<>();

    public QueryBuilder addCondition(Condition condition) {
        conditions.add(condition);
        return this;
    }

    public QueryBuilder setTableName(String tableName){
        this.tableName=tableName;
        return this;
    }

    public QueryBuilder setQueryType(QueryType queryType){
        this.queryType=queryType;
        return this;
    }

    public QueryBuilder addQueryColumns(List<ColumnInfo> columnInfos){
        this.queryColumns.addAll(columnInfos);
        return this;
    }

    private String createSelectQuery(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select ");
        queryColumns(stringBuilder);
        stringBuilder.append(" from ");
        queryTable(stringBuilder);

        queryFilter(stringBuilder);

        return stringBuilder.toString();
    }

    private void queryColumns(StringBuilder stringBuilder) {
        for (int i = 0, queryColumnsSize = queryColumns.size(); i < queryColumnsSize; i++) {
            ColumnInfo queryColumn = queryColumns.get(i);
            if(i>0){
                stringBuilder.append(", ");
            }
            stringBuilder.append(queryColumn.getDbColumnName());

        }
    }

    private void queryTable(StringBuilder stringBuilder) {
        stringBuilder.append(" ").append(tableName);
    }

    private void queryFilter(StringBuilder stringBuilder) {
        if(conditions.size()>0){
            stringBuilder.append(" where ");

        }

        for (int i = 0, conditionsSize = conditions.size(); i < conditionsSize; i++) {
            Condition condition = conditions.get(i);
            if(i>0){
                stringBuilder.append(" and ");
            }
            stringBuilder.append(condition.getColumnName()).append("=");
            stringBuilder.append(SQLHelper.prepareForSql(condition.getValue()));
        }
    }

    public String createQuery(){
        switch (queryType){
            case DELETE:
                return createDeleteQuery();
            case INSERT:
                return createInsertQuery();
            case SELECT:
                return createSelectQuery();
            case UPDATE:
                return createUpdateQuery();
                default:
                    throw new IllegalStateException("Caz netratat!!");
        }
    }

    private String createDeleteQuery(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("delete from ");
        queryTable(stringBuilder);
        queryFilter(stringBuilder);
        return stringBuilder.toString();
    }

    private String createInsertQuery(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("insert into ");
        queryTable(stringBuilder);
        stringBuilder.append("(");
        queryColumns(stringBuilder);
        stringBuilder.append(") values( ");
        for (int i = 0; i < queryColumns.size(); i++) {
            ColumnInfo queryColumn = queryColumns.get(i);
            if(i>0){
                stringBuilder.append(",");
            }
            stringBuilder.append(SQLHelper.prepareForSql(queryColumn.getValue()));
        }
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private String createUpdateQuery(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("update ");
        queryTable(stringBuilder);
        stringBuilder.append(" set ");
        for (int i = 0; i < queryColumns.size(); i++) {
            ColumnInfo queryColumn = queryColumns.get(i);
            if(i>0){
                stringBuilder.append(",");
            }
            stringBuilder.append(queryColumn.getDbColumnName()).append("=");
            //ne protejam la null -> set col=null
            if(queryColumn.getValue()==null){
                stringBuilder.append("null");
            }else{
                stringBuilder.append(SQLHelper.prepareForSql(queryColumn.getValue()));
            }
        }
        queryFilter(stringBuilder);

        return stringBuilder.toString();
    }

    public List<ColumnInfo> getQueryColumns() {
        return queryColumns;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public List<Condition> getConditions() {
        return conditions;
    }
}
