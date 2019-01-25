package com.object173.pointsgame;

import java.awt.*;

/**
 * Created by Ярослав on 12.01.2017.
 */

//синглтон с настройкими игры
public class Options {
    private static Options ourInstance = new Options(); //единственный экземпляр
    public static Options getInstance() {
        return ourInstance;
    } //даем ссылку на ед экземпляр
    //приватный конструктор, нельзя создать объект класса
    private Options() {
    }
    //константы, сообщения
    public static final String MESSAGE_START="START_GAME";
    public static final String MESSAGE_RESTART="RESTART_GAME";
    public static final String MESSAGE_DISCONNECT="DISCONNECT_PLAYER";
    public static final String MESSAGE_MOVE="MOVE_PLAYER";
    public static final String MESSAGE_UPDATE="UPDATE_FIELD";
    public static final String MESSAGE_WINNER="WINNER";
    public static final String MESSAGE_SETTAG="SET_TAG";
    //константы ограничений настроек
    private final int MIN_PLAYERS=2;
    private final int MAX_PLAYERS=4;
    private final int MIN_SIZE=5;
    private final int MAX_SIZE=100;
    //гетеры ограничений
    public int getMIN_SIZE() {
        return MIN_SIZE;
    }
    public int getMAX_SIZE() {
        return MAX_SIZE;
    }
    public int getMIN_PLAYERS() {return MIN_PLAYERS;}
    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }
    //файл с правилами
    public static String pathHelp="src/resources/help.txt";
    //цвета и метки игроков
    private final Color[] playerColor = {Color.WHITE, Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW};
    private final int[] playerTag = {1,2,3,4};
    //получить метку игрока по номеру
    public int getPlayerTag(int n) {
        if(n>=playerTag.length) return 0;
        if(n<0) return 0;
        return playerTag[n];
    }
    //получить номер игрока по метке
    public int getPlayerForTag(int tag) {
        for(int i=0;i<playerTag.length;i++) if(playerTag[i]==tag) return i;
        return -1;
    }
    //текущие настройки игры
    private int width=10, height=10; //рамеры поля
    private int countPlayer = 3; //максимальное количество игроков на сервере
    private int singlePlayer = 0; //номер игрока, который будем ходить первым
    //гетеры и сетеры настроек
    public int getWidth() {
        return width;
    }

    public boolean setWidth(int width) {
        if(width<getMIN_SIZE() || width>getMAX_SIZE()) return false;
        this.width = width;
        return true;
    }

    public int getHeight() {
        return height;
    }

    public boolean setHeight(int height) {
        if(height<getMIN_SIZE() || height>getMAX_SIZE()) return false;
        this.height = height;
        return true;
    }

    public int getSinglePlayer() {
        return singlePlayer;
    }

    public boolean setSinglePlayer(int singlePlayer) {
        if(singlePlayer<1 || singlePlayer>countPlayer) return false;
        this.singlePlayer = singlePlayer;
        return true;
    }

    public int getCountPlayer() {
        return countPlayer;
    }

    public boolean setCountPlayer(int countPlayer) {
        if(countPlayer<2 || countPlayer>MAX_PLAYERS) return false;
        this.countPlayer = countPlayer;
        return true;
    }
    //получить цвет игрока
    public Color getPlayerColor(int n) {
        return playerColor[n];
    }
}
