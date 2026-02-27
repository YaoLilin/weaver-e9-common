package com.customization.yll.common.manager;

import org.apache.kafka.clients.producer.*;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author yaolilin
 * @desc Kafka 消息推送
 * @date 2024/10/31
 **/
public class KafkaManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Properties props;
    private final String serverUrl;
    private final String topic;
    private final String key;

    public KafkaManager(String serverUrl, String topic, String key) {
        this.serverUrl = serverUrl;
        this.topic = topic;
        this.key = key;
        initProps();
    }

    public KafkaManager( String serverUrl, String topic, String key,Properties props) {
        this.serverUrl = serverUrl;
        this.topic = topic;
        this.key = key;
        this.props = props;
        initProps();
    }

    private void initProps() {
        if (props == null) {
            props = new Properties();
            //所有follower都响应了才认为消息提交成功，即"committed"
            props.put("acks", "all");
            //retries = MAX 无限重试，直到你意识到出现了问题:)
            props.put("retries", 5);
            //batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
            props.put("batch.size", 16384);
            //producer可以用来缓存数据的内存大小。
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        }
        props.put("bootstrap.servers", serverUrl);
    }

    /**
     * 发送消息（同步）
     * @param data 消息内容
     * @return 是否成功
     */
    public boolean produce(String data) {
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            logger.info("kafka 发送消息");
            // 同步调用
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, data);
            RecordMetadata metadata = producer.send(producerRecord).get();
            if (metadata == null) {
                logger.error("发送消息失败，RecordMetadata 为 null");
                return false;
            }
            logger.info("同步发送后获得分区为 :" + metadata.partition() + "，同步发送后获得offset为 :" + metadata.offset());
            return true;
        } catch (ExecutionException e) {
            logger.error("消息推送错误", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException",e);
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
