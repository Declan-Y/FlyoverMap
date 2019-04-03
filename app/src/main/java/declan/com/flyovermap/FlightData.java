package declan.com.flyovermap;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FlightData {
    public static final String TAG = FlightData.class.getSimpleName();
    public static final String SKIP_THIS_PHRASE = "RAAF Flying Activities";
    public List<Article> parse(InputStream in) throws XmlPullParserException, IOException {
        String title = null;

        //String link = null;
        String description = null;
        String date = null;
        boolean isItem = false;
        List<Article> articleList = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(in, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {//title
                    title = result;
                }  if (name.equalsIgnoreCase("description")) {//description
                    if(!result.equals(SKIP_THIS_PHRASE)) {
                        description = result;

                    }
                } if(name.equalsIgnoreCase("pubdate")){
                    date = result;
                    Log.d(TAG, date+" ");
                }

                if (title != null && description != null) {
                    if (isItem) {


                        Article item = new Article(title, description);
                        articleList.add(item);
                    } else {
                        continue;
                        //do nothing
                    }

                    title = null;
                    description = null;
                    isItem = false;
                }
            }


            return articleList;
        } finally {
            in.close();
        }
    }
}
