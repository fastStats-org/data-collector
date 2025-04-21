package org.faststats.controller;

import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
class SQLController implements AutoCloseable {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Connection connection;

    protected SQLController(String url) {
        try {
            this.connection = DriverManager.getConnection(url);
            logger.info("Successfully connected to database");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
        try {
            executeUpdate(statement("sql/table/servers.sql"));
            executeUpdate(statement("sql/table/projects.sql")); // moved to faststats monorepo
            executeUpdate(statement("sql/table/metrics.sql"));
            executeUpdate(statement("sql/index/metrics.sql"));
            logger.info("Successfully setup database");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to setup database", e);
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    protected List<Project> readProjects(ResultSet resultSet) throws SQLException {
        var projects = new ArrayList<Project>();
        while (resultSet.next()) {
            var project = readProject(resultSet);
            projects.add(project);
        }
        return projects;
    }

    protected Project readProject(ResultSet resultSet) throws SQLException {
        return new Project(resultSet.getInt("project_id"));
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected <T> @Nullable T executeQuery(String sql, ThrowingFunction<ResultSet, T> mapper, @Nullable Object... parameters) throws SQLException {
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (var i = 0; i < parameters.length; i++)
                preparedStatement.setObject(i + 1, parameters[i]);
            try (var resultSet = preparedStatement.executeQuery()) {
                return ThrowingFunction.unchecked(mapper).apply(resultSet);
            }
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected int executeUpdate(String sql, @Nullable Object... parameters) throws SQLException {
        try (var statement = connection.prepareStatement(sql)) {
            for (var i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            return statement.executeUpdate();
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected int executeUpdateGetKey(String sql, @Nullable Object... parameters) throws SQLException {
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (var i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            if (statement.executeUpdate() == 0) throw new SQLException("No rows affected");
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) return generatedKeys.getInt(1);
            }
            throw new SQLException("Statement returns no generated keys");
        }
    }

    private static String statement(String file) {
        try (var resource = SQLController.class.getClassLoader().getResourceAsStream(file)) {
            if (resource == null) throw new FileNotFoundException("Resource not found: " + file);
            try (var reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        @Nullable
        R apply(T t) throws SQLException;

        static <T, R> ThrowingFunction<T, R> unchecked(ThrowingFunction<T, R> f) {
            return f;
        }
    }
}
