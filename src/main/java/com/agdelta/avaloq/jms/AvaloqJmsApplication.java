package com.agdelta.avaloq.jms;

import com.agdelta.avaloq.jms.messageconverter.CompositeMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.ConnectionFactory;
import java.util.Arrays;
import java.util.HashMap;

@SpringBootApplication
@EnableJms
public class AvaloqJmsApplication {

	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer,
													MessageConverter compositeMessageConverter) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setMessageConverter(compositeMessageConverter);
		return factory;
	}

	@Bean
	public MessageConverter compositeMessageConverter(MessageConverter jacksonJmsMessageConverter, MessageConverter simpleMessageConverter) {
		return CompositeMessageConverter.builder()
				.addMessageConverter(jacksonJmsMessageConverter)
				.addMessageConverter(simpleMessageConverter)
				.build();
	}


	@Bean
	public MessageConverter simpleMessageConverter() {
		return new SimpleMessageConverter();
	}

	/**
	 * Activate this {@link org.springframework.jms.support.converter.MessageConverter} if message content is valid JSON,
	 * otherwise it fails to deserialize.
	 * @return messageConverter
	 */
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("JMS_IBM_Format");
		HashMap<String, Class<?>> typeIdMappings = new HashMap<>();
		typeIdMappings.put("MQSTR   ", String.class);
		converter.setTypeIdMappings(typeIdMappings);
		return converter;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(AvaloqJmsApplication.class, args);
		System.out.println(Arrays.asList(applicationContext.getBeanDefinitionNames()));
	}
}
