package de.frittenburger.smtp.impl;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEToolkit;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;

import de.frittenburger.smtp.bo.Email;
import de.frittenburger.smtp.bo.Sender;
import de.frittenburger.smtp.bo.SmtpParameter;
import de.frittenburger.smtp.interfaces.SmtpService;

public class SmtpServiceImpl implements SmtpService {

	private final SmtpParameter smtpParameter;

	public SmtpServiceImpl(SmtpParameter smtpParameter) {
		this.smtpParameter = smtpParameter;
	}

	@Override
	public void send(Sender sender, Email email)  {

		
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", smtpParameter.getHost());

		if (smtpParameter.isUseSSL()) {
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.EnableSSL.enable", "true");
			properties.setProperty("mail.smtp.port", "587");
		}

		// authenticate
		if (smtpParameter.isAuthenticate())
			properties.setProperty("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(properties, new Authenticator() {

			// Override method to Authenticate to mail server
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpParameter.getUsername(), smtpParameter.getPassword());
			}
		});

		
		
		try
		{
		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(sender.getFrom()));

		// Set To: header field of the header.
		for (String addr : email.getTo())
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(addr));

		// Set Subject: header field
		message.setSubject(email.getSubject());

		// Set Date
		message.setSentDate(new Date());
		// Now set the actual message

		Multipart multiPartMixed = new MimeMultipart("mixed");

		Multipart multiPartAlternative = new MimeMultipart("alternative");

		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(email.getTextBody(), "utf-8");

		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(email.getHtmlBody(), "text/html; charset=utf-8");

		multiPartAlternative.addBodyPart(textPart);
		multiPartAlternative.addBodyPart(htmlPart);

		// stuff the multipart into a bodypart and add the bodyPart to the
		// mainMultipart
		MimeBodyPart htmlAndTextBodyPart = new MimeBodyPart();
		htmlAndTextBodyPart.setContent(multiPartAlternative);
		multiPartMixed.addBodyPart(htmlAndTextBodyPart);

		for(String attachment : email.getAttachments())
		{
			// attach file body parts directly to the mainMultipart
			MimeBodyPart filePart = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(attachment);
			filePart.setDataHandler(new DataHandler(fds));
			filePart.setFileName(fds.getName());
			multiPartMixed.addBodyPart(filePart);
		}
		if (sender.isSign()) {

			MimeBodyPart contentBodyPart = new MimeBodyPart();
			contentBodyPart.setContent(multiPartMixed);
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(new FileInputStream(sender.getKeystore()), sender.getKeystorePassword().toCharArray());

			
			 Enumeration<String> aliases = keystore.aliases();
			 while(aliases.hasMoreElements()) System.out.println("alias \"" + aliases.nextElement()+"\"");
			
			PrivateKey signerKey = (PrivateKey) keystore.getKey(sender.getPrivkeyPasswort(),
					sender.getPrivkeyPasswort().toCharArray());
			if (signerKey == null)
				throw new NullPointerException("signerKey");

			X509Certificate signerCert = (X509Certificate) keystore.getCertificate(sender.getCertAlias());
			if (signerCert == null)
				throw new NullPointerException("signerCert");

			SMIMEToolkit toolkit = new SMIMEToolkit(new BcDigestCalculatorProvider());

			MimeMultipart smm = toolkit.sign(contentBodyPart, new JcaSimpleSignerInfoGeneratorBuilder()
					.setProvider(new BouncyCastleProvider()).build("SHA1withRSA", signerKey, signerCert));

			message.setContent(smm);
		} else {
			message.setContent(multiPartMixed);
		}
			// Send message
				Transport.send(message);
				System.out.println("Sent message successfully....");
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}

}
