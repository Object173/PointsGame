package com.object173.pointsgame.controller;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventListener;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.events.NetworkEventAdapter;
import com.object173.pointsgame.events.NetworkEventListener;
import com.object173.pointsgame.messages.FactoryMessage;
import com.object173.pointsgame.messages.Message;
import com.object173.pointsgame.messages.MessagePoint;
import com.object173.pointsgame.messages.MessageUpdate;
import com.object173.pointsgame.model.GameField;
import com.sun.corba.se.spi.activation.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ярослав on 02.10.2016.
 */

//контроллер сервера игры
public class ServerController implements Runnable{

    private GameField gameField; //экземпляр игрового поля

    private Thread thread; //рабочий поток для сервера
    private boolean alive=true,enabled=true; //флаги активности (работы потока) и доступности (для подключения) сервера

    private ServerSocket server; //серверный сокет
    private ArrayList<Player> players; //коллекция игроков

    private int actualID=0; //счетчик id игроков, для избежания опвторений

    private int currentPlayer=0; //номер текущего игрока (который ходит)

    private Options options=Options.getInstance(); //ссылка на настройки

    private NetworkEventListener networkEventListener; //слушатель событий
    //конструктор, получает имя игрока - сервера
    public ServerController(String playername)
    {
        players = new ArrayList<>(); //инициализация коллекции игроков

        players.add(new LocalPlayer(actualID,playername)); //создаем локального игрока
        actualID++; //инкрементируем счетчик id
        EventProducer.fireAddPlayer(playername,actualID-1); //вызываем событие добавления игрока
        //создаем слушаетля событий
        networkEventListener=new NetworkEventAdapter(){
            @Override
            public void removePlayer(Player player) //событие отключение игрока
            {
                disconPlayer(player);
            }

            @Override
            public void messageToServer(Message message) //получение сообщения от локального игрока
            {
                ServerController.this.checkMessage(message);
            }
        };
        EventProducer.addListener(networkEventListener); //подключаем слушателя
    }
    //игрок по его номеру
    public Player getPlayer(int id)
    {
        return players.get(id);
    }
    //подключение сервера (получает номер порта)
    public boolean connect(int port)
    {
        try
        {
            server=new ServerSocket(port); //открываем соединение
        }
        catch(Exception ex)
        {
            System.out.println("Ошибка подключения к порту");
            return false;
        }

        thread=new Thread(this); //создаем рабочий поток
        thread.setDaemon(true); //поток демон, чтобы завершался при завершении основного
        thread.start(); //запускаем поток

        enabled=true; //делаем сервер доступным
        return true;
    }
    //отключение игрока
    private void disconPlayer(Player player)
    {
        int id=getPlayerNumber(player.id); //сохранем id удаляемого игрока
        //если ход удаляемого игрока
        boolean cur = (currentPlayer<players.size() && player.getId()==players.get(currentPlayer).getId());
        //создаем сообщение на удаление игрока
        Message mes = FactoryMessage.createMessage(Options.MESSAGE_DISCONNECT,player.getName(),id);
        players.remove(player); //удаляем игрока из коллекции
        sendMessageAll(mes); //рассылаем сообщение всем игрокам
        //если игроков осталось меньше минимума, а игра начата
        if(players.size()< options.getMIN_PLAYERS() && gameField!=null)
        {
            EventProducer.fireAllPlayersDisconnect(); //вызываем событие, что все игроки отключены
            disconnect(); //отключаем сервер
        }
        //если идети игра, то удаляем с игрового поля все метки игрока
        if(gameField!=null) gameField.removePlayer(id);
        currentPlayer--; //сдвигаем назад номер игрока который ходит (чтобы ход не перешел через один, так как коллеция сдвинулась)
        //если был ход удаленного игрока
        if(cur) nextMove(); //передаем ход дальше
    }
    //получить список имен игроков
    private String[] getPlayerNames()
    {
        String[] names = new String[players.size()]; //создаем массив имен игроков
        //обходим всех игроков и доаляем их имена
        for(int i=0;i<players.size();i++) names[i]=new String(players.get(i).getName());
        return names;
    }
    //получить номер игрока в коллекции по его id
    private int getPlayerNumber(int id)
    {
        for(int i=0;i<players.size();i++) //обходим коллецию
            if(players.get(i).getId()==id) return i; //если id игрока равен искомогу, возвращаем его номер
        return -1; //иначе -1
    }
    //обработка полученного сообщения
    private void checkMessage(Message message)
    {
        if(message instanceof MessagePoint) //если получено сообщение с точкой хода игрока
        {
            MessagePoint point = (MessagePoint)message; //приводим тип сообщения
            if(point.getId()==players.get(currentPlayer).getId()) //проверяем что сообщение от игрока который сейчас ходит
                //проверяем успешно ли сделан ход
                if(gameField.putPoint(point.getX(),point.getY(),players.get(getPlayerNumber(point.getId())).getTag()))
                {
                    if(gameField.isFill()) //если игровое поле заполнилось
                        sendMessageAll(FactoryMessage.createMessage(gameField.getScore())); //отправляем всем сообщение о конце игры
                    else nextMove(); //иначе передаем ход дальше
                }
                else //если нет, то заного отправляем сообщение с текущим игроком (возвращаем ход)
                {
                    sendMessageAll(FactoryMessage.createMessage(Options.MESSAGE_MOVE,players.get(currentPlayer).getName(),
                            currentPlayer));
                }
        }
        if(message instanceof MessageUpdate) //если сообщение обновления поля
        {
            sendMessageAll(message); //рассылаем его всем
        }
    }
    //рассылка сообщения всем игрокам
    private void sendMessageAll(Message mes)
    {
        for(int i=0;i<players.size();i++)
            players.get(i).sendMessage(mes);
    }
    //старт игры
    public void startGame()
    {   //создаем игровое поле (модель)
        gameField=new GameField(options.getWidth(),options.getHeight(),players.size());
        enabled=false; //сбрасываем флаг доступности сервера (чтобы не поключались во время игры)
        for(int n=0;n<players.size();n++) players.get(n).setTag(n);
        //рассылаем всем сообщение о начале игры
        sendMessageAll(FactoryMessage.createMessage(options.getWidth(),options.getHeight(),getPlayerNames()));
        currentPlayer=Options.getInstance().getSinglePlayer()-1; //устанавливаем текущего игрока
        nextMove(); //передаем ход
    }
    //передаем ход следующему игроку
    public void nextMove()
    {
        currentPlayer++; //сдвигаем номер текущего игрока
        if(currentPlayer>=players.size()) currentPlayer=0; //если номер вышел за пределы, сбрасываем
        //рассылаем сообщение с текущим игроком
        sendMessageAll(FactoryMessage.createMessage(Options.MESSAGE_MOVE,players.get(currentPlayer).getName(),
                currentPlayer));
    }
    //отключение сервера
    public void disconnect()
    {
        for(Player player:players) player.disconnect(); //отключаем всех игроков
        close(); //закрываем соединение
    }
    //закрыть соединение
    private void close()
    {
        try {
            alive=false; //сбрасываем флаг активности сервера (завершаем поток)
            EventProducer.removeListener(networkEventListener); //удаляем слушателя событий
            server.close(); //закрываем сокет
        }
        catch(Exception ex) {System.out.println("close "+ex.toString());}
    }
    //основной метод работы потока
    public void run()
    {
        try
        {
            System.out.println("сервер запущен");
            while(alive) //пока выставлен флаг активности
            {
                Socket ss=server.accept(); //получаем сокет нового игрока
                //если выставлен флаг доступности сервера и количество игроков меньше максимума
                if(enabled && players.size()< Options.getInstance().getCountPlayer())
                {
                    Player player=new ServerReceiver(actualID++,ss); //создаем поток обработки нового игрока
                    players.add(player); //добавляем игрока в коллекцию
                }
            }
        }
        catch(Exception ex) {System.out.println("Run stop "+ex);}
        EventProducer.fireSetServerEnabled(false); //вызов события не активности сервера
        close(); //закрываем соединение
    }
}
