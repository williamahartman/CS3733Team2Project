package ui;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by Scott on 12/6/2015.
 */
public class Email extends Thread{
    private String recvEmail;
    private String directions;

    public Email(String addr) throws AddressException{
        InternetAddress emailAddr = new InternetAddress(addr);
        emailAddr.validate();
        recvEmail = addr;
    }

    public Email(String addr, String instructions) throws AddressException{
        InternetAddress emailAddr = new InternetAddress(addr);
        emailAddr.validate();
        recvEmail = addr;
        directions = instructions;
    }

    public void setDirections(String instructions){
        directions = instructions;
    }

    public void run(){
        this.sendEmail(directions, null);
    }

    public void sendEmail(String instructions, List<String> images) {
        // Recipient's email ID needs to be mentioned.
        String to = recvEmail;

        // Sender's email ID needs to be mentioned
        String from = "aztec.wash.3733@gmail.com";
        final String username = "aztec.wash.3733";
        final String password = "aztecwash123";

        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
        try {
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Test Email");
        } catch (MessagingException e){
            e.printStackTrace();
        }
        MimeMultipart multipart = new MimeMultipart("related");
        // first part (the html)
        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = "<H1>Hello Team!!</H1><p>" + instructions +
                "</p><img src=\"cid:image\">" + "<p> test</p>" + "<img src=\"cid:test2\">";
        try {
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        // second part (the image)
        messageBodyPart = new MimeBodyPart();
        String path = System.getProperty("user.dir");
        DataSource fds = new FileDataSource(
                path + "\\picture.jpeg");
        try {
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(messageBodyPart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            // put everything together
            message.setContent(multipart);
            // Send message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
