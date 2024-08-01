package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoKeys {

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

//        you will observer here that keys will be go to same partition for example when j=0 id_1,id_3,id_5 goes to partition 1
//        and when j=1 id_1,id_3,id_5 will go again to partition 1
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 20; i++) {

                String topic = "demo_java";
                String key = "id_" + i;
                String value = "hello world " + i;
                //        create the producer record
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

//        send data -- asynchronous
                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//                execute everytime a record successfully sent or an exception is thrown
                        if (e == null) {
//                    the record was successfully sent
                            logger.info("Key: " + key + " | Partition: " + recordMetadata.partition());
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

}
