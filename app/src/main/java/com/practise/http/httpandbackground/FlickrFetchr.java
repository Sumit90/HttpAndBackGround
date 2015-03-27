package com.practise.http.httpandbackground;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by e00959 on 3/27/2015.
 */
public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "69af587a231483ddcda2bdcafe10ca98";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String XML_PHOTO = "photo";


    public ArrayList<GalleryItem> fetchItems() throws IOException,XmlPullParserException
    {
        ArrayList<GalleryItem> galleryItems = new ArrayList<GalleryItem>();

        String url= Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .build().toString();

        String xmlString = getURL(url);
        Log.i(TAG, "Received xml: " + xmlString);

        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser=xmlPullParserFactory.newPullParser();
        parser.setInput(new StringReader(xmlString));

        parseItems(galleryItems,parser);

        return galleryItems;

    }
    private byte[] getURLBytes(String urlspecpec) throws IOException
    {
        //create a URL form the string
        URL url=new URL(urlspecpec);

        // get HTTP URL connection object. This will not connect to end point.
        HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

        try {
            //This will connect to the end point
            InputStream is = httpURLConnection.getInputStream();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead=0;
            byte[] buffer = new byte[1024];

            while ((bytesRead=is.read(buffer))>0)
            {
                byteArrayOutputStream.write(buffer,0,bytesRead);
            }

            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
        finally {
            httpURLConnection.disconnect();
        }

    }

    private String getURL(String urlSpec) throws IOException
    {
        return new String(getURLBytes(urlSpec));
    }

    private void parseItems(ArrayList<GalleryItem> galleryItem,XmlPullParser parser)
            throws XmlPullParserException,IOException
    {
        int event=parser.next();
        while(event!=XmlPullParser.END_DOCUMENT)
        {
            if(event==XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName()))
            {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                GalleryItem item = new GalleryItem();
                item.setmId(id);
                item.setmCaption(caption);
                item.setmUrl(smallUrl);
                galleryItem.add(item);
            }

            event=parser.next();
        }

    }
}
