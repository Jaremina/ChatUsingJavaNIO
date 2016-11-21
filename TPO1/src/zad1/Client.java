/**
 *
 *  @author Pasławska Aneta S6034
 *
 */

package zad1;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private SocketChannel channel;
	private String server = "localhost";
	private int port = 8080;
	private Charset charset = Charset.forName("ISO-8859-2");
	private CharBuffer cbuf;
	private ByteBuffer inBuff = ByteBuffer.allocateDirect(1024);
	private ByteBuffer outBuff = ByteBuffer.allocateDirect(1024);
	
	private String separator = "-----------------------\n";
	private Matcher match = Pattern.compile(separator).matcher("");

	
	private JPanel mainPanel;
	private JPanel LogingPanel;
	private JPanel ChatPanel;
	private JTextField textField;
	private JTextArea chatTextArea;
	private JTextArea msgTextArea;
	private JButton btnSend;
	private JScrollPane chatScroll;
	private JScrollPane msgScroll;
	
	private String login;
	
	
	
	public Client() {
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		mainPanel.setLayout(new CardLayout(0, 0));
		
		
		LogingPanel = new JPanel();
		mainPanel.add(LogingPanel, "name_167754409975858");
		LogingPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(136, 94, 164, 22);
		LogingPanel.add(textField);
		textField.setColumns(10);
		
		JLabel lblLogin = new JLabel("User name");
		lblLogin.setBounds(54, 97, 70, 16);
		LogingPanel.add(lblLogin);
		
		JButton btnLogIn = new JButton("Log in");
		btnLogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loginButtonClicked();
			}
		});
		btnLogIn.setBounds(156, 154, 97, 25);
		LogingPanel.add(btnLogIn);
		
		ChatPanel = new JPanel();
		mainPanel.add(ChatPanel, "name_167993342401307");
		ChatPanel.setLayout(null);
		
		chatTextArea = new JTextArea();
		chatTextArea.setEditable(false);
		
		chatScroll = new JScrollPane(chatTextArea);
		chatScroll.setBounds(0, 0, 422, 193);
		ChatPanel.add(chatScroll);
		
		DefaultCaret chatCaret = (DefaultCaret) chatTextArea.getCaret(); 
		chatCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		msgTextArea = new JTextArea();
		
		msgScroll = new JScrollPane(msgTextArea);
		msgScroll.setBounds(0, 206, 298, 37);
		ChatPanel.add(msgScroll);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				sendMsg(login + " says: " + msgTextArea.getText() + "\n" + separator);
				msgTextArea.setText("");
			}
			
		});
		btnSend.setBounds(313, 206, 97, 25);
		ChatPanel.add(btnSend);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				try {					
					channel.close();
					channel.socket().close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
	}	
	
	private void loginButtonClicked()
	{
		if(!textField.getText().equals(""))
		{
			login = textField.getText();
			
			CardLayout cl = (CardLayout)(mainPanel.getLayout());
			cl.last(mainPanel);
			
			connectWithServer(); 
		}
	}
	
	private void connectWithServer()
	{
		try{
			chatTextArea.setText("**connectig with the server");
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(server, port));
			
			
			while(!channel.finishConnect())
			{
				
			}
		}catch (UnknownHostException e)
		{
			System.err.println("unknown host: " + server);
		}catch ( IOException e) {
			e.printStackTrace();
		}
		
		new ReciveMessages().start();
	}
	
	private void sendMsg(String sMsg)
	{
		cbuf = CharBuffer.wrap(sMsg);
		
		outBuff.clear();
		outBuff = charset.encode(cbuf); 
		
		try {
			channel.write(outBuff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	class ReciveMessages extends Thread
	{
		
		public void run() 
		{
			try {
				
				while(true){
					inBuff.clear();
					int readBytes = channel.read(inBuff);
					
					if(readBytes == 0){
									
						Thread.sleep(100);			
						continue;
					}else if (readBytes == -1)
					{
						chatTextArea.append("\n**channel closed");
						break;
					}else {
						inBuff.flip();
						cbuf = charset.decode(inBuff);
						chatTextArea.append("\n" + cbuf.toString());  
						match.reset(cbuf);
						inBuff.clear();
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

