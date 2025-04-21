package org.faststats.controller;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class DatabaseController extends SQLController {
    public DatabaseController(String url) {
        super(url);
    }
    // public boolean insertMetric(Metric metric) throws SQLException {
    //     var serverId = metric.consumerId(); // get id from uuid
    //     return executeUpdate(INSERT_METRIC, serverId, metric.projectId()) > 0;
    // }
}
