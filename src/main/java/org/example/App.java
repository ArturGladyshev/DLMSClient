package org.example;


import client.GXDLMSReader;
import client.GXDLMSSecureClient2;
import gurux.common.IGXMedia;
import gurux.common.enums.TraceLevel;
import gurux.dlms.enums.*;
import gurux.dlms.objects.*;
import gurux.net.GXNet;
import gurux.net.enums.NetworkType;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.util.Date;


public class App
{

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main( String[] args ) throws XMLStreamException {
        IGXMedia media = new GXNet(NetworkType.TCP, "localhost", 4059);

        GXDLMSSecureClient2 client = new GXDLMSSecureClient2(true);
        client.setInterfaceType(InterfaceType.HDLC);
        client.setAutoIncreaseInvokeID(true);
        client.getCiphering().setSigning(Signing.NONE);
        client.setStandard(Standard.DLMS);

        //None association
        client.setAuthentication(Authentication.NONE);
        client.setPassword(hexStringToByteArray("00000000"));

        //Low association
        client.setAuthentication(Authentication.LOW);
        client.setClientAddress(17);
        client.getCiphering().setAuthenticationKey("1234567887654321".getBytes());
        client.setPassword("1234567811118101".getBytes());


        String outputFile = "src/main/resources/settings.xml";

        String invocationCounter = "0.0.43.1.0.255";

        TraceLevel trace = TraceLevel.INFO;

        GXDLMSReader reader;

        Object received = null;
        GXDLMSObjectCollection objects;
        try {
            reader = new GXDLMSReader(client, media, trace,
                    invocationCounter);
            media.open();

            reader.initializeConnection();
            received = reader.read(new GXDLMSRegister("1.0.71.7.0.255"), 2);
            System.out.println(received);
            received = reader.read(new GXDLMSRegister("1.0.71.7.0.255"), 2);
            System.out.println(received);

            System.out.println("initialize done");

            objects = GXDLMSObjectCollection.load(outputFile);
            client.getObjects().addAll(objects);
            System.out.println("get objects by file");


            received = reader.read(new GXDLMSRegister("1.0.71.7.0.255"), 2);
            System.out.println(received);

            received = reader.read(new GXDLMSData("0.0.96.1.0.255"), 2);
            System.out.println(received);

            GXDLMSProfileGeneric passportProfile = (GXDLMSProfileGeneric) client.getObjects().findByLN(ObjectType.PROFILE_GENERIC, "0.0.94.7.1.255");
            received = reader.read(passportProfile, 2);
            received.toString();

            passportProfile = (GXDLMSProfileGeneric) client.getObjects().findByLN(ObjectType.PROFILE_GENERIC, "1.0.94.7.0.255");
            received = reader.read(passportProfile, 2);
            received.toString();

            reader.disconnect();
            /*
            GXDLMSProfileGeneric passportProfile = (GXDLMSProfileGeneric) client.getObjects().findByLN(ObjectType.PROFILE_GENERIC, "1.0.98.2.0.255");
            received = reader.readRowsByRange(passportProfile, new Date(1670223492L), new Date(1673223492L));
            received.toString();

            passportProfile = (GXDLMSProfileGeneric) client.getObjects().findByLN(ObjectType.PROFILE_GENERIC, "0.0.94.7.1.255");
            received = reader.read(passportProfile, 2);
            received.toString();

            passportProfile = (GXDLMSProfileGeneric) client.getObjects().findByLN(ObjectType.PROFILE_GENERIC, "1.0.94.7.0.255");
            received = reader.read(passportProfile, 2);
            received.toString();

            passportProfile = (GXDLMSProfileGeneric) client.getObjects().findByLN(ObjectType.PROFILE_GENERIC, "1.0.99.1.0.255");
            received = reader.read(passportProfile, 2);
            // received = reader.readRowsByRange(passportProfile, new Date(1670223492L), new Date(1673223492L));
            received.toString();
*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
