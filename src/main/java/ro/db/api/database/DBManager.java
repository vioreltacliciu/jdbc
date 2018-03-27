package ro.db.api.database;

import java.sql.*;

/**
 * Developer: Viorelt
 * <p>
 **/

public class DBManager {
    public static final String CONNECTION_STRING = "jdbc:postgresql://" +
            DBProperties.IP + ":" + DBProperties.PORT + "/" + DBProperties.DB_NAME;

    private static void registerDriver() {
        try {
            Class.forName(DBProperties.DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        registerDriver();
        Connection ret = null;
        ret = DriverManager.getConnection(CONNECTION_STRING, DBProperties.USER, DBProperties.PASS);

        return ret;
    }

    public static String checkConnection() {
        try (Connection connection=getConnection();
             Statement stmt = connection.createStatement()) {

            String hw = "Hello World";
            ResultSet rs = stmt.executeQuery("SELECT '" + hw + "' WHERE 1 = 1");
            Object ret = rs.getObject(1);
            return ret.toString();



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        checkConnection();
    }
}
