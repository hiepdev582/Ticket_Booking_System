package com.ticketbooking.booking.config;

import com.ticketbooking.common.constants.KafkaTopicConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic seatHoldTopic() {
        return TopicBuilder.name(KafkaTopicConstants.SEAT_HOLD_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderPaidTopic() {
        return TopicBuilder.name(KafkaTopicConstants.ORDER_PAID_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic seatReleasedTopic() {
        return TopicBuilder.name(KafkaTopicConstants.SEAT_RELEASED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
