package clientserver;


//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.Channel;

import com.rabbitmq.client.*;


public class TopicExchangePublisher {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        // Create a connection factory and set up connection parameters
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPassword("guest");
        factory.setUsername("guest");
        factory.setPort(5672);
      
        // Create a connection to RabbitMQ
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the topic exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // Publish a message with a routing key
        String message = "Hello, World!";
        String routingKey = "topic.key.1"; // Customize the routing key according to your topic hierarchy

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        System.out.println("Sent message: " + message + " with routing key: " + routingKey);

        // Close the connection
        channel.close();
        connection.close();
    }
}
