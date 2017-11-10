package group19.employeetracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;

@SuppressWarnings("MissingPermission")
public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;

    private User user;

    // The employees visible to the user
    private HashSet<Employee> employees;

    // The groups that the visible employees are part of
    private HashSet<String> groups;

    // The groups shown on the map
    private HashSet<String> activeGroups = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // TODO: Uncomment when linked with login page and intent passes the user along
        //user = getIntent().getParcelableExtra("user");

        employees = getEmployees();
        groups = findGroups();

        // This permission logic needs to be on the login page
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployees();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        createNavView();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Intent intent = new Intent(this, BackgroundGPS.class);
        startService(intent);

        View navHeader = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        Button logout = (Button) navHeader.findViewById(R.id.logout);

        // TODO: Also return user to login page
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(intent);
            }
        });
    }

    private void createNavView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        checkAll(menu);
        SubMenu subMenu = menu.addSubMenu("Groups");

        for(String group : groups)
            makeListener(subMenu, group);
    }

    private void makeListener(SubMenu subMenu, final String group) {
        MenuItem item = subMenu.add(group);
        CheckBox check = new CheckBox(this);

        if(activeGroups.contains(group))
            check.setChecked(true);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashSet<String> activeGroup = new HashSet<>();
                activeGroup.add(group);

                if (isChecked) {
                    populateMap(filter(employees, activeGroup));
                } else {
                    mMap.clear();
                    activeGroups.remove(group);

                    populateMap(filter(employees, activeGroups));
                }

                // Uncheck checkAll checkbox if at least one checkbox is unchecked
                // Or, check checkAll checkbox if all checkboxes are checked

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                CheckBox check = (CheckBox)navigationView.getMenu().getItem(0).getActionView();

                check.setOnCheckedChangeListener(null);
                check.setChecked(activeGroups.size() == groups.size());
                check.setOnCheckedChangeListener(checkAllListener());

                // TODO: Probably deleting this in favor of above code. Not enough testing has been done
                /* Uncheck checkAll checkbox if all checkboxes are unchecked
                   Or, check checkAll checkbox if all checkboxes are checked
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                SubMenu menu = navigationView.getMenu().getItem(1).getSubMenu();

                for (int i = 0; i < groups.size(); i++) {
                    CheckBox check = (CheckBox) menu.getItem(i).getActionView();

                    if(check.isChecked() == !isChecked)
                        return;
                }

                CheckBox check = (CheckBox)navigationView.getMenu().getItem(0).getActionView();
                check.setChecked(isChecked);*/
            }
        });

        item.setActionView(check);
    }

    private CompoundButton.OnCheckedChangeListener checkAllListener() {
        CompoundButton.OnCheckedChangeListener checkAllListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

                SubMenu menu = navigationView.getMenu().getItem(1).getSubMenu();

                for (int i = 0; i < groups.size(); i++) {
                    CheckBox checkbox = (CheckBox) menu.getItem(i).getActionView();

                    checkbox.setChecked(isChecked);
                }
            }
        };

        return checkAllListener;
    }

    private void checkAll(Menu menu) {
        MenuItem item = menu.add("Select all");
        final CheckBox check = new CheckBox(this);

        if(activeGroups.size() == groups.size())
            check.setChecked(true);

        check.setOnCheckedChangeListener(checkAllListener());

        item.setActionView(check);
    }

    // TODO: Replace this temp method with the real one from the DatabaseIO class
    private HashSet<Employee> getEmployees() {
        HashSet<Employee> employees = new HashSet<>(6);

        String[] names = {"Ryan Graves", "Kyle Martinez", "David Mosquera", "Carlos Najera",
                "John Sermarini", "Talbot White"
        };

        String[] groups = {"Alpha,Delta,Sigma","Alpha","Sigma","Alpha,Theta","Delta","Alpha,Delta,Theta"};

        LatLng[] coords = {new LatLng(28.604273, -81.200187), new LatLng(28.603718, -81.200488),
                new LatLng(28.599837, -81.198138), new LatLng(28.601940, -81.200552),
                new LatLng(28.601947, -81.198342), new LatLng(28.601447, -81.198438)
        };

        int[] pics = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f};

        Bitmap pic;

        for(int i = 0; i < names.length; i++) {
            pic = BitmapFactory.decodeResource(this.getResources(), pics[i]);

            employees.add(new Employee(names[i], groups[i], coords[i], pic));
        }

        return employees;
    }

    // TODO: Remove temp data when DatabaseIO is working
    private void updateEmployees() {
        employees = new HashSet<>();

        Bitmap pic = BitmapFactory.decodeResource(this.getResources(), R.drawable.a);

        employees.add(new Employee("Test", "New Group", new LatLng(28.604279, -81.200189), pic));

        groups = findGroups();

        HashSet<String> newActiveGroups = new HashSet<>();
        for(String group : activeGroups)
            if(groups.contains(group))
                newActiveGroups.add(group);
        activeGroups = newActiveGroups;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().clear();

        createNavView();

        mMap.clear();
        populateMap(filter(employees, activeGroups));
    }

    private HashSet<String> findGroups() {
        HashSet<String> groups = new HashSet<>();

        for(Employee employee : employees)
            for(String group : employee.group)
                if(!groups.contains(group))
                    groups.add(group);

        return groups;
    }

    private LatLng getUserCoords() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));

        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void populateMap(HashSet<Employee> employees) {
        Marker marker;

        for(Employee employee : employees) {
            Bitmap pic = Bitmap.createScaledBitmap(employee.pic, 150, 150, false);

            marker = mMap.addMarker(new MarkerOptions().position(employee.coords)
                    .title(employee.fullName)
                    .icon(BitmapDescriptorFactory.fromBitmap(pic)));
            marker.setTag(employee);
        }
    }

    private HashSet<Employee> filter(HashSet<Employee> employees, HashSet<String> selectedGroups) {
        HashSet<Employee> filteredEmployees = new HashSet<>();

        for(Employee employee : employees)
            for(String j : selectedGroups) {
                if(!activeGroups.contains(j))
                    activeGroups.add(j);

                if(employee.group.contains(j)) {
                    filteredEmployees.add(employee);
                    break;
                }
            }

        return filteredEmployees;
    }

    private void search(String query) {
        // TODO: Implement Searching. Maybe with this, idk

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(getUserCoords()));
        }

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.setOnMarkerClickListener(this);
    }

    // Sends the employee that was clicked on to the profile activity
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Employee employee = (Employee) marker.getTag();

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("user", user);
        intent.putExtra("employee", employee);

        startActivity(intent);

        return false;
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

            // TODO: permission handling for the camera goes here maybe
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Extends the checkable region of the checkbox to the whole nav item
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        CheckBox checkBox = (CheckBox) item.getActionView();
        checkBox.setChecked(!checkBox.isChecked());

        return true;
    }
}
