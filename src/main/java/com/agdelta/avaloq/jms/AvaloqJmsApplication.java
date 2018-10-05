package com.agdelta.avaloq.jms;

import com.agdelta.avaloq.jms.messageconverter.CompositeMessageConverter;
import com.prowidesoftware.swift.model.field.*;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;
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
import java.util.Calendar;
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
	public MessageConverter compositeMessageConverter(
			MessageConverter jacksonJmsMessageConverter,
			MessageConverter simpleMessageConverter) {
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
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("Enter> \t");
			String line = scanner.nextLine();
			if ("quit".equals(line)) {
				applicationContext.close();
				break;
			} else if ("help".equals(line)) {
				showUsage();
				continue;
			} else if ("mt103".equals(line))
				line = createMt103();

			jmsTemplate.convertAndSend("AMI_IN", line);
			jmsTemplate.convertAndSend("AMI_SYNC_IN", line);
			System.out.println(String.format("Message [%s] sent", line));
		}
	}

	private static void showUsage() {
		System.out.println(
				String.format("%n<input>: send <input> to AMI_IN, AMI_SYNC_IN%nmt103: send SWIFT MT103 message to AMI_IN, AMI_SYNC_IN%nquit: exit the application%n"));
	}

	private static String createMt103() {
		MT103 m = new MT103();
		m.setSender("FOOSEDR0AXXX");
		m.setReceiver("FOORECV0XXXX");
		m.addField(new Field20("REFERENCE"));
		m.addField(new Field23B("CRED"));

		Field32A f32A = new Field32A()
				.setDate(Calendar.getInstance())
				.setCurrency("HKD")
				.setAmount("1234567,89");
		m.addField(f32A);

		Field50A f50A = new Field50A()
				.setAccount("12345678901234567890")
				.setBIC("FOOBANKXXXXX");
		m.addField(f50A);

		Field59 f59 = new Field59()
				.setAccount("12345678901234567890")
				.setNameAndAddress("JOE DOE");
		m.addField(f59);

		m.addField(new Field71A("OUR"));
		return m.message();
	}
}
