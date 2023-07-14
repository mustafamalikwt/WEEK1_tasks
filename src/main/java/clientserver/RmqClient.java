package clientserver;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RmqClient {
//    private static final String QUEUE_NAME = "myQueue";S
    private static final String EXCHANGE_NAME = "myExchange";
    private static final String ROUTING_KEY = "myRoutingKey";
    private static final String MESSAGE = "Hello, RabbitMQ 2nd Message!";

    public static void main(String[] args) {
        try {
            // Establish connection
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // Declare exchange
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // Publish message to the exchange
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, MESSAGE.getBytes());
            System.out.println("Message sent: " + MESSAGE);

            // Close connections
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
