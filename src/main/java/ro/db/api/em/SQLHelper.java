package ro.db.api.em;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Developer: Viorelt
 * <p>
 **/

public class SQLHelper {

    private static final String MM_DD_YYYY_JAVA = "MM-dd-yyyy";
    private static final String MM_DD_YYYY_DB = "mm-dd-yyyy";

    private static String prepareForSql(String value){
        return "'"+value+"'";
    }
    private static String prepareForSql(Date value){
        DateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY_JAVA);
        return "TO_DATE('"+dateFormat.format(value)+"','"+MM_DD_YYYY_DB+"')";
    }

    public static String prepareForSql(Object value){
        if(value instanceof Date){
            return prepareForSql((Date) value);
        }
        if(value instanceof String){
            return prepareForSql((String)value);
        }

        return value.toString();
    }


    public static Condition condition(ColumnInfo columnInfo){
        Condition condition = new Condition();
        condition.setColumnName(columnInfo.getDbColumnName());
        condition.setValue(columnInfo.getValue());
        return condition;
    }

    public static void main(String[] args) {
        System.out.println(prepareForSql(new Date()));
    }

}
