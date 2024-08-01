package io.conduktor.demos.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

//adding this shutdown hook so that we can properly shutdown the consumer as we previously did for producer.
public class ConsumerDemoWithShutdown {

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

//        get a reference to the main thread
        final Thread thread = Thread.currentThread();

//        adding the shutdown hook
//        The shutdown hook is a special thread that will run when the JVM is shutting down.
//        Shutdown Hook:
//        Inside the hook:
//              consumer.wakeup() is called to interrupt any ongoing poll operation and exit the loop gracefully.
//              thread.join() is called to wait for the main thread to finish its execution,
//              ensuring that all the necessary clean-up operations in the main thread are completed before the JVM exits.
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                logger.info("Detected a shutdown, let's exit by calling consumer.wakeup()......");
//             when we do a consumer.wakeup() next time when we will do consumer.poll() in our code
//             this is going to throw a wakeup exception.
                consumer.wakeup();

//                join the main thread to allow the execution of the code in the main thread
                try{
                    thread.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        try {
//        subscribe to a topic
            consumer.subscribe(List.of(topic));

//        poll for a data
            while (true) {
                logger.info("Polling");

                ConsumerRecords<String, String> record = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> records : record) {
                    logger.info("Key: " + records.key() + " Value: " + records.value());
                    logger.info("Partition: " + records.partition() + " Offset: " + records.offset());
                }
            }
        }catch (WakeupException e){
            logger.info("Consumer is starting to shut down");
        }catch (Exception e){
            logger.info("Unxpected Exception");
        }finally {
            consumer.close();   // close the consumer and this will also commit offsets
            logger.info("The consumer is now gracefully shutdown");
        }
    }
}

//    Key Points
//    Graceful Shutdown: The shutdown hook ensures the consumer can shut down gracefully by using consumer.wakeup() and joining the main thread.
//        Polling Loop: The consumer continuously polls for messages and processes them until an interruption occurs.
//        Resource Management: The finally block ensures that the consumer is properly closed, even if an exception occurs.
