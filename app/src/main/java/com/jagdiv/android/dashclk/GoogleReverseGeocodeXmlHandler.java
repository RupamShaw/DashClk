package com.jagdiv.android.dashclk;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Dell on 14/03/2016.
 */
public class GoogleReverseGeocodeXmlHandler extends DefaultHandler
{
    private boolean inLocalityName = false;
    private boolean finished = false;
    private StringBuilder builder;
    private String localityName;

    public String getLocalityName()
    {
        System.out.println("this.localityName "+this.localityName);
        return this.localityName;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        if (this.inLocalityName && !this.finished)
        {
            if ((ch[start] != '\n') && (ch[start] != ' '))
            {
                builder.append(ch, start, length);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException
    {
        super.endElement(uri, localName, name);

        if (!this.finished)
        {
            if (localName.equalsIgnoreCase("formatted_address"))
            {
                this.localityName = builder.toString();
                System.out.println("this.localityName "+this.localityName);
                this.finished = true;
            }

            if (builder != null)
            {
                builder.setLength(0);
            }
        }
    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);

        if (localName.equalsIgnoreCase("formatted_address"))
        {
            this.inLocalityName = true;
        }
    }
}

