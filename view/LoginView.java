package CrowdFunding.view;

import CrowdFunding.controller.AuthController;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    public interface OnLogin { void success(); }

    private final AuthController auth;
    private final OnLogin onLogin;

    private final JTextField userField = new JTextField(16);
    private final JPasswordField passField = new JPasswordField(16);

    public LoginView(AuthController auth, OnLogin onLogin) {
        this.auth = auth;
        this.onLogin = onLogin;

        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; add(new JLabel("Username:"), g);
        g.gridx = 1; g.gridy = 0; add(userField, g);

        g.gridx = 0; g.gridy = 1; add(new JLabel("Password:"), g);
        g.gridx = 1; g.gridy = 1; add(passField, g);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton login = new JButton("Login");
        JButton clear = new JButton("Clear");
        btns.add(clear); btns.add(login);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; add(btns, g);

        clear.addActionListener(e -> { userField.setText(""); passField.setText(""); });
        login.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        try {
            auth.login(userField.getText(), new String(passField.getPassword()));
            JOptionPane.showMessageDialog(this, "Login สำเร็จ: " + auth.getCurrentUser());
            if (onLogin != null) onLogin.success();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
