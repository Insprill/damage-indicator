/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2024  Pierce Thompson
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zenya.damageindicator.file;

import com.google.common.base.Preconditions;
import com.zenya.damageindicator.DamageIndicator;
import org.intellij.lang.annotations.Language;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DBFile extends StorageFile {

    private static boolean loaded = false;

    private final Thread mainThread;
    private final ExecutorService dbThread;

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
        mainThread = Thread.currentThread();
        dbThread = Executors.newSingleThreadExecutor();
        if (loaded && !file.exists()) {
            runOnDbThread(this::createTables);
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

    private void sendStatement(String sql) {
        sendPreparedStatement(sql);
    }

    private void sendPreparedStatement(String sql, Object... parameters) {
        sendQueryStatement(sql, null, parameters);
    }

    private Object sendQueryStatement(String sql, String query, Object... parameters) {
        Object result = null;

        Preconditions.checkState(Thread.currentThread() != mainThread, "Database operations must be run on the database thread!");
        Preconditions.checkState(!dbThread.isShutdown() && !dbThread.isTerminated(), "Database thread is not running! Database operations cannot be performed after shutdown is called.");

        try (Connection conn = connect()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void shutdown() {
        if (dbThread.isShutdown() || dbThread.isTerminated()) return;
        dbThread.shutdown();
        try {
            if (!dbThread.awaitTermination(10, TimeUnit.SECONDS)) {
                dbThread.shutdownNow();
            }
        } catch (InterruptedException ie) {
            dbThread.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void runOnDbThread(Runnable runnable) {
        dbThread.submit(runnable);
    }

    public void createTables() {
        @Language("sqlite")
        String sql = "CREATE TABLE IF NOT EXISTS damageindicator ("
                + "id integer PRIMARY KEY AUTOINCREMENT, "
                + "player text NOT NULL UNIQUE, "
                + "toggle tinyint NOT NULL);";

        sendStatement(sql);
    }

    public void initData(UUID uuid) {
        @Language("sqlite")
        String sql = "INSERT OR IGNORE INTO damageindicator(player, toggle) VALUES(?, ?)";
        sendPreparedStatement(sql, uuid, 1);
    }

    public boolean getToggleStatus(UUID uuid) {
        initData(uuid);
        boolean status = false;

        @Language("sqlite")
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

        @Language("sqlite")
        String sql = "UPDATE damageindicator SET toggle = ? WHERE player = ?";
        sendPreparedStatement(sql, toggleInt, uuid);
    }

}
