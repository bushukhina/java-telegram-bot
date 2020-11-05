package storage;

import java.sql.*;

public class DataBase {
    private Connection connection;

    public void connect() {
        try {
            String dbUrl = System.getenv("JDBC_DATABASE_URL");
            connection = DriverManager.getConnection(dbUrl, "postgres", "");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void reconnect() {
        try {
            connection.close();
            connect();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void initDataBase() {
        try {
            if (connection.isClosed())
                reconnect();

            Statement stmt = connection.createStatement();
            String createEnumType = "CREATE TYPE game_state AS ENUM('notStarted', 'smallBlind', " +
                    "'bigBlind', 'distribution', 'preTrade', 'flop', 'tradeSecond', 'tern', 'tradeThird', " +
                    "'river', 'tradeFourth', 'showDown');";
            stmt.executeUpdate(createEnumType);
            String createGameTable = "CREATE TABLE IF NOT EXISTS game(" +
                            "id INT PRIMARY KEY NOT NULL, " +
                            "code UUID NOT NULL, " +
                            "state game_state);";
            stmt.executeUpdate(createGameTable);
            String createUserTable = "CREATE TABLE IF NOT EXISTS user(" +
                    "id INT PRIMARY KEY NOT NULL, " +
                    "firstName TEXT, " +
                    "chatId INT," +
                    "money INT," +
                    "bet INT," +
                    "isActive BOOLEAN);";
            stmt.executeUpdate(createUserTable);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
