package com.example.mukgoapplication.map

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mukgoapplication.BuildConfig
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.example.mukgoapplication.R
import com.example.mukgoapplication.model.AutocompleteEditText
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import java.util.*


/**
 * Activity for using Place Autocomplete to assist filling out an address form.
 */
class AutocompleteAddressActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var address1Field: AutocompleteEditText
//    private var stateField: EditText? = null
//    private var postalField: EditText? = null
//    private var countryField: EditText? = null
    private var coordinates: LatLng? = null
    private var checkProximity = false
    private var mapFragment: SupportMapFragment? = null
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var placesClient: PlacesClient? = null
    private var mapPanel: View? = null
    private var deviceLocation: LatLng? = null
    private var inputAddress:String="목표위치"

    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)


    var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }
    private val startAutocomplete = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    // Write a method to read the address components from the Place
                    // and populate the form with the address components
                    Log.d(
                        TAG,
                        "Place: " + place.addressComponents
                    )
                    fillInAddress(place)
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(
                    TAG,
                    "User canceled autocomplete"
                )
            }
        } as ActivityResultCallback<ActivityResult>)

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        address1Field.setOnClickListener(startAutocompleteIntentListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use whatever theme was set from the MainActivity - some of these colors (e.g primary color)
        // will get picked up by the AutocompleteActivity.
//        val theme = intent.getIntExtra(MainActivity.THEME_RES_ID_EXTRA, 0)
//        if (theme != 0) {
//            setTheme(theme)
//        }
        setContentView(R.layout.activity_autocomplete_address)
        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)
        address1Field = findViewById(R.id.autocomplete_address1)
//        stateField = findViewById(R.id.autocomplete_state)
//        postalField = findViewById(R.id.autocomplete_postal)
//        countryField = findViewById(R.id.autocomplete_country)

        // Attach an Autocomplete intent to the Address 1 EditText field
        address1Field.setOnClickListener(startAutocompleteIntentListener)

        // Update checkProximity when user checks the checkbox
        val checkProximityBox = findViewById<CheckBox>(R.id.cbAddress)
        checkProximityBox.setOnCheckedChangeListener { view: CompoundButton?, isChecked: Boolean ->
            // Set the boolean to match user preference for when the Submit button is clicked
            checkProximity = isChecked
        }

        // Submit and optionally check proximity
        val saveButton = findViewById<Button>(R.id.autocomplete_save_button)
        saveButton.setOnClickListener { v: View? -> saveForm() }

        // Reset the form
        val resetButton = findViewById<Button>(R.id.autocomplete_reset_button)
        resetButton.setOnClickListener { v: View? -> clearForm() }
    }

    private fun startAutocompleteIntent() {

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = Arrays.asList(
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LAT_LNG, Place.Field.VIEWPORT
        )

        // Build the autocomplete intent with field, country, and type filters applied
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountry("KOR")
            .build(this)

        startAutocomplete.launch(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a string resource.
            val success = map!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates!!, 15f))
        marker = map!!.addMarker(MarkerOptions().position(coordinates!!).title(inputAddress))
    }

    private fun fillInAddress(place: Place) {
        val components = place.addressComponents
        val address1 = StringBuilder()
        val postcode = StringBuilder()

        // Get each component of the address from the place details,
        // and then fill-in the corresponding field on the form.
        // Possible AddressComponent types are documented at https://goo.gle/32SJPM1
        if (components != null) {
            for (component in components.asList()) {
                val type = component.types[0]
                when (type) {
                    "street_number" -> {
                        address1.insert(0, component.name)
                    }
                    "route" -> {
                        address1.append(" ")
                        address1.append(component.shortName)
                    }
                    "postal_code" -> {
                        postcode.insert(0, component.name)
                    }
                    "postal_code_suffix" -> {
                        postcode.append("-").append(component.name)
                    }
//                    "administrative_area_lev                                                                                                                                                                                                                                                                                                                                                                             el_1" -> {
//                        stateField!!.setText(component.shortName)
//                    }
//                    "country" -> countryField!!.setText(component.name)
                }
            }
        }
        address1Field.setText(address1.toString())
//        postalField!!.setText(postcode.toString())
        // After filling the form with address components from the Autocomplete
        // prediction, set cursor focus on the second address line to encourage
        // entry of sub-premise information such as apartment, unit, or floor number.

        // Add a map for visual confirmation of the address
        showMap(place)
    }

    private fun showMap(place: Place) {
        coordinates = place.latLng

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        mapFragment =
            supportFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?

        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            mapPanel = (findViewById<View>(R.id.stub_map) as ViewStub).inflate()
            val mapOptions = GoogleMapOptions()
            mapOptions.mapToolbarEnabled(false)

            // To programmatically add the map, we first create a SupportMapFragment.
            mapFragment = SupportMapFragment.newInstance(mapOptions)

            // Then we add it using a FragmentTransaction.
            supportFragmentManager
                .beginTransaction()
                .add(R.id.confirmation_map, mapFragment!!, MAP_FRAGMENT_TAG)
                .commit()
            mapFragment!!.getMapAsync(this)
        } else {
            updateMap(coordinates)
        }
    }

    private fun updateMap(latLng: LatLng?) {
        marker!!.position = latLng!!
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        if (mapPanel!!.visibility == View.GONE) {
            mapPanel!!.visibility = View.VISIBLE
        }
    }

    private fun saveForm() {
        Log.d(
            TAG,
            "checkProximity = $checkProximity"
        )
        if (checkProximity) {
            checkLocationPermissions()
        } else {
            Toast.makeText(
                this,
                "Address accepted without checking proximity",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun clearForm() {
        address1Field.setText("")
//        stateField!!.text.clear()
//        postalField!!.text.clear()
//        countryField!!.text.clear()
        if (mapPanel != null) {
            mapPanel!!.visibility = View.GONE
        }
        address1Field.requestFocus()
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Since ACCESS_FINE_LOCATION is the only permission in this sample,
            // run the location comparison task once permission is granted.
            // Otherwise, check which permission is granted.
            andCompareLocations
        } else {
            // Fallback behavior if user denies permission
            Log.d(
                TAG,
                "User denied permission"
            )
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            andCompareLocations
        } else {
            requestPermissionLauncher.launch(
                permission.ACCESS_FINE_LOCATION
            )
        }
    }// TODO: Display UI based on the locations not matching// TODO: Display UI based on the locations matching// Got last known location. In some rare situations this can be null.

    // Use the computeDistanceBetween function in the Maps SDK for Android Utility Library
    // to use spherical geometry to compute the distance between two Lat/Lng points.
    // TODO: Detect and handle if user has entered or modified the address manually and update
    // the coordinates variable to the Lat/Lng of the manually entered address. May use
    // Geocoding API to convert the manually entered address to a Lat/Lng.
    @get:SuppressLint("MissingPermission")
    private val andCompareLocations: Unit
        private get() {
            // TODO: Detect and handle if user has entered or modified the address manually and update
            // the coordinates variable to the Lat/Lng of the manually entered address. May use
            // Geocoding API to convert the manually entered address to a Lat/Lng.
            val enteredLocation = coordinates
            map!!.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location == null) {
                        return@addOnSuccessListener
                    }
                    deviceLocation = LatLng(location.latitude, location.longitude)
                    Log.d(
                        TAG,
                        "device location = " + deviceLocation.toString()
                    )
                    Log.d(
                        TAG,
                        "entered location = " + enteredLocation.toString()
                    )

                    // Use the computeDistanceBetween function in the Maps SDK for Android Utility Library
                    // to use spherical geometry to compute the distance between two Lat/Lng points.
                    var distanceInMeters: Double =
                        computeDistanceBetween(deviceLocation, enteredLocation)
                    if (distanceInMeters <= acceptedProximity) {
                        Log.d(
                            TAG,
                            "location matched"
                        )
                        // TODO: Display UI based on the locations matching
                    } else {
                        Log.d(
                            TAG,
                            "location not matched"
                        )
                        // TODO: Display UI based on the locations not matching
                    }
                }
        }


    companion object {
        private const val TAG = "ADDRESS_AUTOCOMPLETE"
        private const val DEFAULT_ZOOM = 15
        private const val MAP_FRAGMENT_TAG = "MAP"
        private const val acceptedProximity = 150.0
    }

    /**
     * 사용자가 예상 장소 목록에서 장소를 선택할 수 있는 양식을 표시합니다. .
     */
    private fun openPlacesDialog() {
        // 사용자에게 현재 위치를 선택하도록 요청합니다.
        val listener = DialogInterface.OnClickListener { dialog, which -> // "which" 에는 선택한 항목의 위치가 포함됩니다.
            val markerLatLng = likelyPlaceLatLngs[which]
            var markerSnippet = likelyPlaceAddresses[which]
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
            }

            if (markerLatLng == null) {
                return@OnClickListener
            }

            // 선택한 플레이스에 대한 마커를 추가하고,
            // 해당 플레이스에 대한 정보를 표시하는 정보 창을 표시합니다.
            map?.addMarker(MarkerOptions()
                .title(likelyPlaceNames[which])
                .position(markerLatLng)
                .snippet(markerSnippet))

            // 지도의 카메라를 마커 위치에 배치합니다.
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                DEFAULT_ZOOM.toFloat()))
        }

        // Display the dialog.
        AlertDialog.Builder(this)
            .setTitle(R.string.pick_place)
            .setItems(likelyPlaceNames, listener)
            .show()
    }

}