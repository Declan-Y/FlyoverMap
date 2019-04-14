package declan.com.flyovermap;


import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.xmlpull.v1.XmlPullParserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
public static final String TAG = MainActivity.class.getSimpleName();



private List<City> cityList = new ArrayList<>();
private static List<Article> mArticleList = new ArrayList<>();
private Map<LatLng, List<Article>> placedMarkers = new HashMap<>();
private  SupportMapFragment mapFragment;
private AsyncTask flyoverDataAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readCityData();
        setContentView(R.layout.activity_main);


         flyoverDataAsyncTask = new GrabFlightData().execute(); //grab the data for flyovers on a different thread to not block UI
         mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng alice = new LatLng(-23.6980, 133.8807);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alice, 3.5f)); //centers the map on Australia on start up
        googleMap.setOnMarkerClickListener(m -> {
            List<Article> articles = placedMarkers.get(m.getPosition());
            Log.d(TAG, "MainActivity"+articles.size());
            List<String> values = new ArrayList<>();
            for(Article article : articles){
                String title = article.getTitle();
                String description = article.getDescription();
                values.add(title);
                values.add(description);

            }
            Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
            intent.putStringArrayListExtra("VALUES", (ArrayList<String>)values);
            startActivity(intent);


            return false;
        });
        //for each article, search each city for a match in the title or description
        for (int i = 0; i < mArticleList.size(); i++) {
            for (int j = 0; j < cityList.size(); j++) {
                Article article = mArticleList.get(i);
                City city = cityList.get(j);
                if (article.getTitle().contains(city.getName()) || article.getDescription().contains(city.getName())) {
                    double lat = city.getLatitude();
                    double lng = city.getLongitude();
                    LatLng latlng = new LatLng(lat, lng);
                    if(!placedMarkers.containsKey(latlng)) {
                        List<Article> cityFlyovers = new ArrayList<>();
                        cityFlyovers.add(article);
                        placedMarkers.put(latlng, cityFlyovers);
                        googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.plane)));

                    }else {
                        List<Article> cityFlyovers = placedMarkers.get(latlng);
                        cityFlyovers.add(article);

                    }

                }

            }
        }

    }

        private void readCityData(){

        //reads in a list of cities from a CSV file
            InputStream is = getResources().openRawResource(R.raw.au);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line;
            try {
                reader.readLine();
                while((line = reader.readLine()) != null){
                    String[] tokens = line.split(",");
                    String name = tokens[0];
                    double lat = Double.parseDouble(tokens[1]);
                    double lng = Double.parseDouble(tokens[2]);
                    City city = new City(name, lat, lng);
                    cityList.add(city);
                }

                }catch(IOException e){
                e.getMessage();
            }
        }
    private  class GrabFlightData extends AsyncTask<Void, Void, List<Article>> {
        @Override
        protected List<Article> doInBackground(Void... voids) {
            try {
                URL url = new URL("https://www.airforce.gov.au/feed/flying-activities");
                InputStream input = url.openConnection().getInputStream();
                return new FlightData().parse(input);
            } catch (MalformedURLException m) {
                m.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                e.getMessage();
            } catch (XmlPullParserException x) {
                x.printStackTrace();
                x.getLineNumber();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Article> articleList) {
            mArticleList = articleList;
            mapFragment.getMapAsync(MainActivity.this);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flyoverDataAsyncTask.cancel(true);
    }


}
