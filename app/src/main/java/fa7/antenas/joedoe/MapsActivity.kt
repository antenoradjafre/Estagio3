package fa7.antenas.joedoe

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest : LocationRequest
    private lateinit var result : Task<LocationSettingsResponse>
    private lateinit var txtKeyword : EditText
    private lateinit var btnSearch : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        client = GoogleApiClient.Builder(this).addApi(AppIndex.API).addApi(LocationServices.API).build()

        setContentView(R.layout.activity_maps)

        txtKeyword = findViewById(R.id.txtKeyword)
        btnSearch = findViewById(R.id.btnSearch)
        btnSearch.setOnClickListener { txtKeyword.setText("Worked") }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//                .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
        ask()

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

    private fun ask(){
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 0x7)
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val marker = mMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Maker Position"))
        try{
            val locationListener : LocationListener = object : LocationListener {
                override fun onLocationChanged(location : Location){
                    val latLng = LatLng(location.latitude, location.longitude)
                    marker.position = latLng
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                }
                override fun onStatusChanged(provider:String, status:Int, extras:Bundle){}
                override fun onProviderEnabled(provider:String){}
                override fun onProviderDisabled(provider:String){}
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, locationListener)
        }catch (ex:SecurityException){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
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
