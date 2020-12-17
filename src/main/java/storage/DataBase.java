package storage;

import java.sql.*;

public class DataBase {
    private Connection connection;

    public void connect() {
        try {
            String dbUrl = System.getenv("JDBC_DATABASE_URL");
            connection = DriverManager.getConnection(dbUrl, "postgres", "");
            initDataBase();
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

            createGameTable(stmt);
            createUsersTable(stmt);
            createCardsTable(stmt);
            createGameUsersTable(stmt);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createGameTable(Statement stmt) throws SQLException {
        String createEnumType = "DO $$ BEGIN\n" +
                "    CREATE TYPE game_state AS ENUM('notStarted', 'smallBlind', 'bigBlind', 'distribution', 'preTrade', 'flop', 'tradeSecond', 'tern', 'tradeThird', 'river', 'tradeFourth', 'showDown');\n" +
                "EXCEPTION\n" +
                "    WHEN duplicate_object THEN null;\n" +
                "END $$;";
        stmt.executeUpdate(createEnumType);
        String createGameTable = "CREATE TABLE IF NOT EXISTS game(" +
                "id serial, " +
                "code UUID NOT NULL, " +
                "state game_state," +
                "PRIMARY KEY (id));";
        stmt.executeUpdate(createGameTable);
    }

    private void createUsersTable(Statement stmt) throws SQLException {
        String createUserTable = "CREATE TABLE IF NOT EXISTS users(" +
                "id serial, " +
                "firstName TEXT, " +
                "chatId TEXT, " +
                "money INT, " +
                "bet INT, " +
                "isActive BOOLEAN," +
                "PRIMARY KEY (id));";
        stmt.executeUpdate(createUserTable);
    }

    private void createCardsTable(Statement stmt) throws SQLException {
        String createCardsTable = "CREATE TABLE IF NOT EXISTS cards(" +
                "id serial, " +
                "suit TEXT, " +
                "rank TEXT," +
                "user_id int NOT NULL," +
                "PRIMARY KEY (id)," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);";
        stmt.executeUpdate(createCardsTable);
    }

    private void createGameUsersTable(Statement stmt) throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS game_users(" +
                "game_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "PRIMARY KEY (game_id, user_id)," +
                "FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE);";
        stmt.executeUpdate(createTable);
    }
}
