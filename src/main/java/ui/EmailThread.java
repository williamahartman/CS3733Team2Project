package ui;

import javax.mail.MessagingException;

/**
 * Created by Scott on 12/6/2015.
 */
public class EmailThread extends Thread {
    private String directions;

    public EmailThread(String directions){
        this.directions = directions;
    }

    public void run(){
        try {
            Email mail = new Email("swiwanicki@wpi.edu");
            mail.sendEmail(directions, null);
        } catch (MessagingException ex){
            ex.printStackTrace();
        }

    }

}
