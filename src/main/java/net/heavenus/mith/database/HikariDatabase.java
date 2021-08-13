package net.heavenus.mith.database;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.database.data.DataTable;

public class HikariDatabase {
    private String host;
    private String port;
    private String dbname;
    private String username;
    private String password;
    private boolean mariadb;

    private HikariDataSource dataSource;
    private ExecutorService executor;

    public HikariDatabase(String host, String port, String dbname, String username, String password, boolean mariadb) {
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.username = username;
        this.password = password;
        this.mariadb = mariadb;

        this.openConnection();
        this.executor = Executors.newCachedThreadPool();

        DataTable.listTables().forEach(table -> {
            this.update(table.getInfo().create());
            table.init(this);
        });
    }

    public void openConnection() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("mConnectionPool");
        config.setMaximumPoolSize(32);
        config.setConnectionTimeout(30000L);
        config.setDriverClassName(this.mariadb ? "org.mariadb.jdbc.Driver" : "com.mysql.jdbc.Driver");
        config.setJdbcUrl((this.mariadb ? "jdbc:mariadb://" : "jdbc:mysql://") + this.host + ":" + this.port + "/" + this.dbname);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.addDataSourceProperty("autoReconnect", "true");
        this.dataSource = new HikariDataSource(config);

        BotSync.getInstance().getLogger().info("Database: Conectado ao MySQL!");
    }

    public void closeConnection() {
        if (isConnected()) {
            this.dataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public boolean isConnected() {
        return !this.dataSource.isClosed();
    }

    public void update(String sql, Object... vars) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            BotSync.getInstance().getLogger().info("Database: Nao foi possivel executar um SQL: ");
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null && !ps.isClosed())
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void execute(String sql, Object... vars) {
        executor.execute(() -> {
            update(sql, vars);
        });
    }

    public int updateWithInsertId(String sql, Object... vars) {
        int id = -1;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            ps.execute();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            BotSync.getInstance().getLogger().info("Database: Nao foi possivel executar um SQL: ");
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null && !ps.isClosed())
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rs != null && !rs.isClosed())
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    public CachedRowSet query(String query, Object... vars) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CachedRowSet rowSet = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(query);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            rs = ps.executeQuery();
            rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(rs);

            if (rowSet.next()) {
                return rowSet;
            }
        } catch (SQLException ex) {
            BotSync.getInstance().getLogger().info("Database: Nao foi possivel executar um Requisicao: ");
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null && !ps.isClosed())
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rs != null && !rs.isClosed())
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
