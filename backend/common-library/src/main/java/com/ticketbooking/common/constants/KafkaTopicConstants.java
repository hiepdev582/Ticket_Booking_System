package com.ticketbooking.common.constants;

public final class KafkaTopicConstants {
    private KafkaTopicConstants() {}

    public static final String SEAT_HOLD_TOPIC = "seat-hold-events";
    public static final String ORDER_PAID_TOPIC = "order-paid";
    public static final String SEAT_RELEASED_TOPIC = "seat-released-events";
    public static final String TICKET_GENERATED_TOPIC = "ticket-generated-events";
}
