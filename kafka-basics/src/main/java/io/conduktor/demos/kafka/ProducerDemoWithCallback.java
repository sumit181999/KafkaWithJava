package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private static final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    public static void main(String[] args) {

        logger.info("I am a kafka producer");
//        create producer properties
        Properties properties = new Properties();

//        connect to localhost
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

//        set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

//        create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

//        you can see that all values will be going to same partition even if we have more than one partition that is because of
//        sticky partition. with this there is performance improvement.
        for (int i = 0; i < 20; i++) {
            //        create the producer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "hello world"+i);

//        send data -- asynchronous
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//                execute everytime a record successfully sent or an exception is thrown
                    if (e == null) {
//                    the record was successfully sent
                        logger.info("Received new metdata \n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n");
                    } else {
                        logger.info("Error while producing", e);
                    }

                }
            });
        }

//        tell the producer to send all data and block until done -- synchronous
        producer.flush();

//        flush and close the producer
//        so when you call producer.close() it will automatically call producer.flush() so you don't need to mention it explicitly.
        producer.close();

    }
}
