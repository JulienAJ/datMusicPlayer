package alonsojimenez.julien.datmusicplayer.voiceRecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by julien on 30/04/15.
 */
public class AndroidVoiceRecognizer implements IVoiceRecognizer
{
    SpeechRecognizer speechRecognizer;
    Context context;
    String result;
    IRecognitionResultListener resultListener;
    boolean done;


    @Override
    public void init(Context context)
    {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        this.context = context;
        speechRecognizer.setRecognitionListener(new recognitionListener());
    }

    @Override
    public void start()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer.startListening(intent);
    }

    @Override
    public void stop()
    {
        speechRecognizer.stopListening();
        done = false;
    }

    @Override
    public void setRecognitionResultListener(IRecognitionResultListener listener)
    {
        this.resultListener = listener;
    }

    private class recognitionListener implements RecognitionListener
    {
        @Override
        public void onReadyForSpeech(Bundle params)
        {}

        @Override
        public void onBeginningOfSpeech()
        {}

        @Override
        public void onRmsChanged(float rmsdB)
        {}

        @Override
        public void onBufferReceived(byte[] buffer)
        {}

        @Override
        public void onEndOfSpeech()
        {}

        @Override
        public void onError(int error)
        {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Other client side errors.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Other network related errors.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network operation timed out.";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No recognition result matched.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "Server sends error status.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Error";
            }
            Log.e("SpeechRecognizer Error", message);
            Toast.makeText(context, message, Toast.LENGTH_LONG);
        }

        @Override
        public void onResults(Bundle results)
        {
            String s = "";
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < data.size(); i++)
            {
                s += data.get(i);
            }
            Log.e("Speech Reco Output", data.get(0).toString());
            result = data.get(0).toString();
            resultListener.onRecognitionResult(result);
        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {}

        @Override
        public void onEvent(int eventType, Bundle params)
        {}

    }
}
