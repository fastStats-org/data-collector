package org.faststats;

import org.faststats.controller.DatabaseController;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterEach;

import java.sql.SQLException;

@NullMarked
public class BaseTest {
    private final String url = "jdbc:postgresql://postgres:5432/data?user=postgres&password=postgres";
    protected DatabaseController database = new DatabaseController(url);

    @AfterEach
    public void cleanupDatabase() throws SQLException {
        database.close();
        database = new DatabaseController(url);
    }
}