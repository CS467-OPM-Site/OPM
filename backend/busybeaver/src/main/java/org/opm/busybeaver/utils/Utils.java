package org.opm.busybeaver.utils;

import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.enums.ErrorMessageConstants;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public final class Utils {
    public static @NotNull HashMap<String, Object> generateExceptionResponse(String message, Integer errorCode) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ErrorMessageConstants.MESSAGE.getValue(), message);
        result.put(ErrorMessageConstants.CODE.getValue(), errorCode);

        return result;
    }

    public static @NotNull Long calculateTimeLengthMillis(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toMillis();
    }
}
