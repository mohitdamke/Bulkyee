package com.example.bulkyee

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bulkyee.navigation.NavigationControl
import com.example.bulkyee.ui.theme.BulkyeeTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) { }
        setContent {
            BulkyeeTheme {
                NavigationControl()
            }
        }
    }
}


fun createNotificationChannel(context: Context) {
    val name = "Bulkyee"
    val descriptionText = "This is bulkyee app"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("Bulkyee App", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun showOrderNotification(context: Context) {
    val notificationManager = NotificationManagerCompat.from(context)
    val builder = NotificationCompat.Builder(context, "Bulkyee App")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Order Placed")
        .setContentText("Your order has been placed successfully!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    notificationManager.notify(1, builder.build())

}
