package gui;
import instances.LoggedInScreen;
import models.Group;
import models.Message;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;

public class EcranLogat {
    static JFrame fereastra;
    static User user;
    static ArrayList<Group> groups = new ArrayList<>();
    static JTabbedPane tabbedPane;
    static final Object object = null;
    public static void setUser(User user) {
        EcranLogat.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static JPanel get_main(){
        JPanel main_screen = new JPanel();
        Button button = new Button("Show groups");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.add("Groups",get_groups());
            }
        });
        Button button1 = new Button("Vizualizare date proprii");
        Button button2 = new Button("Inchide");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fereastra.dispose();
                user=null;
                groups=null;
                tabbedPane=null;
                InterfataGrafica.fereastra.setVisible(true);
            }
        });
        Button button3 = new Button("Cauta utilizator");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.add("Search",search_screen());
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
            }
        });
        main_screen.add(button);
        main_screen.add(button1);
        main_screen.add(button2);
        main_screen.add(button3);
        return main_screen;
    }

    public static JPanel get_second(){
        JPanel main_screen = new JPanel();
        Button button = new Button("Inchide");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());

            }
        });
        main_screen.add(button);
        return main_screen;
    }

    public static JPanel get_groups(){
        JPanel main_screen = new JPanel();
        main_screen.setLayout(new BorderLayout());
        ArrayList<Group> groups = Group.getGroups(user.getIdUser());
        JList<String> jList = new JList<>(groups.stream().map(Group::getTitle).toArray(String[]::new));
        JSplitPane jSplitPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(jList),null);
        jSplitPane.setMinimumSize(new Dimension(500,500));
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    jSplitPane.setBottomComponent(message_screen(groups.get(index)));
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(),groups.get(index).getTitle());
                } else if (evt.getClickCount() == 3) {
                    // Triple-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    jSplitPane.setBottomComponent(admin_screen(groups.get(index)));
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(),groups.get(index).getTitle());
                }
            }
        });

        main_screen.add(jSplitPane,BorderLayout.CENTER);
        Button button = new Button("Inchide");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
            }
        });
        main_screen.add(button,BorderLayout.SOUTH);
        return main_screen;
    }
    public static JPanel message_screen(Group group){
        JPanel main_screen = new JPanel();
        main_screen.setLayout(new BorderLayout());
        group.getNewestMessages();
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> jList = new JList<>( model );
        model.addAll(group.getMessages().stream().map(group::getContentText).collect(Collectors.toList()));
        jList.setMinimumSize(new Dimension(200,200));
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(jScrollPane.getVerticalScrollBar().getValue() == jScrollPane.getVerticalScrollBar().getMinimum()){
                    model.addAll(0,group.getOlderMessages().stream().map(group::getContentText).collect(Collectors.toList()));
                }
                System.out.println(jScrollPane.getVerticalScrollBar().getModel().getExtent()+" "+jScrollPane.getVerticalScrollBar().getMaximum()+" "+jScrollPane.getVerticalScrollBar().getMinimum());
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        model.addAll(group.getNewestMessages().stream().map(group::getContentText).collect(Collectors.toList()));
                        Thread.sleep(1000);
                    }
                    catch(ConcurrentModificationException | InterruptedException ignored){
                        ignored.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        main_screen.add(jScrollPane,BorderLayout.CENTER);
        JTextField jTextField = new JTextField("",30);
        jTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(new Message(user.getIdUser(),group.getIdGroup(),jTextField.getText()).send()){
                    jTextField.setText("");
                    jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getMaximum());
                }
            }
        });
        main_screen.add(jTextField,BorderLayout.SOUTH);
        return main_screen;
    }

    public static JPanel admin_screen(Group group){
        JPanel main_screen = new JPanel();
        group.get_users();
        main_screen.setLayout(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> jList = new JList<>( model );
        model.addAll(group.getUsers().stream().map(User::getCompleteName).collect(Collectors.toList()));
        JSplitPane jSplitPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(jList),null);
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    JPanel jPanel = new JPanel();
                    Button button = new Button("Remove from group");
                    button.addActionListener(e -> group.deleteUserFromGroup(group.getUsers().get(index).getIdUser()));
                    Button button2 = new Button("Delete group");
                    button2.addActionListener(e -> {
                            group.deleteGroup();
                            tabbedPane.remove(tabbedPane.getSelectedIndex());
                    });
                    Button button1 = new Button("Add person");
                    button1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JDialog dialog = new JDialog();
                            dialog.setContentPane(search(group));
                            dialog.setMinimumSize(new Dimension(300,300));
                            dialog.setVisible(true);
                        }
                    });
                    jPanel.add(button1);
                    jPanel.add(button);
                    jPanel.add(button2);
                    jSplitPane.setBottomComponent(jPanel);
                }
            }
        });
        main_screen.add(jSplitPane,BorderLayout.CENTER);
        return main_screen;
    }
    public static JPanel search_screen(){
        JPanel jPanel = new JPanel();
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
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    System.out.println("da");
                    if (button.isSelected()) {
                        java.util.List<User> users = null;
                        if(button.getText().equals("First Name")){
                            users=User.my_search("2",textField.getText());
                        }
                        if(button.getText().equals("Last Name")){
                            users=User.my_search("1",textField.getText());
                        }
                        if(button.getText().equals("Email")){
                            users=User.my_search("3",textField.getText());
                        }
                        model.clear();
                        model.addAll(users.stream().map(User::getCompleteSearch).collect(Collectors.toList()));
                    }

                }
            }
        });
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    System.out.println(model.getElementAt(index));
                    String [] cuvinte = model.getElementAt(index).split(" ");
                    System.out.println(User.my_search("3",cuvinte[cuvinte.length-1]));
                    tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
                    tabbedPane.add(cuvinte[0]+" "+cuvinte[1],user_screen(User.my_search("3",cuvinte[cuvinte.length-1]).get(0)));
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
                }
            }
        });
        return jPanel;
    }
    public static JPanel user_screen(User user){
        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel(user.getCompleteName()));
        jPanel.add(new JLabel(user.getEmail()));
        jPanel.add(new JLabel(user.getGender()));
        jPanel.add(new JLabel(String.valueOf(user.getBirthDate())));
        jPanel.add(new Button("Adauga intr-un grup"));
        Button button = new Button("Creaza grup nou cu aceasta persoana");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Group.create(EcranLogat.user,user)!=null){
                    tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
                }
            }
        });
        jPanel.add(button);
        return jPanel;
    }

    public static JPanel search(Group group){
            JPanel jPanel = new JPanel();
            jPanel.setMinimumSize(new Dimension(300,300));
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
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        System.out.println("da");
                        if (button.isSelected()) {
                            java.util.List<User> users = null;
                            if(button.getText().equals("First Name")){
                                users=User.my_search("2",textField.getText());
                            }
                            if(button.getText().equals("Last Name")){
                                users=User.my_search("1",textField.getText());
                            }
                            if(button.getText().equals("Email")){
                                users=User.my_search("3",textField.getText());
                            }
                            model.clear();
                            model.addAll(users.stream().map(User::getCompleteSearch).collect(Collectors.toList()));
                        }

                    }
                }
            });
            jList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    JList list = (JList)evt.getSource();
                    if (evt.getClickCount() == 2) {
                        // Double-click detected
                        int index = list.locationToIndex(evt.getPoint());
                        System.out.println(model.getElementAt(index));
                        String [] cuvinte = model.getElementAt(index).split(" ");
                        try {
                            if(group.add(User.my_search("3", cuvinte[cuvinte.length - 1]).get(0))){
                                JDialog parent = (JDialog) jPanel.getTopLevelAncestor();
                                parent.dispose();
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }
                }
            });
            return jPanel;
    }

    public static JPanel get_message_screen(Group group){
        JPanel main_screen = new JPanel();
        Button button = new Button("Inchide");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
            }
        });
        main_screen.add(button);
        return main_screen;
    }

    public static JFrame getScreen(){
        if(user==null) return null;
        if(InterfataGrafica.fereastra!=null){
            InterfataGrafica.fereastra.setVisible(false);
        }
        fereastra = new JFrame("Conectat ca "+user.getFirstName()+" "+user.getLastName());
        JPanel panou = (JPanel) fereastra.getContentPane();
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Hello",null,get_main());
        tabbedPane.addTab("You too",null,get_second());
        panou.add(tabbedPane);
        fereastra.setSize(new Dimension(600,600));
        fereastra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return fereastra;
    }

    public static void main(String[] args) throws SQLException {
        User user = User.my_login("cc@cti.ro","asd");
        EcranLogat.setUser(user);
        Frame logat = EcranLogat.getScreen();
        if(logat!=null){
            logat.setVisible(true);
            logat.dispose();
        }
        Objects.requireNonNull(EcranLogat.getScreen()).setVisible(true);
    }
}
