package com.example.altitude_sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.altitude_sensor.ui.theme.Altitude_sensorTheme
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {


    // declare sensor stuff
    private lateinit var sensorManager : SensorManager
    private var pressureSensor : Sensor? = null
    private var _pressure by mutableFloatStateOf(0f)
    // should i calc altiude here too
    private var _altitude by mutableFloatStateOf(0f)
    private var _accuracy by mutableStateOf("Unknown")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize sensor stufff
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) // pressure sensor instead of accelorometer liek in example

        // normal compose stuff
        enableEdgeToEdge()
        setContent {
            Altitude_sensorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    AltitudeScreen(_altitude)
                }
            }
        }
    }

    // functions needed with sensors
    override fun onResume() {
        super.onResume()
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _pressure = it.values[0]
            _altitude = calculateAltitude(_pressure) // update alt here? since on change
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Altitude_sensorTheme {
        Greeting("Android")
    }
}

//
//Build a an app that will calculate altitude changes and display a basic altimeter UI.
//•	Use the pressure sensor
//•	Simulate pressure changes to test and update the screen in real time.
// Also change the background color using darker colors at higher altitudes.
//•	Convert pressure readings using the following formula where h is the altitude
// P0 = 1013.25.

private fun calculateAltitude(pressure: Float): Float {
    val pressure0 = 1013.25f
    val pFrac = (pressure / pressure0).pow(1/5.257f)
    val h = 44330 * (1 - pFrac)
    return h

}



// colors
val lightestGreen = Color(0xFFBBD58e)
val lighterGreen = Color(0xFF5A9F68)
val green = Color(0xFF588157)
val darkerGreen = Color(0xFF3A5A40)
val darkestGreen = Color(0xFF344E41)
@Composable
fun AltitudeScreen(altitude : Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = if(altitude > 12000f) {
                    darkestGreen
                } else if (altitude > 6000f) {
                    darkerGreen
                } else if (altitude > 3000f) {
                    green
                } else if (altitude > 0f) {
                    lighterGreen
                } else {
                    lightestGreen
                }
            ) // since our column is fill ax this wil set entire screen color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "what is the altitude?")
        Text("$altitude")
    }
}
