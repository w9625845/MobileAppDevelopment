package uk.ac.tees.mad.W9625845


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.content.pm.PackageManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class Near : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var LOCATION_PERMISSION_REQUEST = 34

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkLocationPermission()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val locations = listOf(
            LatLng(51.47572608211605, -0.044862551009314106),
            LatLng(51.47120264937392, -0.011456614762416719),

            LatLng(51.44676836134134, -0.15778913984393464),
            LatLng(51.515286987963805, 0.02921148153815398),
            LatLng(51.49765836830821, 0.08876119397827542),
            LatLng(51.540362111727426, -0.16142021987077126),

        )
        var i=0
        locations.forEach { location ->

            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Hospital")
                    .icon(BitmapDescriptorFactory.fromBitmap(getColoredBitmap(this, R.drawable.hospitcom ,Color.BLUE)))
            )
            i++
        }

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(51.540362111727426, -0.16142021987077126))
        )

    }

    fun getColoredBitmap(context: Context, drawableId: Int, color: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)?.mutate() ?: return null
        val bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        DrawableCompat.setTint(drawable, color)
        drawable.draw(canvas)
        return bitmap
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            showCurrentLocationOnMap()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showCurrentLocationOnMap()
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun showCurrentLocationOnMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                mMap.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
            }
        }
    }
}