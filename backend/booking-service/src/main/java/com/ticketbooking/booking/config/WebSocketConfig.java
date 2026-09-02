package com.ticketbooking.booking.config;

import com.ticketbooking.common.constants.WebSocketConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cho phép gửi message đến các topic bắt đầu bằng /topic
        config.enableSimpleBroker(WebSocketConstants.TOPIC_DESTINATION_PREFIX);
        // Prefix cho các message từ client gửi lên server
        config.setApplicationDestinationPrefixes(WebSocketConstants.APP_DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket kết nối từ Frontend Nuxt 3
        registry.addEndpoint(WebSocketConstants.WS_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Thêm endpoint chuẩn native WebSocket không SockJS
        registry.addEndpoint(WebSocketConstants.WS_ENDPOINT)
                .setAllowedOriginPatterns("*");
    }
}
