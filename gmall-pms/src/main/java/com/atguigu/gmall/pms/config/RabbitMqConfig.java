package com.atguigu.gmall.pms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;

@SpringBootConfiguration
@Slf4j
public class RabbitMqConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    //@PreDestroy  // <bean id="xx" class="类的全路径" init-method="" destroy-method=""/>
    public void init(){
        // 确认消息是否到达交换机。不管是否到达都会执行
        rabbitTemplate.setConfirmCallback((@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) -> {
            if (ack){
                log.info("消息到达交换机！");
            } else {
                log.error("消息没有到达交换机--------+++++++++" + cause);
            }
        });
        // 确认消息是否到达队列，如果没有到达队列，才会执行
        rabbitTemplate.setReturnCallback((Message message, int replyCode, String replyText, String exchange, String routingKey) -> {
            log.error("消息没有到达队列。交换机：{}，路由键：{}，消息内容：{}，文本信息：{}", exchange, routingKey, new String(message.getBody()), replyText);
        });
    }

    @Bean
    public Queue spuQueue(){
        return new Queue("PMS_SPU_QUEUE",true,false,false);
    }

    @Bean
    public Exchange spuExchange(){
        return ExchangeBuilder.directExchange("PMS_SPU_EXCHANGE").durable(true).build();
    }

    @Bean
    public Binding spuBin(Queue spuQueue, Exchange spuExchange){
        return BindingBuilder.bind(spuQueue).to(spuExchange).with("msg.spu").noargs();
    }

}
