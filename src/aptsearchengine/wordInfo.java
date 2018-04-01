/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aptsearchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 *
 * @author Ahmed
 */
public class wordInfo {
    private int Freq, TotalNumberOfWords;
    private double TF;
    private HashMap <Integer, String> Position_AroundWordSentence;
    
    public wordInfo(int iTotalNumberOfWords){
        Freq = 0;
        TF = 0.0;
        TotalNumberOfWords = iTotalNumberOfWords;   
        Position_AroundWordSentence = new HashMap<>();
    }
    
    public void addPosition(int iPosition, String aroundWordSentence)   
    {
        Position_AroundWordSentence.put(iPosition, aroundWordSentence);
        Freq++;
        TF = (double)Freq / (double)TotalNumberOfWords;
    }
    
    public int getFreq() { return Freq; }
    public double getTF() { return TF; }
    public HashMap<Integer, String> getPosition() { return Position_AroundWordSentence; }    
}
