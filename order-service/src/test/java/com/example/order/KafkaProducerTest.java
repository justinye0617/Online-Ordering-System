package com.example.order;

import com.example.order.kafka.OrderProducer;
import com.example.order.model.Order;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "order-topic" })
public class KafkaProducerTest {

    private final EmbeddedKafkaBroker embeddedKafka;
    private final OrderProducer orderProducer;

    public KafkaProducerTest(EmbeddedKafkaBroker embeddedKafka, OrderProducer orderProducer) {
        this.embeddedKafka = embeddedKafka;
        this.orderProducer = orderProducer;
    }

    @Test
    public void testKafkaSendReceive() {
        Order order = new Order("T001", "奶茶", 1);
        orderProducer.sendOrder(order);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "order-topic");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "order-topic");
        assertThat(record.value()).contains("奶茶");
    }
}
