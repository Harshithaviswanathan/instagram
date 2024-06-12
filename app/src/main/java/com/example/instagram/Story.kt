package com.example.instagram

import com.google.firebase.firestore.PropertyName

class Story {
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl")
    var imageUrl: String = ""

    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = false

    @get:PropertyName("statusColor") @set:PropertyName("statusColor")
    var statusColor: String = ""

    constructor()

    constructor(imageUrl: String, isActive: Boolean, statusColor: String) {
        this.imageUrl = imageUrl
        this.isActive = isActive
        this.statusColor = statusColor
    }
}