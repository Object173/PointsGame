package com.object173.pointsgame.view;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventAdapter;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.events.NetworkEventAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Ярослав on 30.12.2016.
 */

//игровое окно
public class GameForm extends JFrame {
    private MainMenuForm parent; //родительское окно (главное меню)
    private boolean isGame=false; //флаг активности игры

    private Options options = Options.getInstance(); //ссылка на настройки

    private Cell[][] field; //игровое поле
    private ArrayList<String> playernames; //список имени игроков
    private ArrayList<JLabel> scoreLabels; //список баллов игроков

    private JPanel gamePanel; //панели игры, меню и кнопок
    private JPanel menuPanel;
    private JPanel buttonPanel;

    private JButton disconnectButton; //кнопки управления
    private JButton restartButton;
    private JLabel statusLabel;
    //конструктор
    public GameForm(MainMenuForm parent)
    {
        this.parent=parent;

        this.setTitle("PointsGame");

        setSize(400,500);

        createGUI(); //создание интерфейса
        //слушатель события закрытия окна
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(isGame) //если идет игра
                {
                    isGame=false;
                    EventProducer.fireDisconnectPlayer(); //событие отключения игрока
                }
                GameForm.this.close(); //закрываем окно

                super.windowClosing(e);
            }
        });
        //слуаштель событий
        EventProducer.addListener(new EventAdapter() {
            @Override
            public void setCurrentPlayer(String name, boolean block) {
                GameForm.this.setCurrentPlayer(name,block);
            }
            @Override
            public void allPlayersDisconnect()
            {
                GameForm.this.allPlayersDisconnect();
            }
            @Override
            public void setWinner(int[] score)
            {
                GameForm.this.setWinner(score);
            }
        });
        //слушатель сетевых событий
        EventProducer.addListener(new NetworkEventAdapter(){
            @Override
            public void disconnectPlayer(String name)
            {
                GameForm.this.disconnectPlayer(name);
            }
            @Override
            //статус соединения с сервером
            public void setConnectPlayerStatus(boolean f)
            {
                if(!isGame) return; //если игра не идет, то выходим
                if(!f)
                {
                    JOptionPane.showMessageDialog(null, "Потеряно соединение с сервером",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Потеряно соединение с сервером");

                    GameForm.this.close();
                }
            }
            @Override
            public void updateClientField(ArrayList<Point> updatePoints, int player, int[] score)
            {
                GameForm.this.updateField(updatePoints,player,score);
            }
        });
    }
    //закрытие окна
    public void close()
    {
        this.hide(); //закрываем это окно
        GameForm.this.parent.show(); //открываем родительское
    }
    //инициализация динамической части интерфейса окна
    public void initForm(String[] playernames, boolean isServer)
    {
        isGame=true; //выставляем флаг активности игры
        restartButton.setEnabled(isServer); //если игрок сервер, то разблокируем кнопку перезапуска
        //заполняем список имен игроков
        this.playernames=new ArrayList<>();
        for(String str:playernames)
            this.playernames.add(new String(str));

        if(scoreLabels!=null) //очищаем старый список лейблов баллов игроков
        {
            for(JLabel score:scoreLabels) buttonPanel.remove(score);
            scoreLabels.clear();
        }
        //создаем список лейблов баллов игроков
        scoreLabels=new ArrayList<>();
        for(int i=0;i<this.playernames.size();i++)
        {
            scoreLabels.add(new JLabel(this.playernames.get(i)+": 0"));
            scoreLabels.get(i).setForeground(options.getPlayerColor(i+1));
            buttonPanel.add(scoreLabels.get(i));
        }

        gamePanel.removeAll(); //очищяем игровую панель
        gamePanel.setLayout(new GridLayout(options.getWidth(),options.getHeight()));

        field=new Cell[options.getWidth()][options.getHeight()]; //создаем поле
        //заполняем поле
        for(int i=0;i<field.length;i++)
            for (int j=0;j<field.length;j++)
            {
                field[i][j]=new Cell(i,j);
                gamePanel.add(field[i][j]);
            }

        restart(); //очищаем поле
    }
    //создание статического интерфейса (не меняется при перезапуске)
    private void createGUI()
    {
        setLayout(new BorderLayout());
        gamePanel=new JPanel(new GridLayout(options.getWidth(),options.getHeight()),true);
        menuPanel=new JPanel(new GridLayout(1,1));

        disconnectButton=new JButton("Покинуть игру");
        statusLabel=new JLabel("Начало игры");
        statusLabel.setFont(new Font("Times New Roman",Font.PLAIN,36));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        menuPanel.add(statusLabel);
        buttonPanel= new JPanel(new GridLayout(3,2));

        buttonPanel.add(disconnectButton);
        restartButton=new JButton("Перезапуск");
        buttonPanel.add(restartButton);

        add(menuPanel,BorderLayout.NORTH);
        add(buttonPanel,BorderLayout.PAGE_END);
        add(gamePanel,BorderLayout.CENTER);

        //события для кнопок
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventProducer.fireDisconnectPlayer();
                GameForm.this.close();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventProducer.fireSignalStartGame();
            }
        });
    }

    public void restart() //перезапуск игры
    {
        setFieldEnabled(false); //блокируем поле
        for(int i=0;i<field.length;i++)
            for(int j=0;j<field.length;j++)
                field[i][j].setTag(0); //устанавливаем тег пустой клетки
        //сбрасываем баллы
        for(int i=0;i<scoreLabels.size();i++)
            scoreLabels.get(i).setText(playernames.get(i)+": 0");
    }
    //блокировка и разблокировка игрового поля
    private void setFieldEnabled(boolean b)
    {
        for(int i=0;i<field.length;i++)
            for(int j=0;j<field.length;j++)
                if(field[i][j].getTag()==0) field[i][j].setEnabled(b);
    }
    //конец игры, вывод результатов
    public void setWinner(int[] score) {

        statusLabel.setText("Игра завершена, свободных клеток нет");
        statusLabel.setForeground(Color.BLACK);
        setFieldEnabled(false);
        //создаем и выводим окно результатов игры
        WinnerDialog wd = new WinnerDialog(this,playernames,score);
        wd.show();
    }
    //обновление игрового поля
    private void updateField(ArrayList<Point> updatePoints, int player,int[] score) //установка метки игрока на поле (вызывается событием контроллера)
    {
        for(Point point:updatePoints) //обновление поля
            field[point.x][point.y].setTag(options.getPlayerTag(player));

        for(int i=0;i<score.length;i++) //обновление баллов
            scoreLabels.get(i).setText(playernames.get(i)+": "+score[i]);
    }
    //отключение игрока
    private void disconnectPlayer(String name)
    {
        if(!isGame) return;
        int player=playernames.indexOf(name);
        if(player>=scoreLabels.size() || player<0) return;
        //удаляем лейбл отключившегося игрока
        int id=playernames.indexOf(name);
        if(id<playernames.size()) {
            buttonPanel.remove(scoreLabels.get(id));
        }

        JOptionPane.showMessageDialog(null, "Игрок "+playernames.get(player)+" отключился",
                "Уведомление", JOptionPane.ERROR_MESSAGE);

        this.repaint(); //переррисовка интерфейса
    }
    //отключились все игроки
    private void allPlayersDisconnect()
    {
        if(!isGame) return;
        JOptionPane.showMessageDialog(null, "Все игроки отсоединились :( Но можно считать что вы победитель :)",
                "GameOver", JOptionPane.ERROR_MESSAGE);
        this.close(); //закрываем окно
    }
    //установка текущего игрока
    private void setCurrentPlayer(String name,boolean block) //установка текузего игрока (вызывается событием контроллера)
    {
        int id=playernames.indexOf(name);
        if(id<0) return;
        statusLabel.setText("Ходит "+name); //выставляем статусную строку
        statusLabel.setForeground(options.getPlayerColor(id+1)); //устанавливаем цвет статусной строки
        setFieldEnabled(block); //блокируем или разблокируем поле
    }
}
