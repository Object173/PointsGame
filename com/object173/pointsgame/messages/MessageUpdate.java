package com.object173.pointsgame.messages;

import com.object173.pointsgame.Options;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 13.01.2017.
 */

//сообщение обновления поля (список измененных клеток, метка игрока, массив баллов игроков)
public class MessageUpdate extends Message {
    private ArrayList<Point> updatePoints;
    private int[] updateScore;
    private int player;

    public ArrayList<Point> getUpdatePoints() {
        return updatePoints;
    }

    public int[] getUpdateScore() {
        return updateScore;
    }

    public int getPlayer() {
        return player;
    }

    public MessageUpdate(ArrayList<Point> points, int player, int[] score) {
        super(Options.getInstance().MESSAGE_UPDATE);

        updatePoints=new ArrayList<>();
        for (Point point:points) {
            updatePoints.add(new Point(point));
        }

        this.player=player;

        updateScore=new int[score.length];
        for(int i=0;i<score.length;i++)
            updateScore[i]=score[i];
    }
}
