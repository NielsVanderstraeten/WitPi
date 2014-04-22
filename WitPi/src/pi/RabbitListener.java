package pi;

import java.io.IOException;
import java.util.ArrayList;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitListener implements Runnable{

	private QueueingConsumer consumer;
	private Channel channel;
	private String queueName, exchangeName;
	private Pi pi;
	
	public RabbitListener(String host, String exchangeName, Pi pi) {
		setUpTopics();
		setUpConnection(host, exchangeName);
		this.pi = pi;
	}
	
	private void setUpConnection(String host, String exchangeName){
		Connection connection = null;
		try {
			this.exchangeName = exchangeName;
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("wit");
			factory.setPassword("wit");
			factory.setHost(host);
			factory.setPort(5673);
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			channel.exchangeDeclare(exchangeName, "topic");
			queueName = channel.queueDeclare().getQueue();
			
			declareTopicBinds();
			
			channel.basicQos(1);
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
			System.out.println("[x] Awaiting RPC requests");
		}
		catch(Exception e){
			System.err.println("Error in TestServer constructor");
		}
	}
	
	private boolean running = true;
	public void run() {
		try{
			QueueingConsumer.Delivery delivery;
			String message;
			String topic;
			while(running) {
				topic = ""; message = ""; delivery = null;
				delivery = consumer.nextDelivery();
				message = new String(delivery.getBody(),"UTF-8");
				topic = delivery.getEnvelope().getRoutingKey();
				
				if(topic.equals("wit.info.position")){
					String[] words = message.split("[ ]+");
					if(words.length == 2){
						if(pi != null)
							pi.setPosition(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
						else
							System.out.println("position " + words[0] + ", " + words[1]);
					}
				} else if(topic.equals("wit.hcommand.move")){
					String[] words = message.split("[ ]+");
					if(pi != null){
						if(words.length == 2){
							pi.setTargetPosition(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
						}
					} else
						System.out.println("move " + words[0] + ", " + words[1]);
				} else if(topic.equals("wit.hcommand.elevate")){
					if(pi !=null)
						pi.goToHeight(Integer.parseInt(message));
					else
						System.out.println("elevate " + message);
				} else if(topic.equals("wit.private.terminate")){
					if(pi != null){
						if(message.equalsIgnoreCase("true")){
							running = true;
							pi.stop();
						}
					} else
						System.out.println("termiante " + message);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("terminate");
	}
	private ArrayList<String> topics;
	private void setUpTopics(){
		topics = new ArrayList<String>();
		topics.add("wit.info.position");
		topics.add("wit.hcommand.elevate");
		topics.add("wit.hcommand.move");
		topics.add("wit.private.terminate");
	}
	
	public void stopRunning(){
		running = false;
	}
	private void declareTopicBinds() throws IOException{
		for(String topic: topics)
			channel.queueBind(queueName, exchangeName, topic);
	}

	public static void main(String[] args){
		RabbitListener listener = new RabbitListener("localhost", "tabor", null);
		listener.run();
	}
}