/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author julien
 */
public class Command
{
    private Action action;
    private String artist;
    private String title;

    public Command(Action action, String artist, String title)
    {
        this.action = action;
        this.artist = artist;
        this.title = title;
    }

    public Command(String soapResponse)
    {
        String regex = "^anyType\\{action=(ADD|REMOVE|SEARCH|PLAY); ((artist=)(.+); )?((title=)(.+); )?\\}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(soapResponse);
        if(matcher.find())
        {
            this.action = actionFromString(matcher.group(1));

            if(matcher.group(4) != null)
                this.artist = matcher.group(4);

            this.title = matcher.group(7);
        }
    }

    public static Action actionFromString(String actionString)
    {
        if(actionString.equals("ADD"))
            return Action.ADD;

        if(actionString.equals("REMOVE"))
            return Action.REMOVE;

        if(actionString.equals("PLAY"))
            return Action.PLAY;

        if(actionString.equals("SEARCH"))
            return Action.SEARCH;

        return null;
    }

    public static String StringFromAction(Action action)
    {
        if(action == Action.ADD)
            return "ADD";

        if(action == Action.REMOVE)
            return "REMOVE";

        if(action == Action.PLAY)
            return "PLAY";

        if(action == Action.SEARCH)
            return "SEARCH";

        return null;
    }

    public void setAction(Action action) { this.action = action; }

    public void setArtist(String artist) { this.artist = artist; }

    public void setTitle(String title) { this.title = title; }

    public Action getAction() { return action; }

    public String getArtist() { return artist; }

    public String getTitle() { return title; }

    @Override
    public String toString()
    {
        return "Command{" +
                "action=" + action +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
