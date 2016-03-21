package com.jagdiv.android.dashclk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.android.gms.common.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.location.LocationClient;

//import android.support.v4.app.FragmentActivity;


public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {
    //LocationClient mLocationClient;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    GoogleApiClient mLocationClient;
    private TextView addressLabel;
    private TextView locationLabel;
    private Button getLocationBtn;
    private Button disconnectBtn;
    private Button connectBtn;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Context mContext;

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
        /*
		* Google Play Services is missing or update is required
		*  return code could be
		* SUCCESS,
		* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
		* SERVICE_DISABLED, SERVICE_INVALID.
		*/
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("in oncreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button startBtn = (Button) findViewById(R.id.sendEmail);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendEmail();
            }
        });
        locationLabel = (TextView) findViewById(R.id.locationLabel);
        addressLabel = (TextView) findViewById(R.id.addressLabel);
        getLocationBtn = (Button) findViewById(R.id.getLocation);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //displayCurrentLocation();
                displayCurrentAddress();
            }
        });

        disconnectBtn = (Button) findViewById(R.id.disconnect);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mLocationClient.disconnect();
                locationLabel.setText("Got disconnected....");
            }
        });

        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                mLocationClient.connect();
                //   locationLabel.setText("Got connected....");
            }
        });

        // Create the LocationRequest object
        // mLocationClient = new LocationClient(this, this, this);
        if (checkGooglePlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //@Override
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if (mLocationClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mLocationClient, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("in onstart");
        // Connect the client.
        toConnect();
    }

    private void toConnect() {
        if (!mLocationClient.isConnecting() &&
                !mLocationClient.isConnected()) {
            mLocationClient.connect();
            System.out.println("in toConnect Got connected...going to onconnected");
            locationLabel.setText("Got connected....");
        } else {
            System.out.println("in toConnect not connected");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            System.out.println("in toConnect not connected mLastLocation" + mLastLocation);
            locationLabel.setText("connected....");
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect the client.
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
        // mLocationClient.disconnect();

        locationLabel.setText("Got disconnected....");
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        System.out.println("in onconnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if (mLastLocation != null) {

            Toast.makeText(this, "Connected and Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
            System.out.println("in onconnected not null location  Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude());
            // startLocationUpdates();
        } else {
            Toast.makeText(this, "Check location first", Toast.LENGTH_LONG).show();
            System.out.println("in onconnected not null location  Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude());
            locationLabel.setText("need to check location first....");
        }
        // Display the connection status
        // Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    // @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Display the error code on failure
        Toast.makeText(this, "Connection Failure : " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();

    }

    public void displayCurrentLocation() {
        // Get the current location's latitude & longitude
        // Location currentLocation = mLocationClient.getLastLocation();
        // toConnect();
        if (mLastLocation != null) {
            Location currentLocation = mLastLocation;
            String msg = "Current Location: " +
                    Double.toString(currentLocation.getLatitude()) + "," +
                    Double.toString(currentLocation.getLongitude());

            // Display the current location in the UI
            locationLabel.setText(msg);

            // To display the current address in the UI
         //   (new GetAddressTask(this)).execute(currentLocation);

            try {
                Geocoder geocoder = new Geocoder(MainActivity.this);
                System.out.println("geocoder.isPresent()" + geocoder.isPresent());
                int attempt=10;
                List<Address> list = geocoder.getFromLocationName("taj mahal", 1);
                while (list.size()==0 && attempt>0) {
                    attempt--;
                    list = geocoder.getFromLocationName("taj mahal", 1);
                    System.out.println("attempt"+attempt);
                }
                locationLabel.setText("not getting");
                if (list.size()>0) {
                    Address address = list.get(0);
                    String locality = address.getLocality();
                    System.out.println("locality" + locality);
                    Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
                    double lat = address.getLatitude();
                    double lon = address.getLongitude();
                  //  gotoLocation(lat, lon, 15);
                    locationLabel.setText("lattt"+lat+" "+lon);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }


        } else
            locationLabel.setText("Need to check location");

    }

    public void displayCurrentAddress() {
        // Get the current location's latitude & longitude
        // Location currentLocation = mLocationClient.getLastLocation();
        // toConnect();
        if (mLastLocation != null) {
            Location currentLocation = mLastLocation;
            String msg = "Current Location: " +
                    Double.toString(currentLocation.getLatitude()) + "," +
                    Double.toString(currentLocation.getLongitude());

            // Display the current location in the UI
            locationLabel.setText(msg);

            // Kickoff an asynchronous task to fire the reverse geocoding
            // request off to google
            ReverseGeocodeLookupTask task = new ReverseGeocodeLookupTask();
            task.applicationContext = this;
            task.execute();


        } else

        {
            locationLabel.setText("Need to check location");
            // If we don't know our location yet, we can't do reverse
            // geocoding - display a please wait message
            //   showToast("Please wait until we have a location fix from the gps");
        }
    }

    public class ReverseGeocodeLookupTask extends AsyncTask<Void, Void, String[]> {
        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(applicationContext, "Please wait...contacting the tubes.",
                    "Requesting reverse geocode lookup", true);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String localityName = "";
            String[] strArray=new String[3] ;
            if (mLastLocation != null) {
                localityName = GeoCoder.reverseGeocode(mLastLocation);
                System.out.println("from  GeoCoder.reverseGeocode(mLastLocation) --localityName " + localityName+" mLastLocation"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude());
            strArray[0]=mLastLocation.getLatitude()+"";
            strArray[1]=mLastLocation.getLongitude()+"";
                strArray[2]=localityName;
            }

            return strArray;
        }

        @Override
        protected void onPostExecute(String result[]) {
            addressLabel.setText(result[2]);
            this.dialog.cancel();
            Intent myIntent= new Intent();
            myIntent.putExtra("latitude", result[0]);
            myIntent.putExtra("longitude", result[1]);
            myIntent.putExtra("locality",result[2]);
           myIntent.setClass(getApplicationContext(), MapActivity.class);
            startActivity(myIntent);
            //  Utilities.showToast("Your Locality is: " + result, applicationContext);
        }
    }

    /*
    * Following is a subclass of AsyncTask which has been used to get
    * address corresponding to the given latitude & longitude.
    */
    private class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        /*
        * When the task finishes, onPostExecute() displays the address.
        */
        @Override
        protected void onPostExecute(String address) {
            // Display the current address in the UI
            addressLabel.setText(address);
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            // Get the current location from the input parameter list
            Location loc = params[0];

            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                System.out.println("location loc.getLatitude()" + loc.getLatitude() + " loc.getLongitude() " + loc.getLongitude() + "geocoder.isPresent() " + geocoder.isPresent());
                int attempt=10;
                //List<Address> list = geocoder.getFromLocationName("taj mahal", 1);
                //addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                addresses = geocoder.getFromLocationName("taj mahal", 1);
                while (addresses.size()==0 && attempt>0) {
                    attempt--;
                    addresses = geocoder.getFromLocationName("taj mahal", 1);
                    System.out.println("attempttttttttttttt"+attempt);
                }
             //   addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity", "IOException in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) + " , " + Double.toString(loc.getLongitude()) + " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }catch (Exception e3){
                String errorString = "taj amahal service";

                Log.e("MainActivity", errorString);
                e3.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);

            /*
            * Format the first line of address (if available),
            * city, and country name.
            */
                String addressText = String.format("%s, %s, %s",

                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",

                        // Locality is usually a city
                        address.getLocality(),

                        // The country of the address
                        address.getCountryName());
                System.out.println("addressTexttttttttt"+addressText);
                // Return the text
                return addressText;
            } else {
                return "No address found";
            }
        }/*
        public static List<Address> getStringFromLocation(double lat, double lng)
                throws IOException, JSONException {

            String address = String
                    .format(Locale.ENGLISH,                                 "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                            + Locale.getDefault().getCountry(), lat, lng);
            HttpGet httpGet = new HttpGet(address);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            List<Address> retList = null;

            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String indiStr = result.getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setAddressLine(0, indiStr);
                    retList.add(addr);
                }
            }

            return retList;
        }*/

    }// AsyncTask class

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "wow you did it to send email");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
