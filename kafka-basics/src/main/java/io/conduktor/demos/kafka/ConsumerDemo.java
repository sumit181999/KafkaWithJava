package io.conduktor.demos.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ConsumerDemo {

    private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        logger.info("I am a kafka consumer");

        String groupId = "my-java-application";
        String topic = "demo_java";

//        create producer properties
        Properties properties = new Properties();

//        connect to localhost
        properties.setProperty("bootstrap.servers","127.0.0.1:9092");

//        create consumer config
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());

        properties.setProperty("group.id",groupId);

//        it is of three types none/earliest/latest. none means that if we don't have any consumer groups.
//        earliest means the beginning of the topic
//        latest corresponds to just now message
        properties.setProperty("auto.offset.reset", "earliest");

//        create a consumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);

//        subscribe to a topic
        consumer.subscribe(List.of(topic));

//        poll for a data
        while(true){
            logger.info("Polling");

            ConsumerRecords<String,String> record = consumer.poll(Duration.ofMillis(1000));
            for(ConsumerRecord<String,String> records : record){
                logger.info("Key: "+records.key()+" Value: "+records.value());
                logger.info("Partition: "+records.partition()+" Offset: "+records.offset());
            }
        }



    }

}
