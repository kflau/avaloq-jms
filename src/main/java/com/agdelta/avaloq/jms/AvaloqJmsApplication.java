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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.ConnectionFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

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
		JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formatDateTime = now.format(formatter);

		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("Enter (quit to exit): \t");
			String line = scanner.nextLine();
			if ("quit".equals(line)) {
				applicationContext.close();
				break;
			}
			jmsTemplate.convertAndSend("AMI_IN", String.format("AMI_IN %s %s", line, formatDateTime));
			jmsTemplate.convertAndSend("AMI_SYNC_IN", String.format("AMI_SYNC_IN %s %s", line, formatDateTime));
			System.out.println(String.format("Message [%s] sent", line));
		}
	}
}
