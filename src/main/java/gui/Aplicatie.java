package gui;

import models.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Aplicatie {
    static JFrame fereastra = new JFrame();
    static User user;
    final static Object object=new Object();
    static Group group;

    private static void getEcranConectare(){
        fereastra.setTitle("Log in");
        fereastra.setVisible(true);

        JPanel jPanel = new JPanel();
        jPanel.setSize(fereastra.getSize());
        jPanel.setVisible(true);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor= GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        jPanel.add(new JLabel("LOG IN"),gridBagConstraints);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=1;
        jPanel.add(new JLabel("Email: "),gridBagConstraints);

        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=1;
        gridBagConstraints.gridwidth=2;
        JTextField textEmail = new JTextField("",30);
        jPanel.add(textEmail,gridBagConstraints);
        gridBagConstraints.gridwidth=1;


        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=2;
        jPanel.add(new JLabel("Password: "),gridBagConstraints);


        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=2;
        gridBagConstraints.gridwidth=2;
        JTextField textPassword = new JPasswordField("",30);
        jPanel.add(textPassword,gridBagConstraints);



        gridBagConstraints.gridwidth=1;

        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=3;
        JButton buttonLogin = new JButton("Log In");
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    User user = User.my_login(textEmail.getText(),textPassword.getText());
                    if(user==null){
                        JOptionPane.showMessageDialog(fereastra,
                                "Email-ul sau parola este incorect",
                                "EROARE",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        Aplicatie.user = user;
                        getLoggedScreen();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    JOptionPane.showMessageDialog(fereastra,
                            "Conexiunea la baza de date a esuat",
                            "EROARE",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonLogin.setEnabled(false);
        textPassword.getDocument().addDocumentListener(new DocumentListener() {
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
                buttonLogin.setEnabled(!textPassword.getText().equals("") && Utils.match_mail(textEmail.getText()));

            }
        });
        textEmail.getDocument().addDocumentListener(new DocumentListener() {
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
                buttonLogin.setEnabled(!textPassword.getText().equals("") && Utils.match_mail(textEmail.getText()));

            }
        });
        jPanel.add(buttonLogin,gridBagConstraints);
        gridBagConstraints.gridx=2;
        gridBagConstraints.gridy=3;
        JButton buttonForgetPassword = new JButton("Forgot Password");
        jPanel.add(buttonForgetPassword,gridBagConstraints);
        buttonForgetPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getForgotPassword();
            }
        });
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=3;
        JButton jButton = new JButton("Create new account");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCreateUser();
            }
        });
        jPanel.add(jButton,gridBagConstraints);
        gridBagConstraints.gridwidth=1;
        fereastra.setContentPane(jPanel);

    }

    private static void getLoggedScreen(){
        if(user==null)
            Aplicatie.getEcranConectare();
        fereastra.setTitle("Logged in as "+ user.getCompleteName());
        fereastra.setVisible(true);

        JPanel jPanel = new JPanel();
        jPanel.setSize(fereastra.getSize());
        jPanel.setVisible(true);

        jPanel.setLayout(new BorderLayout());
        final JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,null,null);

        jPanel.add(jSplitPane,BorderLayout.CENTER);

        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        JList<String> jList = new JList<>(defaultListModel);
        final ArrayList<Group> groups = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
                @Override
                public void run () {
                    synchronized (object) {
                            defaultListModel.clear();
                            groups.clear();
                            groups.addAll(Group.getGroups(user.getIdUser()));
                            defaultListModel.addAll(Objects.requireNonNull(groups.stream().map(Group::getTitle).collect(Collectors.toList())));
                    }
                }
        });
        thread.start();
        jSplitPane.setLeftComponent(new JScrollPane(jList));
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    group=groups.get(index);
                    jSplitPane.setRightComponent(messageScreen(groups.get(index)));
                } else if (evt.getClickCount() == 3) {

                    // Triple-click detected
                    int index = list.locationToIndex(evt.getPoint());
                }
            }
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isRightMouseButton(e) ) {
                    jList.setSelectedIndex(jList.locationToIndex(e.getPoint()));
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem itemDelete = new JMenuItem("Delete Group");
                    itemDelete.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int index= jList.getSelectedIndex();
                            Group group2 = groups.get(jList.getSelectedIndex());
                            defaultListModel.remove(index);
                            groups.remove(index);
                            group2.deleteGroup();
                            jSplitPane.setRightComponent(null);
                        }
                    });
                    JMenuItem itemRename = new JMenuItem("Rename");
                    itemRename.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFrame jFrame = new JFrame("Rename group "+groups.get(jList.getSelectedIndex()).getTitle());
                            JPanel jPanel1 = (JPanel) jFrame.getContentPane();
                            jPanel1.setLayout(new FlowLayout());
                            jFrame.setSize(new Dimension(300,300));
                            jPanel1.add(new JLabel("Titlu nou: "));
                            JTextField jTextField = new JTextField("",15);
                            jPanel1.add(jTextField);
                            Button button = new Button("Schimba nume");
                            button.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!(jTextField.getText().equals(""))){
                                        System.out.println(groups.get(jList.getSelectedIndex()).my_rename(jTextField.getText()));
                                        jFrame.dispose();
                                    }
                                }
                            });
                            jPanel1.add(button);
                            jPanel1.setVisible(true);
                            jFrame.setVisible(true);
                        }
                    });

                    JMenuItem itemAddUser = new JMenuItem("Add user");
                    itemAddUser.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFrame jFrame = new JFrame("Add user to "+groups.get(jList.getSelectedIndex()).getTitle());
                            jFrame.setSize(new Dimension(300,300));
                            JPanel jPanel = (JPanel) jFrame.getContentPane();
                            jPanel.setLayout(new FlowLayout());
                            jPanel.setSize(jFrame.getSize());
                            JPanel radioPanel = new JPanel(new FlowLayout());
                            TitledBorder border = BorderFactory.createTitledBorder("Criteriu: ");
                            radioPanel.setBorder(border);
                            JRadioButton radio1 = new JRadioButton("First Name");
                            JRadioButton radio2 = new JRadioButton("Last Name");
                            JRadioButton radio3 = new JRadioButton("Email");
                            radioPanel.add(radio1);
                            radioPanel.add(radio2);
                            radioPanel.add(radio3);
                            ButtonGroup buttonGroup = new ButtonGroup();
                            buttonGroup.add(radio1);
                            buttonGroup.add(radio2);
                            buttonGroup.add(radio3);
                            jPanel.add(radioPanel);
                            JTextField textField = new JTextField("",15);
                            Button button = new Button("Cauta");
                            jPanel.add(textField);
                            jPanel.add(button);
                            button.setEnabled(false);
                            textField.getDocument().addDocumentListener(new DocumentListener() {
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
                                    if (textField.getText().equals("")){
                                        button.setEnabled(false);
                                    }
                                    else {
                                        button.setEnabled(true);
                                    }

                                }
                            });
                            DefaultListModel<String> model = new DefaultListModel<>();
                            JList<String> jList = new JList<>( model );
                            jPanel.add(new JScrollPane(jList));
                            final java.util.List<User> users = new ArrayList<>();
                            button.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                                        AbstractButton button = buttons.nextElement();
                                        users.clear();
                                        if (button.isSelected()) {
                                            if(button.getText().equals("First Name")){
                                                users.addAll(Objects.requireNonNull(User.my_search("2", textField.getText())));
                                            }
                                            if(button.getText().equals("Last Name")){
                                                users.addAll(Objects.requireNonNull(User.my_search("1", textField.getText())));
                                            }
                                            if(button.getText().equals("Email")){
                                                users.addAll(Objects.requireNonNull(User.my_search("3", textField.getText())));
                                            }
                                            model.clear();
                                            System.out.println(users);
                                            model.addAll(users.stream().map(User::getCompleteSearch).collect(Collectors.toList()));
                                            break;
                                        }
                                    }
                                }
                            });
                            jList.addMouseListener(new MouseAdapter() {
                                public void mouseClicked(MouseEvent evt) {
                                    JList list = (JList)evt.getSource();
                                    if (evt.getClickCount() == 2) {
                                        int index = list.locationToIndex(evt.getPoint());
                                        try {
                                            groups.get(jList.getSelectedIndex()).add(users.get(index));
                                            jFrame.dispose();
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                    }
                                }
                                public void mousePressed(MouseEvent e) {
                                    if ( SwingUtilities.isRightMouseButton(e) ) {
                                        jList.setSelectedIndex(jList.locationToIndex(e.getPoint()));
                                        JPopupMenu menu = new JPopupMenu();
                                        JMenuItem itemNewGroup = new JMenuItem("Create a new group with this person");
                                        itemNewGroup.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                int index= jList.getSelectedIndex();
                                                User user = users.get(jList.getSelectedIndex());
                                                Group group = Group.create(Aplicatie.user,user);
                                                defaultListModel.add(defaultListModel.getSize(),group.getTitle());
                                                groups.add(group);
                                                jSplitPane.setRightComponent(null);
                                                jFrame.dispose();
                                            }
                                        });
                                        menu.add(itemNewGroup);
                                        menu.show(jList, e.getPoint().x, e.getPoint().y);
                                    }
                                }
                            });
                            jPanel.setVisible(true);
                            jFrame.setVisible(true);
                        }
                    });
                    JMenuItem itemLeave = new JMenuItem("Leave group");
                    itemLeave.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            groups.get(jList.getSelectedIndex()).deleteUserFromGroup(user.getIdUser());
                            defaultListModel.remove(jList.getSelectedIndex());
                            jSplitPane.setRightComponent(null);
                        }
                    });


                    JMenuItem itemRemoveUser = new JMenuItem("Remove user");
                    itemRemoveUser.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFrame jFrame = new JFrame("Remove user from "+groups.get(jList.getSelectedIndex()).getTitle());
                            JPanel jPanel1 = (JPanel) jFrame.getContentPane();
                            jPanel1.setLayout(new FlowLayout());
                            jFrame.setSize(new Dimension(300,300));
                            DefaultListModel<String> defaultListModel1 = new DefaultListModel<>();
                            JList<String> jList1 = new JList<>(defaultListModel1);
                            groups.get(jList.getSelectedIndex()).get_users();
                            defaultListModel1.addAll(groups.get(jList.getSelectedIndex()).getUsers().stream().map(User::getCompleteName).collect(Collectors.toList()));
                            jList1.addMouseListener(new MouseAdapter() {
                                   public void mouseClicked(MouseEvent evt) {
                                       JList list = (JList) evt.getSource();
                                       if (evt.getClickCount() == 2) {
                                           // Double-click detected
                                           int index = list.locationToIndex(evt.getPoint());
                                           user = groups.get(jList.getSelectedIndex()).getUsers().get(jList1.getSelectedIndex());
                                           groups.get(jList.getSelectedIndex()).deleteUserFromGroup(user.getIdUser());
                                           //jSplitPane.setRightComponent(messageScreen(groups.get(index)));
                                           jFrame.dispose();
                                       }
                                   }
                            });
                            jPanel1.add(new JScrollPane(jList1));
                            jPanel1.setVisible(true);
                            jFrame.setVisible(true);
                        }
                    });

                    JMenuItem itemExport = new JMenuItem("Export messages");
                    itemExport.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser chooser = new JFileChooser();
                            chooser.setCurrentDirectory(new java.io.File("."));
                            chooser.setDialogTitle("Select title");
                            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            //
                            // disable the "All files" option.
                            //
                            chooser.setAcceptAllFileFilterUsed(false);
                            //
                            if (chooser.showOpenDialog(jPanel) == JFileChooser.APPROVE_OPTION) {
                                System.out.println("getCurrentDirectory(): "
                                        +  chooser.getCurrentDirectory());
                                System.out.println("getSelectedFile() : "
                                        +  chooser.getSelectedFile());
                                Group group = new Group(groups.get(jList.getSelectedIndex()).getIdGroup(),groups.get(jList.getSelectedIndex()).getTitle());
                                group.getNewestMessages2();
                                while(group.getOlderMessages2().stream().count()>0){

                                }
                                try {
                                    String directory = chooser.getSelectedFile() +"\\"+group.getTitle().replace(' ','_');
                                    System.out.println(chooser.getSelectedFile() +"\\"+group.getTitle().replace(' ','_')+"_"+System.currentTimeMillis()+"\\"+"chat_at_"+System.currentTimeMillis()+".txt");
                                    File file = new File(directory);
                                    file.mkdir();
                                    file = new File(directory+"\\"+"chat_at_"+System.currentTimeMillis()+".txt");
                                    file.createNewFile();
                                    System.out.println(group.getMessages2().stream().count());
                                    FileWriter fileWriter = new FileWriter(file);
                                    for(Message2 message2: group.getMessages2()){
                                        fileWriter.write(group.getContentText(message2));
                                        fileWriter.write(" at " +message2.getSendTime());
                                        fileWriter.write("\n");
                                        if(message2 instanceof Message2Complex)
                                            ((Message2Complex) message2).get_file(directory);
                                    }
                                    fileWriter.flush();
                                    fileWriter.close();
                                    Desktop.getDesktop().open(new File(directory));
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }


                            }
                            else {
                                System.out.println("No Selection ");
                            }

                            /*
                            groups.get(jList.getSelectedIndex()).deleteUserFromGroup(user.getIdUser());
                            defaultListModel.remove(jList.getSelectedIndex());
                            jSplitPane.setRightComponent(null);
                             */
                        }

                    });

                    menu.add(itemRename);
                    menu.add(itemAddUser);
                    menu.add(itemRemoveUser);
                    menu.add(itemDelete);
                    menu.add(itemExport);
                    menu.add(itemLeave);
                    menu.show(jList, e.getPoint().x, e.getPoint().y);
                }
            }
        });
        fereastra.setContentPane(jPanel);
    }
    static JPanel messageScreen(Group group){
        JPanel jPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        JList<String> jList = new JList<>(defaultListModel);
        defaultListModel.addAll(group.getMessages2().stream().map(group::getContentText).collect(Collectors.toList()));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    while (group.getIdGroup()==Aplicatie.group.getIdGroup()) {
                        defaultListModel.addAll(group.getNewestMessages2().stream().map(group::getContentText).collect(Collectors.toList()));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
        jPanel.add(new JScrollPane(jList),BorderLayout.CENTER);
        jPanel.add(messageSend(group,defaultListModel),BorderLayout.SOUTH);
        jPanel.setVisible(true);
        return jPanel;
    }
    static JPanel messageSend(Group group,DefaultListModel defaultListModel){
        JPanel jPanel = new JPanel();
        jPanel.setVisible(true);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor= GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        JTextField jTextField = new JTextField("",20);
        jPanel.add(jTextField,gridBagConstraints);
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=0;
        Button button = new Button("Attach");
        final File[] file = new File[1];
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
                dialog.setMode(FileDialog.LOAD);
                dialog.setVisible(true);
                String path = dialog.getDirectory() + dialog.getFile();;
                if(path.equals("nullnull")){
                    file[0]=null;
                    button.setBackground(Color.red);
                } else {
                    file[0] = new File(path);
                    button.setBackground(Color.green);
                }
            }
        });
        jTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Message2.sendMessage(user.getIdUser(),group.getIdGroup(),jTextField.getText(),file[0])!=null){
                    jTextField.setText("");
                    button.setBackground(Color.red);
                }
            }
        });
        button.setBackground(Color.red);
        jPanel.add(button,gridBagConstraints);

        Button button1 = new Button("Get messages");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultListModel.addAll(0,group.getOlderMessages2().stream().map(group::getContentText).collect(Collectors.toList()));
            }
        });
        gridBagConstraints.gridx=2;
        gridBagConstraints.gridy=0;
        jPanel.add(button1,gridBagConstraints);
        return jPanel;
    }
    private static void getCreateUser(){
        fereastra.setTitle("Create new account");
        fereastra.setVisible(true);
        JPanel jPanel = new JPanel();
        fereastra.setSize(new Dimension(500,500));
        jPanel.setSize(fereastra.getSize());
        jPanel.setVisible(true);

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
                button.setEnabled(!first_name_text.getText().equals("") && !last_name_text.getText().equals("") && Utils.match_mail(email_text.getText())  && match_dob(dob_text.getText()) && !password_text.getText().equals("") && ok );
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

                if(User.my_signup(last_name_text.getText(),first_name_text.getText(),email_text.getText(),gender, LocalDate.parse(dob_text.getText(), DateTimeFormatter.ofPattern("d/MM/yyyy")),password_text.getText())){
                    getEcranConectare();
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
        jPanel.add(button);
        Button button1 = new Button("Go back to login screen");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getEcranConectare();
            }
        });
        jPanel.add(button1);
        fereastra.setContentPane(jPanel);

    }
    private static void getForgotPassword(){
        fereastra.setTitle("Forgot Password");
        fereastra.setVisible(true);
        JPanel jPanel = new JPanel();
        fereastra.setSize(new Dimension(500,500));
        jPanel.setSize(fereastra.getSize());
        jPanel.setVisible(true);
        jPanel.add(new JLabel("Enter Email: "));
        JTextField jTextField = new JTextField("",30);
        jPanel.add(jTextField);
        JButton jButton = new JButton("Verify code");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getVerifyCode();
            }
        });
        JButton jButton1 = new JButton("Send code");
        jTextField.getDocument().addDocumentListener(new DocumentListener() {
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
                jButton1.setEnabled(Utils.match_mail(jTextField.getText()));
            }
        });
        jButton1.setEnabled(false);
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (object){
                    Connection conn = null;
                    try {
                        conn = DriverManager.getConnection(Utils.connectionString,Utils.user, Utils.password);
                        String query = "UPDATE users set verification_code=? where email=?";
                        PreparedStatement preparedStatement = conn.prepareStatement(query);
                        String code = new Random().ints(10, 65, 101).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
                        preparedStatement.setString(1,code);
                        preparedStatement.setString(2,jTextField.getText().trim().toLowerCase(Locale.ROOT));
                        if(preparedStatement.executeUpdate()>0){
                            send_code_via_mail(jTextField.getText().trim().toLowerCase(Locale.ROOT),code);
                        }
                        getForgotPassword();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            }
        });
        jPanel.add(jButton);
        jPanel.add(jButton1);
        JButton jButton2 = new JButton("Go back to Login screen");
        jButton2.addActionListener(e1 -> getEcranConectare());
        jPanel.add(jButton2);
        fereastra.setContentPane(jPanel);
    }
    private static boolean send_code_via_mail(String to,String code){
        String from = "server@cti.ro";
        String host = "smtp.mailtrap.io";
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.port", "2525");
        properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ade94f3825269e", "4d986cc037cffc");
            }
        };
        Session session = Session.getDefaultInstance(properties,authenticator);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Verification code");
            message.setText("The verification code is: "+code+".");
            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }
    private static void getVerifyCode(){
        fereastra.setTitle("Verify Code");
        fereastra.setVisible(true);
        JPanel jPanel = new JPanel();
        fereastra.setSize(new Dimension(500,500));
        jPanel.setSize(fereastra.getSize());
        jPanel.setVisible(true);
        jPanel.add(new JLabel("Enter Verification code: "));
        JTextField jTextField = new JTextField("",30);
        jPanel.add(jTextField);
        JButton jButton = new JButton("Verify code");
        jButton.setEnabled(false);
        jPanel.add(jButton);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    User user = User.my_search_code(jTextField.getText());
                    if(user!=null){
                        JPanel jPanel1 = new JPanel();
                        jPanel1.setSize(fereastra.getSize());
                        jPanel1.setVisible(true);
                        jPanel1.add(new JLabel("New Password"));
                        JTextField jTextField1 = new JPasswordField("",30);
                        jPanel1.add(jTextField1);
                        JButton jButton1 = new JButton("Change password");
                        jButton1.setEnabled(false);
                        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
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
                                jButton1.setEnabled(!jTextField1.getText().equals(""));
                            }
                        });
                        jButton1.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if(User.edit_password(user.getIdUser(),jTextField1.getText())){
                                    getEcranConectare();
                                }
                            }
                        });
                        jPanel1.add(jButton1);
                        JButton jButton2 = new JButton("Go back to Login screen");
                        jButton2.addActionListener(e1 -> getEcranConectare());
                        jPanel1.add(jButton2);
                        fereastra.setContentPane(jPanel1);
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        jTextField.getDocument().addDocumentListener(new DocumentListener() {
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
                jButton.setEnabled(!jTextField.getText().equals(""));
            }
        });
        JButton jButton2 = new JButton("Go back to Login screen");
        jButton2.addActionListener(e1 -> getEcranConectare());
        jPanel.add(jButton2);
        fereastra.setContentPane(jPanel);

    }

    private static boolean match_dob(String dob){
        SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy");
        format.setLenient(false);
        try {
            format.parse(dob);
            return true;
        }
        catch(ParseException e){
            return false;
        }
    }
    public static void main(String[] args) {
        fereastra.setVisible(true);
        fereastra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fereastra.setSize(600,600);
        fereastra.setMinimumSize(new Dimension(300,300));
        /*
        try {
            Aplicatie.user=User.my_login("cc@cti.ro","asd");
            getLoggedScreen();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
         */
        getEcranConectare();

    }


}
