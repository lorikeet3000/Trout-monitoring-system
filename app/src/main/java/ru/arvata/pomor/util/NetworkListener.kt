package ru.arvata.pomor.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

fun registerNetworkReceiver(context: Context, callback: () -> Unit) {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val builder = NetworkRequest.Builder()
    builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

    val networkRequest = builder.build()

    connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback () {
            override fun onAvailable(network: Network?) {
                super.onAvailable(network)
                callback.invoke()
            }

            override fun onLost(network: Network?) {
                super.onLost(network)
            }
        })
}