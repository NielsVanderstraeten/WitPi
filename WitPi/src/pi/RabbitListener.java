package pi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitListener implements Runnable{

	private QueueingConsumer consumer;
	private Channel channel;
	private String queueName, exchangeName;
	private Pi pi;
	private String host;
	private Logger logger;
	private FileHandler fh;
	private final boolean logging = false;

	public RabbitListener(String host, String exchangeName, Pi pi) throws SecurityException, IOException {
		if (logging) {
			logger = Logger.getLogger("pirabbitlistenerlogger");  
			fh = new FileHandler("/rabbitlistener.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter); 
		}

		this.pi = pi;
		this.host = host;
		this.exchangeName = exchangeName;
	}

	private void setUpConnection(){
		Connection connection = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("wit");
			factory.setPassword("wit");
			factory.setHost(host);
			factory.setPort(5672);
			if (logging)
				logger.info("Setup factory");
			connection = factory.newConnection();
			if (logging)
				logger.info("Setup connection");
			channel = connection.createChannel();

			channel.exchangeDeclare(exchangeName, "topic");
			if (logging)
				logger.info("Declared channel");

			queueName = channel.queueDeclare().getQueue();

			declareTopicBinds();	
			if (logging)
				logger.info("Declared topics");

			channel.basicQos(1);
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
			System.out.println("[x] Awaiting RPC requests");
			if (logging)
				logger.info("Awaiting RPC requests");
		}
		catch(Exception e){
			System.err.println("Error in TestServer constructor");
			e.printStackTrace();
			if (logging)
				logger.info("Error in constructor");
		}
	}

	private boolean running = true;
	public void run() {
		setUpTopics();
		setUpConnection();

		try{
			QueueingConsumer.Delivery delivery;
			String message;
			String topic;
			while(running) {
				topic = ""; message = ""; delivery = null;
				delivery = consumer.nextDelivery();
				message = new String(delivery.getBody(),"UTF-8");
				if (logging)
					logger.info("Got message: " + topic);
				topic = delivery.getEnvelope().getRoutingKey();

				if(topic.equals("wit.info.location")){
					String[] words = message.split("[ ]+");
					if(words.length >= 2){
						if(pi != null)
							pi.setPosition(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
						else
							System.out.println("position " + words[0] + " " + words[1]);
					}
				} else if(topic.equals("wit.private.rotation")){
					pi.setRotation(Double.parseDouble(message));
				} else if(topic.equals("wit.hcommand.move")){
					String[] words = message.split("[ ]+");
					if(pi != null){
						if(words.length >= 2){
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
					pi.takePicture();
					File file = new File("picture.jpg");
					
					//Stuur aantal bytes
					InputStream inFile = new FileInputStream(file);
//					int size = (int) file.length();
					byte[] buf = new byte[8192];
//					channel.basicPublish(exchangeName, "wit.private.recvPicture", null, (""+size).getBytes());
					
					//Niels: Foto naar byte[]
//					BufferedImage originalImage = ImageIO.read(file);
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					ImageIO.write(originalImage, "jpg", baos);
//					byte[] pictureBytes = baos.toByteArray();
					
					//Niels: byte[] in 1x publishen
//					channel.basicPublish(exchangeName, "wit.private.recvPicture", null, pictureBytes);
					
					//Stuur foto
					int len = 0;
					OutputStream outFileStream = new FileOutputStream(file, false);
					while ((len = inFile.read(buf)) != -1) {
						if(len < 8192){
							byte[] buf2 = new byte[len];
							buf2 = Arrays.copyOfRange(buf, 0, len);
							//channel.basicPublish(exchangeName, "wit.private.recvPicture", null, buf2);
							outFileStream.write(buf2);
						} else{
							//channel.basicPublish(exchangeName, "wit.private.recvPicture", null, buf);
							outFileStream.write(buf);
						}
						System.out.println(len);
					}
					
					//Stuur end bericht
					channel.basicPublish(exchangeName, "wit.private.recvPicture", null, "end".getBytes());
					inFile.close();
					outFileStream.close();
					
//					baos.close();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			if (logging)
				logger.info("Error in run()");
		}
		System.out.println("terminate");
	}

	private ArrayList<String> topics;

	private void setUpTopics(){
		topics = new ArrayList<String>();
		topics.add("wit.info.location");
		topics.add("wit.hcommand.elevate");
		topics.add("wit.hcommand.move");
		topics.add("wit.private.terminate");
		topics.add("wit.private.sendPicture");
		topics.add("wit.private.rotation");
	}

	public void stopRunning(){
		running = false;
	}

	private void declareTopicBinds() throws IOException{
		for(String topic: topics)
			channel.queueBind(queueName, exchangeName, topic);
	}

	public static void main(String[] args) throws SecurityException, IOException{
		RabbitListener listener = new RabbitListener("localhost", "server", null);
		listener.run();
	}
}