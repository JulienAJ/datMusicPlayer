/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alonsojimenez.julien.datmusicplayer.commandParser;

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

    public Command(String soapResponse)
    {
        String regex = "^anyType\\{action=(ADD|REMOVE|SEARCH|PLAY); ((artist=)([^;]+); )?((title=)(.+); )?\\}$";
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

    public Action getAction() { return action; }

    public String getArtist() { return artist; }

    public String getTitle() { return title; }

    @Override
    public String toString()
    {
        return "Command{" + "action=" + action + ", artist='" + artist + '\''
                + ", title='" + title + '\'' + '}';
    }
}
