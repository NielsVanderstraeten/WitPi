package pi;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class PiRabbitClient implements Runnable{

	private String exchangeName;
	private Connection connection;
	private Channel channel;
	private int port;
	private String server;
	private Pi pi;
	
	public PiRabbitClient(String host, String exchangeName, Pi pi){
		setUpConnection(host, exchangeName);
		this.exchangeName = exchangeName;
		this.pi = pi;
		server = host;
	}
	
	public PiRabbitClient(){
		setUpConnection("localhost", "tobar");
		this.exchangeName = "tobar";
	}
	
	public int getPort(){
		return port;
	}

	public String getServerName(){
		return server;
	}
	
	private void setUpConnection(String host, String exchangeName){
		try{
			//Setting up connection
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("wit");
			factory.setPassword("wit");
			factory.setHost(host);
			factory.setPort(5673);
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(exchangeName, "topic");
//			//Setting up reply 
			//Reply niet nodig, want daarvoor hebben we RabitRecv!
//			replyQueueName = channel.queueDeclare().getQueue();
//			consumer = new QueueingConsumer(channel);
//			channel.basicConsume(replyQueueName, true, consumer);
		} catch(IOException ex){
			System.out.println("Error in setUpConnection");
		}
	}
	
	public void sendMessage(String message, String topic){
		try{
			//Sending the message
			channel.basicPublish(exchangeName, topic, null, message.getBytes());
			System.out.println(topic + " " + message);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}


	private void closeChannel(){
		try{
			channel.close();
			connection.close();
		} catch(IOException ex){
			System.out.println("Error in closeChannel");
		}
	}
	
	private boolean running = true;
	
	public void run(){
		while(running){
			float height = pi.getLastCalculatedHeight();
			sendMessage("" + height, "wit.info.height");
			try{
				Thread.sleep(500);
			} catch (Exception e){
				//PECH
			}
		}
		closeChannel();
	}
	
	public void stopRunning(){
		running = false;
	}
	
	public static void main(String[] args) throws Exception{
		PiRabbitClient client = new PiRabbitClient();
		int i = 1;
		while(i < 10){
			client.sendMessage(i*300 + "", "wit.info.height");
			Thread.sleep(500);
			i++;
		}
	}
}
