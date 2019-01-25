package com.object173.pointsgame.events;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ярослав on 30.12.2016.
 */
//адаптер локальных событий, чтобы не обязательно было все методы реализовывать
public abstract class EventAdapter implements EventListener {
    //от представления
    @Override
    public void playerRequestMove(int x, int y) {} //ход игрока
    @Override
    public void setOptions(int width, int height,int maxPlayers, int singlePlayer) {}

    //от модели
    @Override
    public void updateField(ArrayList<Point> updatePoints, int player,int[] score) {}

    //от контроллера
    @Override
    public void setCurrentPlayer(String name,boolean block) {} //установка текущего игрока
    @Override
    public void setWinner(int[] score) {} //вывод победителя
    @Override
    public void allPlayersDisconnect() {}
}
