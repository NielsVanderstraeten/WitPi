package pi;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
	private Logger logger;
	private FileHandler fh;
	
	public PiRabbitClient(String host, String exchangeName, Pi pi) throws SecurityException, IOException{
//		try {
//			Process p = Runtime.getRuntime().exec("ssh r0304874@terbank.cs.kuleuven.be -L 5672:tabor.cs.kotnet.kuleuven.be:5672 -N");
//			p.waitFor();
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
		
		logger = Logger.getLogger("pirabbitclientlogger");  
		fh = new FileHandler("/rabbitclient.log");  
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter); 

		this.exchangeName = exchangeName;
		this.pi = pi;
		server = host;
	}
	
	public PiRabbitClient() throws SecurityException, IOException{
		this("localhost", "server", null);
	}
	
	public int getPort(){
		return port;
	}

	public String getServerName(){
		return server;
	}
	
	private void setUpConnection(String host){
		try{
			//Setting up connection
			ConnectionFactory factory = new ConnectionFactory();
			logger.info("Created factory");
			factory.setUsername("wit");
			factory.setPassword("wit");
			logger.info("Set login");
			factory.setHost(host);
			factory.setPort(5672);
			logger.info("Set host + port");
			connection = factory.newConnection();
			logger.info("Created connection");
			channel = connection.createChannel();
			logger.info("Created channel");
			channel.exchangeDeclare(exchangeName, "topic");
			logger.info("Declared exchange");
//			//Setting up reply 
			//Reply niet nodig, want daarvoor hebben we RabitRecv!
//			replyQueueName = channel.queueDeclare().getQueue();
//			consumer = new QueueingConsumer(channel);
//			channel.basicConsume(replyQueueName, true, consumer);
		} catch(IOException ex){
			System.out.println("Error in setUpConnection");
			logger.info("Error in setup connection");
			ex.printStackTrace();
		}
	}
	
	public void sendMessage(String message, String topic){
		try{
			//Sending the message
			channel.basicPublish(exchangeName, topic, null, message.getBytes());
			logger.info("Sent message");
			System.out.println(topic + " " + message);
		} catch(Exception ex){
			logger.info("Error in sending message");
			ex.printStackTrace();
		}
	}


	private void closeChannel(){
		try{
			channel.close();
			logger.info("Closed channel");
			connection.close();
			logger.info("Closed connection");
		} catch(IOException ex){
			System.out.println("Error in closeChannel");
			logger.info("Error in closing channel");
		}
	}
	
	private boolean running = true;
	
	public void run(){
		setUpConnection(server);
		
		while(running){
			float height = pi.getLastCalculatedHeight();
			logger.info("Got height");
			sendMessage("" + height, "wit.info.height");
			logger.info("Sent height");
			try{
				Thread.sleep(500);
			} catch (Exception e){
				//PECH
			}
		}
		logger.info("Exitted while loop run()");
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
