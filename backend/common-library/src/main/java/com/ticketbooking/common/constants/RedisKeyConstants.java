package com.ticketbooking.common.constants;

public final class RedisKeyConstants {
    private RedisKeyConstants() {}

    // Redis Distributed Lock: lock:seat:{tripId}:{seatNumber}
    public static final String LOCK_SEAT_PREFIX = "lock:seat:";

    // Redis Hold Key: hold:seat:{tripId}:{seatNumber} (TTL: 300s)
    public static final String HOLD_SEAT_PREFIX = "hold:seat:";

    // Trip Catalog Cache
    public static final String CACHE_TRIP_PREFIX = "cache:trip:";

    // Rate Limiter Key Prefix
    public static final String RATE_LIMIT_PREFIX = "rate:hold:";

    public static String getLockSeatKey(String tripId, String seatNumber) {
        return LOCK_SEAT_PREFIX + tripId + ":" + seatNumber;
    }

    public static String getHoldSeatKey(String tripId, String seatNumber) {
        return HOLD_SEAT_PREFIX + tripId + ":" + seatNumber;
    }

    public static String getTripCacheKey(String tripId) {
        return CACHE_TRIP_PREFIX + tripId;
    }
}
