package com.object173.pointsgame.messages;

import com.object173.pointsgame.Options;

import java.lang.management.OperatingSystemMXBean;

/**
 * Created by Ярослав on 14.01.2017.
 */

//сообщение конца игры (массив баллов игроков)
public class MessageWinner extends Message{
    private int[] score;

    public int[] getScore() {
        int[] nscore=new int[score.length];
        for(int i=0;i<score.length;i++) nscore[i]=score[i];
        return nscore;
    }

    public MessageWinner(int[] score)
    {
        super(Options.MESSAGE_WINNER);
        this.score=new int[score.length];
        for(int i=0;i<score.length;i++) this.score[i]=score[i];
    }
}
