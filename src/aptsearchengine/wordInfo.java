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
    int Freq, TotalNumberOfWords;
    double TF;
    ArrayList<String> IMG_URL;
    HashMap<Integer, String> Position;
    
    public wordInfo(int iTotalNumberOfWords){
        Freq = 0;
        TF = 0.0;
        TotalNumberOfWords = iTotalNumberOfWords;     
        IMG_URL = new ArrayList<String>();
        Position = new HashMap<Integer, String>();
    }
        
    public void addImg(String iIMG)
    {
        IMG_URL.add(iIMG);
    }
    
    public void addPosition(int iPosition, String iText)        //iText is the 2 words before the word + word itself + 2 words after the word
    {
        Position.put(iPosition, iText);
        Freq++;
        TF = (double)Freq / (double)TotalNumberOfWords;
    }
    
    public int getFreq() { return Freq; }
    public double getTF() { return TF; }
    public ArrayList<String> getImgURL() { return IMG_URL; }
    public HashMap<Integer, String> getPosition() { return Position; }    
}
