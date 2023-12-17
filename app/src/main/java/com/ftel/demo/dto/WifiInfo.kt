package com.ftel.demo.dto


import android.net.TransportInfo
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
abstract class WifiInfo :  TransportInfo, Parcelable