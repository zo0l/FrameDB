package farmtotable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String URL =
        "jdbc:sqlserver://localhost\\SQLEXPRESS;"
        + "databaseName=FarmToTable;"
        + "user=sa;"
        + "password=1234;"
        + "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    
    //get datat from db
    public static Object[][] getData(String sql) {

        List<Object[]> rows = new ArrayList<>();

        try (
            Connection c = getConnection();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql)
        ) {

            int cols = r.getMetaData().getColumnCount();

            while (r.next()) {

                Object[] row = new Object[cols];

                for (int i = 0; i < cols; i++) {
                    row[i] = r.getObject(i + 1);
                }

                rows.add(row);
            }

        } catch (Exception e) {

            javax.swing.JOptionPane.showMessageDialog(
                null,
                "Error: " + e.getMessage()
            );
        }

        return rows.toArray(new Object[0][]);
    }
    
    
    // any update in db
public static boolean runUpdate(String sql) {
        try (Connection c = getConnection();
             java.sql.Statement s = c.createStatement()) {
            s.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }
}