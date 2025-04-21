package org.faststats;

import core.file.format.GsonFile;
import core.io.IO;
import io.javalin.Javalin;
import org.faststats.controller.DatabaseController;
import org.faststats.model.Config;
import org.faststats.route.metric.MetricsRoute;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

@NullMarked
public class FastStats {
    public static final Config CONFIG = new GsonFile<>(IO.of("data", "config.json"), new Config(
            5000, "jdbc:postgresql://postgres:5432/data?user=postgres&password=postgres", "*"
    )).validate().save().getRoot();

    public static final DatabaseController DATABASE = new DatabaseController(CONFIG.connectionString());

    private final Javalin javalin = Javalin.create(config -> {
        config.showJavalinBanner = false;
        config.useVirtualThreads = false;
    });

    public static void main(String[] args) {
        var fastStats = new FastStats();
        fastStats.registerRoutes();
        fastStats.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DATABASE.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to properly close database", e);
            }
        }, "shutdown-hook"));
    }

    private void registerRoutes() {
        MetricsRoute.register(javalin);
    }

    private void start() {
        javalin.start(FastStats.CONFIG.metricsPort());
    }
}
