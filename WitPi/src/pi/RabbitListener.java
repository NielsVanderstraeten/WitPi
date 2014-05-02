package pi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitListener implements Runnable{

	private QueueingConsumer consumer;
	private Channel channel;
	private String queueName, exchangeName;
	private Pi pi;
	private Logger logger;
	private FileHandler fh;
	
	public RabbitListener(String host, String exchangeName, Pi pi) throws SecurityException, IOException {
		logger = Logger.getLogger("pirabbitlistenerlogger");  
		fh = new FileHandler("~/rabbitlistener.log");  
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter); 
        
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
			logger.info("Setup connection");
			channel = connection.createChannel();
			
			channel.exchangeDeclare(exchangeName, "topic");
			logger.info("Declared channel");
			queueName = channel.queueDeclare().getQueue();
			
			declareTopicBinds();
			
			logger.info("Declared topics");
			
			channel.basicQos(1);
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
			System.out.println("[x] Awaiting RPC requests");
			logger.info("Awaiting RPC requests");
		}
		catch(Exception e){
			System.err.println("Error in TestServer constructor");
			logger.info("Error in constructor");
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
				logger.info("Got message: " + message);
				topic = delivery.getEnvelope().getRoutingKey();
				
				if(topic.equals("wit.info.position")){
					String[] words = message.split("[ ]+");
					if(words.length == 2){
						if(pi != null)
							pi.setPosition(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
						else
							System.out.println("position " + words[0] + ", " + words[1]);
					}
				} else if(topic.equals("wit.private.rotation")){
					pi.setRotation(Integer.parseInt(message));
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
				}else if(topic.equals("wit.private.terminate")){
					if(pi != null){
						if(message.equalsIgnoreCase("true")){
							running = true;
							pi.stop();
						}
					} else
						System.out.println("terminate " + message);
				} else if(topic.equals("wit.private.sendPicture")){
					//pi.takePicture();
					//File file = new File("picture.jpg");
					File file = new File("src/pi/photo.jpg");
					InputStream inFile = new FileInputStream(file);
					long size = file.length();
					byte[] buf = new byte[8192];
					channel.basicPublish(exchangeName, "wit.private.recvPicture", null, (""+size).getBytes());
					int len = 0;
					System.out.println(size);
					while ((len = inFile.read(buf)) != -1) {
						if(len < 8192){
							byte[] buf2 = new byte[len];
							buf2 = Arrays.copyOfRange(buf, 0, len);
							channel.basicPublish(exchangeName, "wit.private.recvPicture", null, buf2);
						} else{
							channel.basicPublish(exchangeName, "wit.private.recvPicture", null, buf);
						}
						System.out.println(len);
					}
					inFile.close();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			logger.info("Error in run()");
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
		topics.add("wit.private.sendPicture");
	}
	
	public void stopRunning(){
		running = false;
	}
	
	private void declareTopicBinds() throws IOException{
		for(String topic: topics)
			channel.queueBind(queueName, exchangeName, topic);
	}

	public static void main(String[] args) throws SecurityException, IOException{
		RabbitListener listener = new RabbitListener("localhost", "tabor", null);
		listener.run();
	}
}