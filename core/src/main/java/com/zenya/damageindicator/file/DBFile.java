package com.zenya.damageindicator.file;

import com.zenya.damageindicator.DamageIndicator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DBFile extends StorageFile {

    private static boolean loaded = false;

    public DBFile(String fileName) {
        this(DamageIndicator.INSTANCE.getDataFolder().getPath(), fileName);
    }

    public DBFile(String directory, String fileName) {
        this(directory, fileName, null, false);
    }

    public DBFile(String directory, String fileName, Integer fileVersion, boolean resetFile) {
        super(directory, fileName, fileVersion, resetFile);
        try {
            Class.forName("org.sqlite.JDBC");
            loaded = true;
        } catch (ClassNotFoundException e) {
            DamageIndicator.INSTANCE.getLogger().severe("Failed to initialize database! Toggles will not persist through server restarts.");
        }
        if (loaded && !file.exists()) {
            this.createTables();
        }
    }

    private static Connection connect() {
        if (!loaded)
            return null;
        String url = "jdbc:sqlite:" + DamageIndicator.INSTANCE.getDataFolder() + File.separator + "database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void sendStatement(String sql) {
        sendPreparedStatement(sql);
    }

    private static void sendPreparedStatement(String sql, Object... parameters) {
        sendQueryStatement(sql, null, parameters);
    }

    private static Object sendQueryStatement(String sql, String query, Object... parameters) {
        Object result = null;

        try {
            Connection conn = connect();
            if (conn == null) return null;
            if ((parameters == null || parameters.length == 0) && query == null) {
                //Simple statement
                Statement statement = conn.createStatement();
                statement.execute(sql);
            } else {
                PreparedStatement ps = conn.prepareStatement(sql);
                for (int i = 0; i < parameters.length; i++) {
                    ps.setObject(i + 1, parameters[i]);
                }

                if (query == null) {
                    //Prepared statement
                    ps.execute();
                } else {
                    //Query statement
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) result = rs.getObject(query);
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS damageindicator ("
                + "id integer PRIMARY KEY AUTOINCREMENT, "
                + "player text NOT NULL UNIQUE, "
                + "toggle tinyint NOT NULL);";

        sendStatement(sql);
    }

    public void initData(UUID uuid) {
        String sql = "INSERT OR IGNORE INTO damageindicator(player, toggle) VALUES(?, ?)";
        sendPreparedStatement(sql, uuid, 1);
    }

    public boolean getToggleStatus(UUID uuid) {
        initData(uuid);
        boolean status = false;

        String sql = "SELECT toggle FROM damageindicator WHERE player = ?";
        Object toggleInt = sendQueryStatement(sql, "toggle", uuid);

        if (toggleInt instanceof Integer) {
            status = (Integer) toggleInt == 1;
        }
        return status;
    }

    public void setToggleStatus(UUID uuid, boolean status) {
        initData(uuid);
        int toggleInt = status ? 1 : 0;

        String sql = "UPDATE damageindicator SET toggle = ? WHERE player = ?";
        sendPreparedStatement(sql, toggleInt, uuid);
    }

}
