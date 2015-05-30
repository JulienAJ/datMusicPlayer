package alonsojimenez.julien.datmusicplayer;

import alonsojimenez.julien.datmusicplayer.voiceRecognition.IVoiceRecognizer;

/**
 * Created by julien on 16/05/15.
 */
public class Settings
{
    private static IVoiceRecognizer voiceRecognizer = null;

    public static void setVoiceRecognizer(IVoiceRecognizer newVoiceReco)
    {
        voiceRecognizer = newVoiceReco;
    }

    public static IVoiceRecognizer getVoiceRecognizer()
    {
        return voiceRecognizer;
    }

    public static Class getVoiceRecognizerType()
    {
        return voiceRecognizer.getClass();
    }
}
