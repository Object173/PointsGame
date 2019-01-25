package com.object173.pointsgame.view;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.controller.Player;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.events.NetworkEventAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.security.cert.Extension;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Ярослав on 30.12.2016.
 */
//окно настроек игры
public class MainMenuForm extends JFrame {
    private GameForm gameForm=new GameForm(this); //окно игры
    private Options options=Options.getInstance(); //ссылка на настройки

    private int players=0; //количество подключенных игроков (сервер)
    private boolean isServer=false; //если создан сервер

    private  JTabbedPane tabbedPane; //вкладки
    private JPanel serverPanel; //панель сервера
    private JPanel connectPanel; //панель клиента
    private JPanel optionsPanel; //панель настроек
    private JPanel helpPanel; //панель помощи

    //клиентские компоненты
    private JLabel connectStatusLabel;
    private JLabel playerNameLabel;
    private JTextArea playerNameArea;
    private JLabel ipLabel;
    private JTextArea ipTextArea;
    private JLabel portLabel;
    private JTextArea portArea;
    private JButton connectButton;
    private JButton disconnectButton;

    //серверные компоненты
    private JLabel serverStatusLabel;
    private JLabel serverNameLabel;
    private JTextArea serverNameArea;
    private JLabel serverIpLabel;
    private JLabel serverPortLabel;
    private JTextArea serverPortArea;
    private JButton serverConnectButton;
    private JButton serverDisconnectButton;
    private JLabel serverPlayersLabel;
    private JButton startGameButton;

    //компоненты настроек
    private JLabel sizeXLabel;
    private JLabel sizeYLabel;
    private JFormattedTextField sizeXField;
    private JFormattedTextField sizeYField;
    private JLabel maxPlayersLabel;
    private JFormattedTextField maxPlayersField;
    private JLabel singlePlayerLabel;
    private JComboBox singlePlayerBox;
    private DefaultComboBoxModel singlePlayerModel;
    private JButton acceptButton;
    private JButton cancelButton;

    //компоненты панели помощи
    private JTextArea helpArea;
    //конструктор
    public MainMenuForm()
    {   //при закрытии окна закрываем приложение
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Options");

        createGUI(); //создаем интерфейс

        pack(); //устанавливаем размеры окна
        setSize(new Dimension(300,400));
        setVisible(true); //включаем отоброжение окна
    }
    //инициализация интерфейса
    private void createGUI()
    {
        setLayout(new BorderLayout());

        tabbedPane=new JTabbedPane();
        add(tabbedPane);

        initConnectPanel(tabbedPane); //инициализация панели клиента
        initServerPanel(tabbedPane); //инициализация панели сервера
        initOptionsPanel(tabbedPane); //инициализация панели настроек
        initHelpPanel(tabbedPane); //инициализация панели помощи
    }
    //инициализация панели клиента
    private void initConnectPanel(JTabbedPane parent)
    {
        //клиентская панель
        connectPanel=new JPanel(new GridLayout(10,2));
        parent.addTab("Клиент",connectPanel);

        connectStatusLabel=new JLabel("Статус: Нет подключения");
        connectPanel.add(connectStatusLabel);
        playerNameLabel=new JLabel("Имя игрока");
        connectPanel.add(playerNameLabel);
        playerNameArea=new JTextArea("noname");
        connectPanel.add(playerNameArea);
        ipLabel=new JLabel("ip сервера");
        connectPanel.add(ipLabel);
        ipTextArea=new JTextArea("127.0.0.1");
        connectPanel.add(ipTextArea);
        portLabel=new JLabel("порт сервера");
        connectPanel.add(portLabel);
        portArea=new JTextArea("7000");
        connectPanel.add(portArea);
        connectButton=new JButton("подключиться");
        connectPanel.add(connectButton);
        disconnectButton=new JButton("отключиться");
        connectPanel.add(disconnectButton);
        disconnectButton.setEnabled(false);

        connectButton.addActionListener(new ActionListener() //события на нажатие кнопки подключения
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = playerNameArea.getText(); //валидация имени игрока, ip и порта
                if(name.length()<3)
                {
                    JOptionPane.showMessageDialog(null, "Имя игрока слишком короткое",
                            "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int port=7000;
                try
                {
                    port=Integer.parseInt(serverPortArea.getText());
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, "Некорректный порт",
                            "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String ip = ipTextArea.getText();
                isServer=false; //сброс флага сервера (мы клиент)
                EventProducer.fireConnectPlayer(name,ip,port); //событие подключения к серверу
            }
        });
        disconnectButton.addActionListener(new ActionListener() //нажатие кнопки отключения
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EventProducer.fireDisconnectPlayer(); //вызываем событие отключения
            }
        });
    }
    //инициализация панели сервера
    private void initServerPanel(JTabbedPane parent)
    {
        //серверная панель
        serverPanel=new JPanel(new GridLayout(10,2));
        parent.addTab("Сервер",serverPanel);

        serverStatusLabel=new JLabel("Статус: сервер не активен");
        serverPanel.add(serverStatusLabel);
        serverPlayersLabel=new JLabel("Игроков подключено: 0");
        serverPanel.add(serverPlayersLabel);
        serverIpLabel=new JLabel("127.0.0.1");
        try
        {   //определяем свой ip
            serverIpLabel.setText(InetAddress.getLocalHost().getHostAddress().toString());
        }
        catch(Exception ex) {}
        serverPanel.add(serverIpLabel);
        serverNameLabel=new JLabel("Имя игрока");
        serverPanel.add(serverNameLabel);
        serverNameArea=new JTextArea("noname");
        serverPanel.add(serverNameArea);
        serverPortLabel=new JLabel("Порт сервера");
        serverPanel.add(serverPortLabel);
        serverPortArea=new JTextArea("7000");
        serverPanel.add(serverPortArea);
        serverConnectButton=new JButton("Запустить");
        serverPanel.add(serverConnectButton);
        serverDisconnectButton=new JButton("Отключить");
        serverPanel.add(serverDisconnectButton);
        serverDisconnectButton.setEnabled(false);
        startGameButton=new JButton("Начать игру");
        serverPanel.add(startGameButton);
        startGameButton.setEnabled(false);
        //нажатие кнопки создания сервера
        serverConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = serverNameArea.getText(); //валидация имени игрока
                if(name.length()<3)
                {
                    JOptionPane.showMessageDialog(null, "Имя игрока слишком короткое",
                            "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int port=7000;
                try
                {
                    port=Integer.parseInt(serverPortArea.getText());
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, "Некорректный порт",
                            "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                EventProducer.fireStartServer(name,port); //событие создания сервера
            }
        });
        //нажатие кнопки отключения сервера
        serverDisconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EventProducer.fireStopServer(); //событие отключения сервера
            }
        });
        //нажатие кнопки начала игры
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isServer=true; //мы становимся сервером
                EventProducer.fireSignalStartGame(); //событие сигнал кначалу игры
            }
        });
        //слушатель сетевых событий
        EventProducer.addListener(new NetworkEventAdapter(){
            @Override
            //статус активности сервера
            public void setServerEnabled(boolean f)
            {
                if(f)
                {
                    serverStatusLabel.setText("Статус: сервер активен");
                }
                else
                {
                    MainMenuForm.this.singlePlayerModel.removeAllElements();
                    serverStatusLabel.setText("Статус: сервер не активен");
                    players=0;
                    serverPlayersLabel.setText("Игроков подключено: "+players);
                    startGameButton.setEnabled(false);
                }

                serverConnectButton.setEnabled(!f);
                serverDisconnectButton.setEnabled(f);
                serverPortArea.setEnabled(!f);
                serverNameArea.setEnabled(!f);

                maxPlayersField.setEditable(!f);
            }
            @Override
            //добавление игрока
            public void addPlayer(String name)
            {
                players++;
                serverPlayersLabel.setText("Игроков подключено: "+players);
                if(players>=options.getMIN_PLAYERS()) startGameButton.setEnabled(true);

                singlePlayerModel.addElement(name);
            }
            @Override
            //отключение игрока
            public void disconnectPlayer(String name)
            {
                System.out.println(name);
                players--;
                if(players<0) players=0;
                serverPlayersLabel.setText("Игроков подключено: "+players);
                if(players<options.getMIN_PLAYERS()) startGameButton.setEnabled(false);

                singlePlayerModel.removeElement(name);
            }
            @Override
            //статус подклчения к серверу
            public  void setConnectPlayerStatus(boolean f)
            {
                if(f)
                {
                    connectStatusLabel.setText("Статус: подключен к серверу");
                }
                else
                {
                    connectStatusLabel.setText("Статус: не подключен");
                }

                connectButton.setEnabled(!f);
                disconnectButton.setEnabled(f);
                playerNameArea.setEnabled(!f);
                ipTextArea.setEnabled(!f);
                portArea.setEnabled(!f);
            }
            @Override
            //начало игры
            public void startGame(String[] players)
            {
                MainMenuForm.this.startGame(players);
            }
        });
    }
    //инициализация панели настроек
    private void initOptionsPanel(JTabbedPane parent)
    {
        optionsPanel=new JPanel(new GridLayout(5,2));
        parent.addTab("Настройки",optionsPanel);

        sizeXLabel=new JLabel("Размер поля Х");
        optionsPanel.add(sizeXLabel);
        sizeXField=new JFormattedTextField(new DecimalFormat("#"));
        sizeXField.setText(Integer.toString(options.getWidth()));
        optionsPanel.add(sizeXField);
        sizeYLabel=new JLabel("Размер поля Y");
        optionsPanel.add(sizeYLabel);
        sizeYField=new JFormattedTextField(new DecimalFormat("#"));
        sizeYField.setText(Integer.toString(options.getHeight()));
        optionsPanel.add(sizeYField);

        maxPlayersLabel=new JLabel("макс игроков");
        optionsPanel.add(maxPlayersLabel);
        maxPlayersField=new JFormattedTextField(new DecimalFormat("#"));
        maxPlayersField.setText(Integer.toString(options.getCountPlayer()));
        optionsPanel.add(maxPlayersField);

        singlePlayerLabel=new JLabel("Первый ход");
        optionsPanel.add(singlePlayerLabel);
        singlePlayerModel=new DefaultComboBoxModel();
        singlePlayerBox=new JComboBox(singlePlayerModel);
        optionsPanel.add(singlePlayerBox);

        acceptButton=new JButton("Применить");
        optionsPanel.add(acceptButton);
        cancelButton=new JButton("Отменить");
        optionsPanel.add(cancelButton);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                  MainMenuForm.this.setOptions();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenuForm.this.setDeffaultOptions();
            }
        });
    }
    //установка настроек
    private void setOptions()
    {
        int width=0, height=0, maxPlayers=0, singlePlayer=0;

        try //валидация
        {
            width=Integer.parseInt(sizeXField.getText());
            height=Integer.parseInt(sizeYField.getText());
            maxPlayers=Integer.parseInt(maxPlayersField.getText());
            singlePlayer=singlePlayerBox.getSelectedIndex();
            if(singlePlayer<0) singlePlayer=0;

            EventProducer.fireSetOptions(width,height,maxPlayers,singlePlayer);
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, "Ошибка ввода",
                    "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
        }
        setDeffaultOptions(); //получение установленных настроек
    }
    //установка текущих настроек
    private void setDeffaultOptions()
    {
        sizeXField.setText(Integer.toString(options.getWidth()));
        sizeYField.setText(Integer.toString(options.getHeight()));
        maxPlayersField.setText(Integer.toString(options.getCountPlayer()));
        singlePlayerModel.setSelectedItem(singlePlayerBox.getItemAt(options.getSinglePlayer()));
    }
    //инициализация понели помощи
    private void initHelpPanel(JTabbedPane parent)
    {
        helpPanel=new JPanel(new BorderLayout());
        tabbedPane.addTab("Помощь",helpPanel);

        helpArea=new JTextArea();
        helpArea.setEditable(false);
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpPanel.add(helpArea,BorderLayout.CENTER);

        File file;
        FileReader file_reader = null;
        try{
            file = new File(options.pathHelp);
            file_reader = new FileReader(file);
            char buffer[] = new char[4096];
            int len;
            helpArea.setText("");
            while ((len = file_reader.read(buffer)) != -1){
                String s = new String (buffer, 0, len);
                helpArea.append(s);
            }
            helpArea.setCaretPosition(0);
        }catch (Exception e){
            helpArea.setText("файл не найден (");
        }
    }
    //запуск игры
    private void startGame(String[] players)
    {
        gameForm.initForm(players,isServer); //инициализируем игровое окно
        gameForm.show(); //открываем игровое окно
        this.hide(); //скрываем текущее
    }
}
