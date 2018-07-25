package ro.cucumber.core.clients.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlClient {
    private static int MAX_ROWS = 100;
    private String url;
    private String user;
    private String pwd;
    private Connection conn;

    private MysqlClient(Builder builder) {
        this.url = builder.url;
        this.user = builder.user;
        this.pwd = builder.pwd;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.conn = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, String>> executeQuery(String sql) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.setMaxRows(MAX_ROWS);
            rs = st.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            List<Map<String, String>> tableData = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> rowData = new HashMap<>();
                for (int i = 1; i <= columns; i++) {
                    Object value = rs.getObject(i);
                    rowData.put(md.getColumnName(i), value != null ? value.toString() : null);
                }
                tableData.add(rowData);
            }
            return tableData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Builder {
        private String url;
        private String user;
        private String pwd;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder pwd(String pwd) {
            this.pwd = pwd;
            return this;
        }

        public MysqlClient build() {
            return new MysqlClient(this);
        }
    }
}


