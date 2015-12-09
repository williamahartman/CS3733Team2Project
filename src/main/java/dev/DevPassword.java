package dev;

import database.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by Elizabeth on 11/27/2015.
 * This class is to handle the verification to enable use of the
 * developer tools
 */
public class DevPassword {
    private String usernameSuccess;
    private String passwordSuccess;

    /**
     * Set up the box for the verification pop-up.
     * Depending on the selection and what information was entered,
     * either log into Dev mode or stay in the User mode
     */
    public int passwordBox() {
        try {
            //Set up titles for the fields.
            //todo database username and password implementation
            JPanel label = new JPanel(new GridLayout(2, 2, 1, 10));
            label.add(new JLabel("Username:", SwingConstants.LEFT));
            JTextField uname = new JTextField();
            label.add(uname);
            label.add(new JLabel("Password:", SwingConstants.LEFT));
            JPasswordField password = new JPasswordField();
            label.add(password);

            //Decide whether the user logged in using the correct credentials, tried
            //to log in using wrong ones, or selected cancel.
            int result = JOptionPane.showConfirmDialog(null, label,
                    "Are You a Developer?", JOptionPane.OK_CANCEL_OPTION);

            int flag = 0;
            if (result == JOptionPane.OK_OPTION) {
                char[] pass = password.getPassword();
                String passString = new String(pass);
                if (tryLogin(uname.getText(), passString)) {
                    usernameSuccess = uname.getText();
                    passwordSuccess = passString;

                    flag = 1; //Condition to switch into dev mode
                } else {
                    flag = 2; //Condition to display error message
                }
            } else if (result == JOptionPane.CANCEL_OPTION) {
                flag = 3; //Close out of the window
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 2;
    }

    private boolean tryLogin(String username, String password) {
        boolean logonSuccessful = false;

        try {
            Database loginTest = new Database(username, password);
            loginTest.closeConnection();
            logonSuccessful = true;
        } catch (SQLException e) {
            System.err.println("Login with username \"" + username + "\" failed!");
        }

        return logonSuccessful;
    }

    public String getSuccessfulUsername() {
        return usernameSuccess;
    }

    public String getSuccessfulPassword() {
        return passwordSuccess;
    }
}





