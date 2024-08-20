package com.domagojleskovic.bleadvert

import com.google.firebase.firestore.DocumentSnapshot

data class UserEventProgress (
    var scans : Int = 0,
    val unlockedRewards : MutableList<Reward> = mutableListOf(),
    val scannedBeaconAddresses : MutableList<String> = mutableListOf()
){
    companion object {
        fun parseFrom(document: DocumentSnapshot) : UserEventProgress? {
            val scans = document.getLong("scans")?.toInt() ?: 0
            val unlockedRewards = Reward.parseFrom(document)
            val scannedBeaconAddresses = document.get("scannedBeaconAddresses") as? List<String> ?: emptyList()

            if(scannedBeaconAddresses.isEmpty() && unlockedRewards.isEmpty() && scans == 0) return null
            return UserEventProgress(
                scans = scans,
                unlockedRewards = unlockedRewards,
                scannedBeaconAddresses = scannedBeaconAddresses.toMutableList()
            )
        }
    }
}
