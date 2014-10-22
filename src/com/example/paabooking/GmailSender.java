package com.example.paabooking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import android.os.AsyncTask;
import android.util.Log;

public class GmailSender extends Authenticator {
	private final static String HOST = "smtp.gmail.com";
	private static String username;
	private static String password;
	private static Session session;
	private static Transport transport;

	/*static {
		Security.addProvider(new JSSEProvider());
	}*/

	public GmailSender(String user, final String password) {
		username = user;
		this.password = password;

		Properties properties = new Properties();
		/*properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.quitwait", "false");
		properties.setProperty("mail.smtp.host", HOST);*/
		properties.put("mail.smtp.user", user);
		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.starttls.enable","true");
		properties.put("mail.smtp.debug", "true");
		properties.put("mail.smtp.auth", "true");
		/*properties.put("mail.smtp.password", password);*/
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");

		/*
		 * JSSEProvider auth = new JSSEProvider(); Session session =
		 * Session.getInstance(properties, auth);
		 */
		session = Session.getDefaultInstance(properties,
				new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		session.setDebug(true);
	}

/*	public class SendMail extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... parameters) {
			String subject = parameters[0];
			String body = parameters[1];
			String sender = parameters[2];
			String recipients = parameters[3];
			try {
				Log.d("GmailSender", "VERSION 4.");
				MimeMessage message = new MimeMessage(session);
				DataHandler handler = new DataHandler(new ByteArrayDataSource(
						body.getBytes(), "text/plain"));
				message.setSender(new InternetAddress(sender));
				message.setSubject(subject);
				Log.d("GmailSender", "Subject set.");
				message.setDataHandler(handler);
				Log.d("GmailSender", "DataHandler set..");
				if (recipients.indexOf(',') > 0) {
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(recipients));
				} else {
					message.setRecipient(Message.RecipientType.TO,
							new InternetAddress(recipients));
				}
				Log.d("GmailSender",
						"Attempting to send message to - " + recipients
								+ " With message - subj: " + message.getSubject()
								+ " content: " + message.getContent().toString());
				if (message != null) {
					Log.d("GmailSender", "message is NOT null");
				}

				transport = session.getTransport("smtps");
				transport.connect(HOST, 465, username, password);

				transport.sendMessage(message, message.getAllRecipients());

				transport.close();
				 Transport.send(message); 

				Log.d("GmailSender", "Sent message.");
			} catch (AddressException ae) {
				Log.d("GmailSender",
						"AddressException - " + ae.getLocalizedMessage());
			} catch (MessagingException me) {
				Log.d("GmailSender",
						"MessagingException - " + me.getLocalizedMessage());
			} catch (Exception e) {
				Log.d("GmailSender", "Exception stacktrace - " + Log.getStackTraceString(e));
				Log.d("GmailSender", "Exception - " + e.getLocalizedMessage());
			}
			return null;
		}
		
	}*/
	
	public synchronized void sendMail(String subject, String body,
			String sender, String recipients) throws Exception {
		
		
			
			
			new AsyncTask<String, Void, Void>() {

				@Override
				protected Void doInBackground(String... parameters) {
					String subject = parameters[0];
					String body = parameters[1];
					String sender = parameters[2];
					String recipients = parameters[3];
					try {
						Log.d("GmailSender", "VERSION 4.");
						MimeMessage message = new MimeMessage(session);
						DataHandler handler = new DataHandler(new ByteArrayDataSource(
								body.getBytes(), "text/plain"));
						message.setSender(new InternetAddress(sender));
						message.setSubject(subject);
						Log.d("GmailSender", "Subject set.");
						message.setDataHandler(handler);
						Log.d("GmailSender", "DataHandler set..");
						if (recipients.indexOf(',') > 0) {
							message.setRecipients(Message.RecipientType.TO,
									InternetAddress.parse(recipients));
						} else {
							message.setRecipient(Message.RecipientType.TO,
									new InternetAddress(recipients));
						}
						Log.d("GmailSender",
								"Attempting to send message to - " + recipients
										+ " With message - subj: " + message.getSubject()
										+ " content: " + message.getContent().toString());
						if (message != null) {
							Log.d("GmailSender", "message is NOT null");
						}

						transport = session.getTransport("smtps");
						transport.connect(HOST, 465, username, password);

						transport.sendMessage(message, message.getAllRecipients());

						transport.close();
						/* Transport.send(message); */

						Log.d("GmailSender", "Sent message.");
					} catch (AddressException ae) {
						Log.d("GmailSender",
								"AddressException - " + ae.getLocalizedMessage());
					} catch (MessagingException me) {
						Log.d("GmailSender",
								"MessagingException - " + me.getLocalizedMessage());
					} catch (Exception e) {
						Log.d("GmailSender", "Exception stacktrace - " + Log.getStackTraceString(e));
						Log.d("GmailSender", "Exception - " + e.getLocalizedMessage());
					}
					return null;
				}
				
			}.execute(subject, body,
					sender, recipients);
		
		
		
		
		
		/*try {
			Log.d("GmailSender", "VERSION 4.");
			MimeMessage message = new MimeMessage(session);
			DataHandler handler = new DataHandler(new ByteArrayDataSource(
					body.getBytes(), "text/plain"));
			message.setSender(new InternetAddress(sender));
			message.setSubject(subject);
			Log.d("GmailSender", "Subject set.");
			message.setDataHandler(handler);
			Log.d("GmailSender", "DataHandler set..");
			if (recipients.indexOf(',') > 0) {
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipients));
			} else {
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(recipients));
			}
			Log.d("GmailSender",
					"Attempting to send message to - " + recipients
							+ " With message - subj: " + message.getSubject()
							+ " content: " + message.getContent().toString());
			if (message != null) {
				Log.d("GmailSender", "message is NOT null");
			}

			transport = session.getTransport("smtps");
			transport.connect(HOST, 465, username, password);

			transport.sendMessage(message, message.getAllRecipients());

			transport.close();
			 Transport.send(message); 

			Log.d("GmailSender", "Sent message.");
		} catch (AddressException ae) {
			Log.d("GmailSender",
					"AddressException - " + ae.getLocalizedMessage());
		} catch (MessagingException me) {
			Log.d("GmailSender",
					"MessagingException - " + me.getLocalizedMessage());
		} catch (Exception e) {
			Log.d("GmailSender", "Exception stacktrace - " + Log.getStackTraceString(e));
			Log.d("GmailSender", "Exception - " + e.getLocalizedMessage());
		}*/
	}


}
