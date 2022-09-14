package com.example.pushnotify

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.renderscript.ScriptGroup
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.pushnotify.auth.TokenBroadcastReceiver
import com.example.pushnotify.databinding.ActivityMainBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings



/**
 * 1 Firebase Console:
 *  1.1 Register the application
 *  1.2 google.json file (save it in our app folder)
 *  1.3 Services to handle incoming messages
 *  1.4 Notification Manager
 */
/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {
    // 1.create object
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth

    private var _binding: emailPassword? = null
    private val binding: emailPassword
        get() = _binding!!

    val user = Firebase.auth.currentUser
    var customToken: String? = null
    private lateinit var tokenReciever: TokenBroadcastReceiver

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to AppTheme for displaying the activity
        //setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up the button to change config value
        var button = findViewById<Button>(R.id.bthFetch)
        button.setOnClickListener(){
            fetchRemoteConfigValues()
        }
        remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        // assign default value

        remoteConfig.setDefaultsAsync(R.xml.remote_config_default)

        // obtain instance of firebaseAnalytics
        analytics= Firebase.analytics
        var bundle = Bundle()
        bundle.putString("APP_LAUNCHED", Telephony.CarrierId.CARRIER_NAME)
        analytics.logEvent("APP_Launched",bundle)
        /**
         *  firebase auth
         */

        // initialize firebase auth
        auth = Firebase.auth
        // Get token
        if (checkGooglePlayServices()) {
            // [START retrieve_current_token]
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, getString(R.string.token_error), task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result
                    Log.d(TAG, token)

                    // Log and toast
                    val msg = getString(R.string.token_prefix, token)
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                })
            // [END retrieve_current_token]
        } else {
            //You won't be able to send notifications to this device
            Log.w(TAG, "Device doesn't have google play services")
        }
    }

    /**
     * get values from firebase remote config
     */
    fun fetchRemoteConfigValues(){
        // fetch and apply updates
        remoteConfig.fetchAndActivate().addOnCompleteListener(this){
            // successful ( if we successfully connect to firebase server )
            if(it.isSuccessful){

                val updated = it.result
                Log.i("remote config","$updated")
                    val bg_color: String = remoteConfig.getString("backgroundcolor")
                var layout = findViewById<ConstraintLayout>(R.id.layout)
                layout.setBackgroundColor(Color.parseColor(bg_color))
            }else{
                //error
            }
        }
    }


    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error")
            // ask user to update google play services.
            false
        } else {
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    public override fun onStart() {
        super.onStart()
        // check if user is sign in(non-null) and update UI

        val currentUser = auth.currentUser
            if(currentUser!= null){
            //    reload();

            }
    }

    /**
     * email and password auth
     */




}
