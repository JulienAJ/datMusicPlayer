package alonsojimenez.julien.datmusicplayer.voiceRecognition;

import android.content.Context;


/**
 * Created by julien on 30/04/15.
 */
public interface IVoiceRecognizer
{
    // Initialize the Voice Recognizer
    // Context is given so implementations using SpeechRecognizer or other classes needing context
    // can be used
    public void init(Context context);

    // Start the recording
    public void start();

    // Stop the recording
    public void stop();

    // Set the class called when recognition is over (See IRecognitionResultListener)
    public void setRecognitionResultListener(IRecognitionResultListener listener);
}
