package alonsojimenez.julien.datmusicplayer.voiceRecognition;

/**
 * Created by julien on 09/05/15.
 */
public interface IRecognitionResultListener
{
    // Method called when the IVoiceRecognizer class finished treating the signal
    // result is the result of that treatment
    public void onRecognitionResult(String result);
}
