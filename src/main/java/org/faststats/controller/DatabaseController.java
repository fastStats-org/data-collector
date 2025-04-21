package org.faststats.controller;

import org.jspecify.annotations.NullMarked;

import java.io.File;

@NullMarked
public class DatabaseController extends SQLController {
    public DatabaseController(File file) {
        super(file);
    }

    // public boolean insertMetric(Metric metric) throws SQLException {
    //     var serverId = metric.consumerId(); // get id from uuid
    //     return executeUpdate(INSERT_METRIC, serverId, metric.projectId()) > 0;
    // }
}
