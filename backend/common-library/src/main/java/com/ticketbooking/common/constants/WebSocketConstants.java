package com.ticketbooking.common.constants;

public final class WebSocketConstants {
    private WebSocketConstants() {}

    public static final String WS_ENDPOINT = "/ws";
    public static final String APP_DESTINATION_PREFIX = "/app";
    public static final String TOPIC_DESTINATION_PREFIX = "/topic";

    public static final String SEAT_UPDATE_TOPIC_PREFIX = "/topic/trips/";
    public static final String SEAT_UPDATE_TOPIC_SUFFIX = "/seats";

    public static String getTripSeatsTopic(String tripId) {
        return SEAT_UPDATE_TOPIC_PREFIX + tripId + SEAT_UPDATE_TOPIC_SUFFIX;
    }
}
