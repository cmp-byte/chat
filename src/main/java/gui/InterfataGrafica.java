package gui;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;

public class InterfataGrafica {
    static JFrame fereastra;
    static GridBagLayout gridBag;
    static GridBagConstraints gbcons;

    static void adauga(Component comp, int x, int y, int w, int h) {
        gbcons.gridx = x;
        gbcons.gridy = y;
        gbcons.gridwidth = w;
        gbcons.gridheight = h;
        gridBag.setConstraints(comp, gbcons);
        fereastra.add(comp);
    }
    public static JDialog sign_up(){
        fereastra.setVisible(false);
        JDialog jDialog = new JDialog();
        jDialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
               fereastra.setVisible(true);
            }
        });
        jDialog.setMinimumSize(new Dimension(500,500));
        JPanel jPanel = (JPanel) jDialog.getContentPane();
        jPanel.setLayout(new FlowLayout());
        JLabel first_name_label = new JLabel("First Name");
        JTextField first_name_text = new JTextField("",40);
        JLabel last_name_label = new JLabel("Last Name");
        JTextField last_name_text = new JTextField("",40);
        JLabel email_label = new JLabel("Email");
        JTextField email_text = new JTextField("",40);

        JPanel radioPanel = new JPanel(new FlowLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Gender: ");
        radioPanel.setBorder(border);
        JRadioButton radio1 = new JRadioButton("M");
        JRadioButton radio2 = new JRadioButton("F");
        radioPanel.add(radio1);
        radioPanel.add(radio2);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radio1);
        buttonGroup.add(radio2);


        JLabel dob_label = new JLabel("DOB(d/MM/yyyy)");
        JTextField dob_text = new JTextField("",40);
        //LocalDate birthDate = LocalDate.parse(new Scanner(System.in).nextLine(), DateTimeFormatter.ofPattern("d/MM/yyyy"))

        JLabel password_label = new JLabel("Password");
        JTextField password_text = new JPasswordField("",40);

        Button button = new Button("Sign up");
        button.setEnabled(false);
        DocumentListener setActive = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }
            public void changed() {
                boolean ok = false;
                for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected()) {
                        ok=true;
                    }
                }
                button.setEnabled(!first_name_text.getText().equals("") && !last_name_text.getText().equals("") && !email_text.getText().equals("")  && !dob_text.getText().equals("")  && !password_text.getText().equals("") && ok );
            }
        };
        radio1.addActionListener(e -> button.setEnabled(!first_name_text.getText().equals("") && !last_name_text.getText().equals("") && !email_text.getText().equals("")  && !dob_text.getText().equals("")  && !password_text.getText().equals("")));
        radio2.addActionListener(e -> button.setEnabled(!first_name_text.getText().equals("") && !last_name_text.getText().equals("") && !email_text.getText().equals("")  && !dob_text.getText().equals("")  && !password_text.getText().equals("")));
        password_text.getDocument().addDocumentListener(setActive);
        first_name_text.getDocument().addDocumentListener(setActive);
        last_name_text.getDocument().addDocumentListener(setActive);
        email_text.getDocument().addDocumentListener(setActive);
        dob_text.getDocument().addDocumentListener(setActive);

        button.addActionListener(e -> {
            String gender = null;
            for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button1 = buttons.nextElement();
                if (button1.isSelected()) {
                    gender= button1.getText();
                }
            }

            try {

                if(User.my_signup(last_name_text.getText(),first_name_text.getText(),email_text.getText(),gender,LocalDate.parse(dob_text.getText(), DateTimeFormatter.ofPattern("d/MM/yyyy")),password_text.getText())){
                    jDialog.dispose();
                    fereastra.setVisible(true);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });


        jPanel.add(first_name_label);
        jPanel.add(first_name_text);
        jPanel.add(last_name_label);
        jPanel.add(last_name_text);
        jPanel.add(email_label);
        jPanel.add(email_text);
        jPanel.add(radioPanel);
        jPanel.add(dob_label);
        jPanel.add(dob_text);
        jPanel.add(password_label);
        jPanel.add(password_text);
        jPanel.add(button);
        jDialog.setVisible(true);
        return jDialog;
    }

    public static void main(String []args) {
        fereastra = new JFrame("Test GridBagLayout");
        gridBag = new GridBagLayout();
        gbcons = new GridBagConstraints();
        gbcons.weightx = 1.0;
        gbcons.weighty = 1.0;

        gbcons.insets = new Insets(5, 5, 5, 5);
        fereastra.setLayout(gridBag);
        JLabel lblLogin = new JLabel("LOGIN", JLabel.CENTER);
        lblLogin.setFont(new Font(" Arial ", Font.BOLD, 24));
        gbcons.fill = GridBagConstraints.BOTH;
        adauga(lblLogin, 0, 0, 4, 2);

        JLabel lblNume = new JLabel("Utilizator:");
        gbcons.fill = GridBagConstraints.NONE;
        gbcons.anchor = GridBagConstraints.EAST;
        adauga(lblNume, 0, 2, 1, 1);

        JLabel lblParola = new JLabel("Parola:");
        adauga(lblParola, 0, 3, 1, 1);

        JTextField txtUtilizator = new JTextField("", 30);
        gbcons.fill = GridBagConstraints.HORIZONTAL;
        gbcons.anchor = GridBagConstraints.CENTER;
        adauga(txtUtilizator, 1, 2, 2, 1);

        JTextField txtParola = new JPasswordField("", 30);
        adauga(txtParola, 1, 3, 2, 1);

        JButton btnSalvare = new JButton(" Conectare ");
        btnSalvare.setEnabled(false);

        DocumentListener setActive = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }
            public void changed() {
                btnSalvare.setEnabled(!txtUtilizator.getText().equals("") && !txtParola.getText().equals(""));
            }
        };
        txtUtilizator.getDocument().addDocumentListener(setActive);
        txtParola.getDocument().addDocumentListener(setActive);
        gbcons.fill = GridBagConstraints.HORIZONTAL;
        btnSalvare.addActionListener(e -> {
            try {
                User user = User.my_login(txtUtilizator.getText(),txtParola.getText());
                if(user==null){
                    JOptionPane.showMessageDialog(fereastra,
                            "Email-ul sau parola este incorect",
                            "EROARE",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    EcranLogat.setUser(user);
                    JFrame logat = EcranLogat.getScreen();
                    fereastra.setVisible(false);
                    if(logat!=null){
                        logat.setVisible(true);
                        logat.dispose();
                    }
                    Objects.requireNonNull(EcranLogat.getScreen()).setVisible(true);
                    //JOptionPane.showMessageDialog(fereastra, "Veti fi redirectionat imediat", "Acces", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                JOptionPane.showMessageDialog(fereastra,
                        "Eroare la conectare la baza de date",
                        "EROARE",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        adauga(btnSalvare, 1, 4, 1, 1);


        JButton btnCreare= new JButton(" Creare cont ");
        btnCreare.addActionListener(e -> InterfataGrafica.sign_up());
        adauga(btnCreare, 2, 4, 1, 1);

        fereastra.setSize(new Dimension(300, 300));
        fereastra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fereastra.setVisible(true);
    }
}

