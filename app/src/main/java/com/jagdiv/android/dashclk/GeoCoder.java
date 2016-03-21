package com.jagdiv.android.dashclk;

import android.location.Location;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Dell on 14/03/2016.
 */

public class GeoCoder
{
    public static String reverseGeocode(Location loc)
    {
        //http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
        String localityName = "";
        HttpURLConnection connection = null;
        URL serverAddress = null;

        try
        {
            // build the URL using the latitude & longitude you want to lookup
            // NOTE: I chose XML return format here but you can choose something else
            //serverAddress = new URL("http://maps.google.com/maps/geo?q=" + Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude()) +
                    //"&output=xml&oe=utf8&sensor=true&key=AIzaSyDC0L3oLRRihnPRFTPd3xPjAeH3fasDXwA");
           // response = getRespByURL("http://maps.googleapis.com/maps/api/geocode/json?address=" + addr + "&sensor=false");

           // serverAddress = new URL("http://maps.google.com/maps/geo?q=" + Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude()) +
             //       "&output=xml&oe=utf8&sensor=true&key=" + R.string.GOOGLE_MAPS_API_KEY);
            //set up out communications stuff
           // serverAddress = new URL("https://maps.googleapis.com/maps/api/geocode/xml?latlng="+Double.toString(loc.getLatitude())+ "," +Double.toString(loc.getLongitude())+"key=AIzaSyDC0L3oLRRihnPRFTPd3xPjAeH3fasDXwA" );
           String uu="https://maps.googleapis.com/maps/api/geo?q=" +Double.toString(loc.getLatitude())+ "," +Double.toString(loc.getLongitude())+"&output=xml&oe=utf8&sensor=true&key=" + R.string.GOOGLE_MAPS_API_KEY;
          String uu1="maps.googleapis.com/maps/api/geocode/xml?latlng=44.4647452,7.3553838&sensor=true";
          //  serverAddress = new URL("https://maps.googleapis.com/maps/api/geo?q=" +Double.toString(loc.getLatitude())+ "," +Double.toString(loc.getLongitude())+

          System.out.println("uu1111111111111"+uu1);
            serverAddress = new URL("https://maps.googleapis.com/maps/api/geocode/xml?latlng=" +Double.toString(loc.getLatitude())+ "," +Double.toString(loc.getLongitude())+"&sensor=true");
                  //  "key=AIzaSyDC0L3oLRRihnPRFTPd3xPjAeH3fasDXwA" );
            //set up out communications stuff
            connection = null;

            //Set up the initial connection
            connection = (HttpURLConnection)serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(20000);

            connection.connect();

            try
            {
             //   System.out.println("connection.getInputStream()"+connection.getInputStream().toString());
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());

                InputSource source = new InputSource(isr);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xr = parser.getXMLReader();
                GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();
           // System.out.println("source"+source);
                xr.setContentHandler(handler);
                xr.parse(source);

                localityName = handler.getLocalityName();

                String response="";
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }

                System.out.println("response"+response);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return localityName;
    }
}

