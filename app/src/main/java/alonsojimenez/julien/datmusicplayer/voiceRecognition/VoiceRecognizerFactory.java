package alonsojimenez.julien.datmusicplayer.voiceRecognition;

/**
 * Created by julien on 16/05/15.
 */
public class VoiceRecognizerFactory
{
    public static IVoiceRecognizer create(Class type)
    {
        if(type == AndroidVoiceRecognizer.class)
            return new AndroidVoiceRecognizer();

        else
            return new SphinxVoiceRecognizer();
    }
}
