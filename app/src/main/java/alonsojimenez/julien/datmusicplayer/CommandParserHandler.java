package alonsojimenez.julien.datmusicplayer;

import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;

import java.net.Proxy;

import Messages.Command;

/**
 * Created by julien on 05/05/15.
 */
public class CommandParserHandler
{
    public static Command parse(String text)
    {
        String namespace = "http://parserServicer.commandParser/ParserService";
        String method = "parse";
        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("request", text);
        SoapSerializationEnvelope envelope = getEnvelope(request);

        String soapAction = "\"" + namespace + method + "\"";
        HttpTransportSE ht = getHttp();
        try
        {
            ht.call(soapAction, envelope);
            SoapObject resultString = (SoapObject)envelope.getResponse();
            String result = resultString.toString();
            Command command = new Command(result);

            return command;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private final static SoapSerializationEnvelope getEnvelope(SoapObject request)
    {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private final static HttpTransportSE getHttp()
    {
        String requestURL = "http://server.datdroplet.ovh:8080/CommandParser/ParserService";
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,requestURL,60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }
}
