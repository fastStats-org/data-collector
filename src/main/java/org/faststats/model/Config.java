package org.faststats.model;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Config(
        @SerializedName("metrics-port") int metricsPort,
        @SerializedName("connection-string") String connectionString,
        @SerializedName("access-control-allow-origin") String accessControlAllowOrigin
) {
}
