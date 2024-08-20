package com.domagojleskovic.bleadvert

data class Beacon(
    val id : String = "",
    val address: String = "",
    var url: String = "",
    val x: Double = 0.0,
    val y: Double = 0.0,
    var distances: MutableList<Double> = mutableListOf(),
    val maximumAdvertisementDistance: Double = 1.25
)



