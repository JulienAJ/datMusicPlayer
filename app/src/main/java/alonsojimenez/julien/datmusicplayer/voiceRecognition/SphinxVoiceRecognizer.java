package alonsojimenez.julien.datmusicplayer.voiceRecognition;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import Ice.Communicator;
import PocketSphinxIce.IPocketSphinxServerPrx;
import PocketSphinxIce.IPocketSphinxServerPrxHelper;

/**
 * Created by julien on 30/04/15.
 */
// TODO correct issues with pocketSphinx wrapper
public class SphinxVoiceRecognizer implements IVoiceRecognizer
{
    private static final int REC_SR = 16000;
    private static final int REC_CHAN = AudioFormat.CHANNEL_IN_MONO;
    private static final int REC_ENC = AudioFormat.ENCODING_PCM_16BIT;
    private static final String hostname = "server.datdroplet.ovh";
    private static final String port = "20000";

    private AudioRecord audioRecord = null;

    private Communicator communicator = null;
    private IPocketSphinxServerPrx server;

    private int bufferSize;
    private short[] recordData;
    private int position;

    IRecognitionResultListener resultListener;

    @Override
    public void init(Context context)
    {
        initCommunicator();
        initServer();

        bufferSize = AudioRecord.getMinBufferSize(REC_SR, REC_CHAN, REC_ENC);
        recordData = new short[50*bufferSize];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                REC_SR, REC_CHAN,
                REC_ENC, bufferSize);
    }

    @Override
    public void start()
    {
        position = 0;
        audioRecord.startRecording();
        new Thread(new Runnable()
        {
            public void run() {
                while (position < bufferSize * 50 && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    int temp = audioRecord.read(recordData, position, bufferSize);
                    position += temp;
                }
            }
        }).start();
    }

    @Override
    public void stop()
    {
        audioRecord.stop();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                String result = null;
                try
                {
                    result = server.decode(recordData);
                    resultListener.onRecognitionResult(result);
                }
                catch(PocketSphinxIce.Error e)
                {
                    Log.e("PocketSphinxError", e.what);
                }
                if(result != null)
                {
                    Log.e("PocketSphinxResult", result);
                }
            }
        }).start();

    }

    @Override
    public void setRecognitionResultListener(IRecognitionResultListener listener)
    {
        this.resultListener = listener;
    }

    private void initCommunicator()
    {
        if(communicator == null)
        {
            communicator = Ice.Util.initialize();
        }
    }

    private void initServer()
    {
        if(communicator == null)
            return;
        try
        {
            Ice.ObjectPrx base = communicator.stringToProxy("PocketSphinxServer:default -h " + hostname +
                    " -p " + port);
            server = IPocketSphinxServerPrxHelper.checkedCast(base);
        }
        catch(Exception e)
        {
            Log.e("InitSphinxException", e.getMessage());
        }

    }
}