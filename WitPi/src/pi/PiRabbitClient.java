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
	private final boolean logging = true;

	public PiRabbitClient(String host, String exchangeName, Pi pi) throws SecurityException, IOException{
		//		try {
		//			Process p = Runtime.getRuntime().exec("ssh r0304874@terbank.cs.kuleuven.be -L 5672:tabor.cs.kotnet.kuleuven.be:5672 -N");
		//			p.waitFor();
		//		} catch (IOException | InterruptedException e) {
		//			e.printStackTrace();
		//		}

		if (logging) {
			logger = Logger.getLogger("pirabbitclientlogger");  
			fh = new FileHandler("/rabbitclient.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter); 
		}

		this.exchangeName = exchangeName;
		this.pi = pi;
		server = host;
		setUpConnection();
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

	private void setUpConnection(){
		try{
			//Setting up connection
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("wit");
			factory.setPassword("wit");
			factory.setHost(server);
			factory.setPort(5672);
			if (logging)
				logger.info("Setup Client factory");
			connection = factory.newConnection();
			if (logging)
				logger.info("Created connection");
			channel = connection.createChannel();
			if (logging)
				logger.info("Created channel");
			channel.exchangeDeclare(exchangeName, "topic");
			if (logging)
				logger.info("Declared exchange");

			//Setting up reply 
			//Reply niet nodig, want daarvoor hebben we RabitRecv!
			//			replyQueueName = channel.queueDeclare().getQueue();
			//			consumer = new QueueingConsumer(channel);
			//			channel.basicConsume(replyQueueName, true, consumer);
		} catch(IOException ex){
			System.out.println("Error in setUpConnection");
			if (logging)
				logger.info("Error in setup connection");
			ex.printStackTrace();
		}
	}

	public void sendMessage(String message, String topic){
		try{
			//Sending the message
			channel.basicPublish(exchangeName, topic, null, message.getBytes());
			if (logging)
				logger.info("Sent message");
			System.out.println(topic + " " + message);
		} catch(Exception ex){
			if (logging)
				logger.info("Error in sending message");
			ex.printStackTrace();
		}
	}


	private void closeChannel(){
		try{
			channel.close();
			if (logging)
				logger.info("Closed channel");
			connection.close();
			if (logging)
				logger.info("Closed connection");
		} catch(IOException ex){
			System.out.println("Error in closeChannel");
			if (logging)
				logger.info("Error in closing channel");
		}
	}

	private boolean running = true;

	public void run(){		
		while(running){
			float height = pi.getLastCalculatedHeight();
			if (logging)
				logger.info("Got height");
			sendMessage("" + height, "wit.info.height");
			if (logging)
				logger.info("Sent height");
			try{
				Thread.sleep(500);
			} catch (Exception e){
				//PECH
			}
		}
		if (logging)
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
