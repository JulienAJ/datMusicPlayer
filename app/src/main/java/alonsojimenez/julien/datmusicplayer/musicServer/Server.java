package alonsojimenez.julien.datmusicplayer.musicServer;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import Ice.Communicator;
import Ice.Identity;
import Ice.InitializationData;
import Ice.LocalException;
import Ice.ObjectPrx;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicManagerPrxHelper;
import IceStorm.TopicPrx;
import Player.Monitor;
import Player.ServerPrx;

/**
 * Created by julien on 08/05/15.
 */
public class Server
{
    private String hostname;
    private String port;
    private String streamingPort;
    private Communicator communicator = null;
    private ServerPrx serverPrx = null;
    private Glacier2.RouterPrx router;
    private static ObjectPrx monitorProxy;
    private static TopicPrx topic;
    private int messageSizeMax = 800000;

    public Server(String hostname, String port)
    {
        this.hostname = hostname;
        this.port = port;
        this.streamingPort = null;
    }

    public void initCommunicator(String glacierHostname, String glacierPort)
    {
        try
        {
            if(communicator == null)
            {
                InitializationData initializationData = new InitializationData();
                initializationData.properties = Ice.Util.createProperties();
                initializationData.properties.setProperty("Ice.Default.Router",
                        "Glacier2/router:tcp -h " + glacierHostname + " -p " + glacierPort);
                initializationData.properties.setProperty("Ice.ACM.Client", "0");
                initializationData.properties.setProperty("Ice.RetryIntervals", "-1");
                initializationData.properties.setProperty("CallbackAdapter.Router",
                        "Glacier2/router:tcp -h " + glacierHostname + " -p " + glacierPort);
                communicator = Ice.Util.initialize(initializationData);
            }

        }
        catch(Exception e)
        {
            Log.e("Ice Communicator", e.getMessage());
        }

    }

    public void initRouter(String user, String password)
    {
        try
        {
            Ice.RouterPrx defaultRouter = communicator.getDefaultRouter();
            router = Glacier2.RouterPrxHelper.checkedCast(defaultRouter);
            router.createSession(user, password);
        }
        catch(Exception e)
        {
            Log.e("Glacier Router", e.getMessage());
        }
    }

    public boolean ping()
    {
        try
        {
            if (serverPrx != null)
            {
                serverPrx.ice_ping();
                return true;
            }
        }
        catch (LocalException e)
        {
            Log.e("Server Ping", "Server Offline");
        }
        return false;
    }

    public void destroy()
    {
        if (communicator == null)
            return;
        try
        {
            communicator.destroy();
        }
        catch(Exception e)
        {
            Log.e("Ice Communicator Destroy", e.getMessage());
        }
    }

    public void setUpStorm(String stormHostname, String stormPort, Monitor monitor)
    {
        try
        {
            ObjectPrx objectPrx = communicator.stringToProxy("IceStorm/TopicManager:tcp -h " + stormHostname + " -p " + stormPort);
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(objectPrx);
            Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithRouter("MonitorAdapter", router);
            monitorProxy = adapter.add(monitor, new Identity("default", router.getCategoryForClient())).ice_twoway();
            adapter.activate();

            topic = topicManager.retrieve("DatMusic");
            Map<String, String> qos = new HashMap<>();
            topic.subscribeAndGetPublisher(qos, monitorProxy);

            Log.i("IceStorm", "Topic active");
            ObjectPrx publisher = topic.getPublisher().ice_twoway();
        }
        catch(Exception e)
        {
            Log.e("IceStorm", e.getMessage());
        }

    }

    public void init()
    {
        try
        {
            if(communicator == null)
                return;

            if(this.serverPrx == null)
            {
                Ice.ObjectPrx base = communicator.stringToProxy("Server:default -h " + this.hostname +
                        " -p " + this.port);
                this.serverPrx = Player.ServerPrxHelper.checkedCast(base);
                setupServerSettings();
                Log.e("Initialize", "done");
            }
        }
        catch(Exception e)
        {
            Log.e("Ice Server", e.getMessage());
        }
    }

    public String getHostname()
    {
        return hostname;
    }

    public String getPort()
    {
        return port;
    }

    public String getStreamingPort()
    {
        return streamingPort;
    }

    public void setupServerSettings()
    {
        this.streamingPort = serverPrx.getStreamingPort();
    }

    public ServerPrx getServerPrx()
    {
        return serverPrx;
    }

    public int getMessageSizeMax()
    {
        return messageSizeMax;
    }

    public String getIdentifier()
    {
        return hostname + ':' + port;
    }

    public int getCount()
    {
        if(serverPrx == null)
            return -1;

        return serverPrx.getCount();
    }

    public static String identifierFromHostPort(String servHost, String servPort)
    {
        return servHost + ":" + servPort;
    }
}