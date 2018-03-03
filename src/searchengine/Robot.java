/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.runtime.Context.DEBUG;

/**
 *
 * @author ahmos
 */
public class Robot {
    public boolean robotSafe(URL url) {
    String strHost = url.getHost();

	// form URL of the robots.txt file
    String strRobot = "http://" + strHost + "/robots.txt";
    URL urlRobot;
    try { urlRobot = new URL(strRobot);
	} catch (MalformedURLException e) {
	    // something weird is happening, so don't trust it
	    return false;
	}

    if (DEBUG) System.out.println("Checking robot protocol " + 
                                   urlRobot.toString());
    String strCommands;
    try {
       InputStream urlRobotStream = urlRobot.openStream();

	    // read in entire file
       byte b[] = new byte[1000];
       int numRead = urlRobotStream.read(b);
       strCommands = new String(b, 0, numRead);
       while (numRead != -1) {
          numRead = urlRobotStream.read(b);
          if (numRead != -1) {
             String newCommands = new String(b, 0, numRead);
	         strCommands += newCommands;
		}
	    }
       urlRobotStream.close();
	} catch (IOException e) {
	    // if there is no robots.txt file, it is OK to search
	    return true;
	}
        if (DEBUG) System.out.println(strCommands);

	// assume that this robots.txt refers to us and 
	// search for "Disallow:" commands.
	String strURL = url.getFile();
	int index = 0;
	while ((index = strCommands.indexOf("Disallow:", index)) != -1) {
	    index += "Disallow:".length();
	    String strPath = strCommands.substring(index);
	    StringTokenizer st = new StringTokenizer(strPath);

	    if (!st.hasMoreTokens())
		break;
	    
	    String strBadPath = st.nextToken();

	    // if the URL starts with a disallowed path, it is not safe
	    if (strURL.indexOf(strBadPath) == 0)
		return false;
	}

	return true;
    }
    public boolean isSafeUrl(String urlLink, String userAgent,Carawler carawler)
    {
        try {
            URL url =new URL(urlLink);
            String host = url.getHost().toLowerCase();
            ArrayList disAllowList = (ArrayList)carawler.getRobotTxtFiles().get(host);
            if(disAllowList == null)
            {
                disAllowList = new ArrayList();
            try{
            URL robotsFileurl = new URL("http://" + host + "/robots.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(robotsFileurl.openStream()));
            String line;
            while((line= reader.readLine())!= null){
            if(line.indexOf("Disallow:")==0)
            {
            String disallowPath = line.substring("Disallow:".length());
            int commentIndex = disallowPath.indexOf("#");
            if(commentIndex != -1)
            {
            disallowPath = disallowPath.substring(0,commentIndex);
            }
            disallowPath = disallowPath.trim();
            disAllowList.add(disallowPath);
            }
            }
            carawler.getRobotTxtFiles().put(host, disAllowList);
            }
            catch(Exception e)
            {
            return true ;
            }
            }
            String file = url.getFile();
            for (int i = 0; i < disAllowList.size(); i++) {
                String disallow = (String) disAllowList.get(i);
                        if (file.startsWith(disallow)) {
                                        return false;
                                                            }
            
        }
        }catch (MalformedURLException ex) {
        //    Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        return false ;
        }
    return true;
    }
}

