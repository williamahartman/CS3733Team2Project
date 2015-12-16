package ui;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.*;

/**
 * Created by Scott on 12/6/2015.
 */
public class Email extends Thread{
    private String recvEmail;
    private HashMap<StartEnd, String> directions;

    public Email(String addr, HashMap<StartEnd, String> instructions) throws AddressException{
        InternetAddress emailAddr = new InternetAddress(addr);
        emailAddr.validate();
        recvEmail = addr;
        directions = instructions;
    }

    public void run(){
        this.sendEmail();
    }

    public void sendEmail() {
        // Recipient's email ID needs to be mentioned.
        String to = recvEmail;

        String additionalChar = "";
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            additionalChar = "/";
        }
        String directory = System.getProperty("java.io.tmpdir") + additionalChar + "aztecWash/";

        // Sender's email ID needs to be mentioned
        String from = "aztec.wash.3733@gmail.com";
        final String username = "aztec.wash.3733";
        final String password = "aztecwash123";
        System.setProperty("java.net.preferIPv4Stack" , "true");
        // Assuming you are sending email through smtp.gmail.com
        String host = "smtp.gmail.com";

        //set properties of the email
        Properties props = new Properties();
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
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
        //Iterator<String> iter = directions.iterator();
        int stepNum = 0;
        int imageNum = 0;
        List<String> imagePaths = new ArrayList<>();
        String instructions = "";
        for (Map.Entry <StartEnd, String> entry : directions.entrySet()) {
            String direction = entry.getValue();
            instructions += ("<p>" + direction + "</p>");
            File image = new File(directory + "image" + imageNum + ".jpeg");
            if(image.exists())
            {
                System.out.println("exists");
            }
            if (image.exists() && !image.isDirectory()) {
                System.out.println("Image!");
                instructions += ("<img src=\"cid:image" + imageNum + "\">");
                imagePaths.add(directory + "image" + imageNum + ".jpeg");
                imageNum++;
            }
                stepNum++;
            }

        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
        try {
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("WPI Directions");
        } catch (MessagingException e){
            e.printStackTrace();
        }
        MimeMultipart multipart = new MimeMultipart("related");
        // first part (the html)
        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = "<H1>WPI Directions</H1><p></p>" + instructions;
        try {
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        // second part (the image)
        //reset the imageNumber to be used in the getting images
        imageNum = 0;
        Iterator<String> imgIter = imagePaths.iterator();
        while (imgIter.hasNext()){
            String imagePath = imgIter.next();
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(imagePath);
            try {
                messageBodyPart.setDataHandler(new DataHandler(fds));
                messageBodyPart.setHeader("Content-ID", "<image" + imageNum + ">");
                multipart.addBodyPart(messageBodyPart);
                imageNum++;
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }

        try {
            // put everything together
            message.setContent(multipart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            Transport t = session.getTransport("smtps");
            t.connect(host, username, password);
            t.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        }
        File index = new File(directory);
        if (index.exists()) {
            String[]entries = index.list();
            for (String s: entries) {
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
            index.delete();
        }


    }

}
