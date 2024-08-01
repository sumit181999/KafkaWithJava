package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        logger.info("I am a kafka producer");
//        create producer properties
        Properties properties = new Properties();

//        connect to localhost
        properties.setProperty("bootstrap.servers","127.0.0.1:9092");

//        set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

//        create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

//        create the producer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java","hello world");

//        send data -- asynchronous
        producer.send(producerRecord);

//        tell the producer to send all data and block until done -- synchronous
        producer.flush();

//        flush and close the producer
//        so when you call producer.close() it will automatically call producer.flush() so you don't need to mention it explicitly.
        producer.close();

    }
}
