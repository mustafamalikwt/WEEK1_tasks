package clientserver;



import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class RmqServer {
    private static final String QUEUE_NAME = "myQueue";
    private static final String EXCHANGE_NAME = "myExchange";
    private static final String ROUTING_KEY = "myRoutingKey";

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

            // Declare queue and bind it to the exchange
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            // Create consumer to receive and process messages
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received message: " + message);
                    
                    // Process the message and insert into the database
                   insertMessageIntoDatabase(message);
   
                   // Manually acknowledge the message and remove it from the queue
                   long deliveryTag = envelope.getDeliveryTag();
                   channel.basicAck(deliveryTag, false);
            
                }
            };
            // Start consuming messages from the queue
            
//          channel.basicConsume(QUEUE_NAME, true, consumer);
            channel.basicConsume(QUEUE_NAME, false, consumer); // autoAck set to false

            // Wait for messages
            System.out.println("Waiting for messages...");
            Thread.sleep(30000);

            // Close connections
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static int insertMessageIntoDatabase(String message) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            // Create a Hibernate entity object
        	
        	
//        	System.out.println("HELLO!!!!");
            MessageEntity messageEntity = new MessageEntity();
//            System.out.println("HELLO!!!!2");
            messageEntity.setMessage(message);

            // Save the entity object to the database
//            System.out.println("HELLO!!!!3");
            session.beginTransaction();
//            System.out.println("HELLO!!!!4");
            session.save(messageEntity);
//            System.out.println("HELLO!!!!5");
            session.getTransaction().commit();
//            System.out.println("HELLO!!!!6");
            
            System.out.println("---------MESSAGE INSERTED INTO DATABASE------------");
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
