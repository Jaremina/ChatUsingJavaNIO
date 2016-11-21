/**
 *
 *  @author Pasławska Aneta S6034
 *
 */

package zad1;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class Server implements Runnable{

	private ServerSocketChannel serverChannel;
	private String server = "localhost";
	private int port = 8080;
	
	private Selector selector;
	
	private ByteBuffer bBuff = ByteBuffer.allocateDirect(1024);
	private String separator = "--------------------------------------------------------------------------------------------";
	

	public Server()
	{
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.socket().bind(new InetSocketAddress(server, port));
			serverChannel.configureBlocking(false);
			
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			 run();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	  @Override
	  public void run() {
		  while(serverChannel.isOpen())
			{
				try {
					selector.select();
					
					Set <SelectionKey>setOfKeys = selector.selectedKeys();
					
					Iterator <SelectionKey> iterator = setOfKeys.iterator();
					
					while(iterator.hasNext())
					{
						SelectionKey key = iterator.next();
						iterator.remove();
						
						if(key.isAcceptable())
						{
							serviceAccept(key);
						}
						
						if(key.isReadable())
						{
							serviceRead(key);
						}
				
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		
	  }

	
	private void serviceAccept( SelectionKey key) throws IOException
	{
		String s = "** server connected\n" + separator + "\n"; 
		ByteBuffer welcome = ByteBuffer.wrap(s.getBytes());
		
		SocketChannel socketChannel = serverChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		socketChannel.write(welcome);
		welcome.rewind();
	}
	
	private void serviceRead( SelectionKey key)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		if(!socketChannel.isOpen()) return;
		
		bBuff.clear();
		
		try {
			
			int read = 0;
		
			while ( (read = socketChannel.read(bBuff)) > 0 )
			{
				bBuff.flip();
				sendMessages(bBuff);
				bBuff.clear();

			}
			
			if(read < 0)
			{
				socketChannel.close();
				socketChannel.socket().close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMessages(ByteBuffer msg) throws IOException
	{
		for(SelectionKey key : selector.keys())
		{
			if(key.isValid() && key.channel() instanceof SocketChannel)
			{
				SocketChannel sc = (SocketChannel) key.channel();
				sc.write(msg);
				msg.rewind();
			}
		}
	}
	
  public static void main(String[] args) {
	    
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		new Server();
		
  }



}
