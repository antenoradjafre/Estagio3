package fa7.antenas.joedoe

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class MapsActivity : AppCompatActivity() {

    private lateinit var locationRequest : LocationRequest
    private lateinit var result : Task<LocationSettingsResponse>
    private lateinit var txtKeyword : EditText
    private lateinit var btnSearch : Button
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var gpsTracker : GPSTracker
    private lateinit var myLatLng : LatLng
    private lateinit var database : FirebaseDatabase
    private lateinit var foundLocation : fa7.antenas.joedoe.domain.Location
    private lateinit var addresses : List<Address>
    private lateinit var geocoder : Geocoder

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        FirebaseApp.initializeApp(this)
        initFirebase()
        ask()
        txtKeyword = findViewById(R.id.txtKeyword)
        btnSearch = findViewById(R.id.btnSearch)
        initializeListener()
        requestLocation()
        btnSearch.setOnClickListener {
                geocoder = Geocoder(this, Locale.getDefault())
                foundLocation = fa7.antenas.joedoe.domain.Location()
                foundLocation.setName("Barneys")
                foundLocation.setFormattedAddress("Rua Dona Maria Tomásia, 740 - Aldeota, Fortaleza")
                addresses = geocoder.getFromLocationName(foundLocation.getFormattedAddress(),1)
            addresses[0]
             }

    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
    }

    override fun onResume() {
        super.onResume()

        if (checkLocationPermission()) {
            gpsTracker = GPSTracker(this)
            if (gpsTracker.canGetLocation) {
                myLatLng = LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())
            } else {
                Toast.makeText(this, "Please accept permission !!!!", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    private fun checkLocationPermission(): Boolean {
        val permission = "android.permission.ACCESS_FINE_LOCATION"
        val res = this.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocation() {
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, locationListener)
        }catch (ex:SecurityException){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeListener() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location : Location){
                myLatLng = LatLng(location.latitude, location.longitude)
            }
            override fun onStatusChanged(provider:String, status:Int, extras:Bundle){}
            override fun onProviderEnabled(provider:String){}
            override fun onProviderDisabled(provider:String){}
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun ask(){
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 0x7)
    }

    private fun askForPermission(permission:String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }else{
            Toast.makeText(this, "" + permission + "is already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(ActivityCompat.checkSelfPermission(this, permissions.get(0)) == PackageManager.PERMISSION_GRANTED){
            askForGPS()
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askForGPS(){
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30*1000
        locationRequest.fastestInterval = 5*1000
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
        result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
    }
}
