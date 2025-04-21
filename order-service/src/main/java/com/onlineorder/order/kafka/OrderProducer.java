package com.onlineorder.order.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineorder.order.model.Order;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendOrder(Order order) {
        try {
            String message = objectMapper.writeValueAsString(order);
            // Use order id as key to control partitioning
            ProducerRecord<String, String> record = new ProducerRecord<>("order-topic", order.getId(), message);
            kafkaTemplate.send(record).addCallback(new MyListenableFutureCallback(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private static class MyListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
        private final String message;

        public MyListenableFutureCallback(String message) {
            this.message = message;
        }

        @Override
        public void onSuccess(SendResult<String, String> result) {
            System.out.println("成功发送消息: " + message);
        }

        @Override
        public void onFailure(Throwable ex) {
            System.err.println("发送失败: " + ex.getMessage());
        }
    }
}
