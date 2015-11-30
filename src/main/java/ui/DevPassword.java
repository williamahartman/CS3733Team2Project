package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elizabeth on 11/27/2015.
 * This class is to handle the verification to enable use of the
 * developer tools
 */
public class DevPassword extends JFrame{
    String username;
    String password;

    /**
     * Constructor.
     *
     * @param username The username of the person signing in.
     * @param password The password of the person signing in.
     */
    public DevPassword (String username, String password){
        this.username = username;
        this.password = password;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Set up the box for the verification pop-up.
     * Depending on the selection and what information was entered,
     * either log into Dev mode or stay in the User mode
     */
    public int passwordBox() {
        //Set up titles for the fields.
        JPanel label = new JPanel(new GridLayout(2, 2, 1, 10));
        label.add(new JLabel("Username:", SwingConstants.LEFT));
        JTextField uname = new JTextField();
        label.add(uname);
        label.add(new JLabel("Password:", SwingConstants.LEFT));
        JPasswordField password = new JPasswordField();
        label.add(password);

        int result = JOptionPane.showConfirmDialog(null, label,
                "Are You an Authorized Developer?", JOptionPane.OK_CANCEL_OPTION);
        int flag = 0;
        if (result == JOptionPane.OK_OPTION) {
            char[] pass = password.getPassword();
            String passString = new String(pass);
            if (((uname.getText().compareTo(this.username)) == 0)
                    && (((passString).compareTo(this.password)) == 0)) {
                flag = 1; //Condition to switch into dev mode
            } else {
                flag = 2; //Condition to display error message
            }
        } else if (result == JOptionPane.CANCEL_OPTION) {
            flag = 3; //Close out of the window
        }
        return flag;
    }
}





