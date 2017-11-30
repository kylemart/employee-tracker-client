package group19.employeetracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

class FilteredMarkers {
    private ArrayList<Marker> filteredMarkers;
    public int size;
    private int nextIndex = 0;

    FilteredMarkers(ArrayList<Marker> filteredMarkers) {
        this.filteredMarkers = filteredMarkers;
        this.size = filteredMarkers.size();
    }

    Marker getNext() {
        if(size == 0)
            return null;

        return filteredMarkers.get((nextIndex++) % size);
    }
}

class EmployeeMarker {
    private String fullName;
    private String lastName;
    private Marker marker;

    EmployeeMarker(String fn, String ln, Marker m) {
        fullName = fn;
        lastName = ln;
        marker = m;
    }

    String getFullName() {
        return fullName;
    }

    String getLastName() {
        return lastName;
    }

    Marker getMarker() {
        return marker;
    }
}

@SuppressWarnings("MissingPermission")
public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private GoogleMap mMap;

    Context ctx;

    // The employees visible to the user
    private HashSet<Employee> employees = new HashSet<>();

    private HashSet<Integer> groupIDs;

    // Current set of markers drawn to map
    private HashSet<EmployeeMarker> markers = new HashSet<>();

    // Set of markers found in a search
    private FilteredMarkers filteredMarkers;

    // Helps with orientation changes
    private boolean reloaded = false;
    private CameraPosition savedView;

    private boolean nextVisibility = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ctx = this;

        groupIDs = (HashSet<Integer>) getIntent().getSerializableExtra("groups");

        if(savedInstanceState != null) {
            reloaded = true;
            savedView = savedInstanceState.getParcelable("view");
        }

        if(!getIntent().getBooleanExtra("group", false))
            ;
        else
            employees = getEmployees();

        // TODO: This permission logic needs to be on the login page
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private HashSet<Employee> getEmployees() {
        employees.clear();

        JSONArray jsonArray = new JSONArray();

        for(int id : groupIDs)
            jsonArray.put(id);

        JSONObject payload = new JSONObject();

        try {
            payload.put("ids", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<JSONObject, Void, JSONObject>() {
            protected JSONObject doInBackground(JSONObject[] params) {
                return BackendServiceUtil.post("group/many", params[0], PrefUtil.getAuth(ctx));
            }
            protected void onPostExecute(JSONObject response) {
                if (response.optBoolean("success")) {
                    JSONArray jEmployees = null;

                    try {
                        jEmployees = response.getJSONArray("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        for (int i = 0; i < jEmployees.length(); ++i) {
                            JSONObject employee = jEmployees.getJSONObject(i);
                            String name = employee.getString("first_name") + " " + employee.getString("last_name");
                            String email = employee.getString("email");
                            LatLng coords2 = new LatLng(employee.getDouble("lat"), employee.getDouble("lng"));

                            String imgData = employee.getString("profile_img");
                            if (imgData.length() == 0) {
                                imgData = Employee.encodePic(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.default_profile));
                            }

                            Bitmap pic = Employee.decodePic(imgData);

                            employees.add(new Employee(name, email, "Fred", coords2, pic));
                        }

                        Log.d("Map", employees.toString());

                        populateMap(employees);
                    } catch (JSONException e) {
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            response.optString("message", "Bad connection :("),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }.execute(payload);

        return employees;
    }

    public LatLng getUserCoords() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String bestProvider;
        if((bestProvider = locationManager.getBestProvider(new Criteria(), false)) == null)
            return new LatLng(0, 0);

        Location location = locationManager.getLastKnownLocation(bestProvider);

        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void populateMap(HashSet<Employee> employees) {
        mMap.clear();
        markers.clear();

        Marker marker;

        for(Employee employee : employees) {
            Bitmap pic = Bitmap.createScaledBitmap(employee.pic, 150, 150, false);

            marker = mMap.addMarker(new MarkerOptions().position(employee.coords)
                    .title(employee.fullName)
                    .icon(BitmapDescriptorFactory.fromBitmap(pic)));
            marker.setTag(employee);

            markers.add(new EmployeeMarker(employee.fullName, employee.lastName, marker));
        }
    }

    private ArrayList<Marker> search(String query, HashSet<EmployeeMarker> markers) {
        HashSet<Marker> filteredMarkers = new HashSet<>();

        if(query.isEmpty() || markers == null)
            return new ArrayList<>(filteredMarkers);

        markers.forEach(marker -> {
            if(marker.getFullName().toLowerCase().startsWith(query.toLowerCase())
            || marker.getLastName().toLowerCase().startsWith(query.toLowerCase()))
                filteredMarkers.add(marker.getMarker());
        });

        return new ArrayList<>(filteredMarkers);
    }

    private void getNextMarker() {
        if(filteredMarkers == null)
            return;

        Marker marker = filteredMarkers.getNext();

        if(marker != null) {
            marker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            if(!reloaded)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(getUserCoords()));
            else
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(savedView));
        }

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.setOnInfoWindowClickListener(this);

        populateMap(employees);
    }

    // Sends the employee that was clicked on to the profile activity
    @Override
    public void onInfoWindowClick(Marker marker) {
        Employee employee = (Employee) marker.getTag();

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("employee", employee);

        startActivity(intent);
    }

    // TODO: Move this to the login page of the app
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 34: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permission denied. TODO: This will have to stop the employee from logging in
                }
                else {// permission was granted. Won't need this in final app
                    mMap.setMyLocationEnabled(true);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(getUserCoords()));
                }

                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem search = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setMaxWidth(4000);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filteredMarkers = new FilteredMarkers(search(query, markers));
                getNextMarker();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredMarkers = new FilteredMarkers(search(newText, markers));
                getNextMarker();

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                nextVisibility = !nextVisibility;
                invalidateOptionsMenu();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        menu.findItem(R.id.action_next).setVisible(nextVisibility);

        if(nextVisibility)
            search.expandActionView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.employeeList) {
            /*Intent intent = new Intent(getApplicationContext(), employeeList.class);
            startActivity(intent);*/
        } else if(id == R.id.action_search) {
            nextVisibility = !nextVisibility;
            invalidateOptionsMenu();
        } else if(id == R.id.action_next)
            getNextMarker();
        else if(id == R.id.action_refresh)
            getEmployees();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("view", mMap.getCameraPosition());

        super.onSaveInstanceState(savedInstanceState);
    }
}
