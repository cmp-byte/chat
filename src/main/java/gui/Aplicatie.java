package gui;

import models.*;

import javax.lang.model.type.ArrayType;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
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
                if (textPassword.getText().equals("") || textEmail.getText().equals("")){
                    buttonLogin.setEnabled(false);
                }
                else {
                    buttonLogin.setEnabled(true);
                }

            }
        });
        jPanel.add(buttonLogin,gridBagConstraints);



        gridBagConstraints.gridx=2;
        gridBagConstraints.gridy=3;
        jPanel.add(new JButton("Forget Password"),gridBagConstraints);

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

                    menu.add(itemRename);
                    menu.add(itemAddUser);
                    menu.add(itemRemoveUser);
                    menu.add(itemDelete);
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
