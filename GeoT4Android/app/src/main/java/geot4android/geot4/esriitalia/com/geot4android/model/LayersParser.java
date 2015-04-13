package geot4android.geot4.esriitalia.com.geot4android.model;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giovannipalombi on 01/04/15.
 */

public class LayersParser {
    private static final String ns = null;

    public List<ServiceLayer> parse(InputStream in)
            throws XmlPullParserException, IOException {
        try {
            // instantiate a parser, use an InputStream as input
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            // start the parsing process
            parser.nextTag();
            // invoke readFeed() method to extract and process data
            return readServiceLayers(parser);
        } finally {
            in.close();
        }
    }

    private List<ServiceLayer> readServiceLayers(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<ServiceLayer> serviceLayers = new ArrayList<ServiceLayer>();

        parser.require(XmlPullParser.START_TAG, ns, "layers");
        while (parser.next() != XmlPullParser.END_TAG) {
            String pName = parser.getName();
            // Start looking for the geomessage tag
            if (pName != null && pName.equals("layer")) {
                serviceLayers.add(readServiceLayer(parser));
            } else {
                Log.d("DEBUG", "TAG NAME = " + pName);
                continue;
            }

        }
        return serviceLayers;
    }

    private ServiceLayer readServiceLayer(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "layer");

        String name = null;
        String type = null;
        boolean visibility = false;
        String url = null;
        String other = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String pName = parser.getName();
            if (pName.equals("name")) {
                name = readName(parser);
            } else if (pName.equals("type")) {
                type = readType(parser);
            } else if (pName.equals("visibility")) {
                visibility = readVisibility(parser);
            } else if (pName.equals("url")) {
                url = readURL(parser);
            }
            else
            {
                parser.require(XmlPullParser.START_TAG, ns, pName);
                other = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, pName);
            }
        }
        return new ServiceLayer(name, type, visibility, url);
    }

    private boolean readVisibility(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "visibility");
        boolean visibility = Boolean.parseBoolean(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "visibility");

        return visibility;
    }

    private String readURL(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "url");
        String url = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "url");

        return url;
    }

    private String readType(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "type");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");

        return type;
    }

    private String readName(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");

        return name;
    }

    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    public class ServiceLayer {

        public final String name;
        public final String type;
        public final boolean visibility;
        public final String url;

        private ServiceLayer(String name, String type, boolean visibility, String url) {
            this.name = name;
            this.type = type;
            this.visibility = visibility;
            this.url = url;
        }
    }
}
