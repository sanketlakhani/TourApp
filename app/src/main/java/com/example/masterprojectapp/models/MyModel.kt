package com.example.masterprojectapp.models


 class MyModel {

    var packageName: String = ""
    var description: String = ""
    var days: String = ""
    var location: String = ""
    var price: String = ""
    var key: String = ""

    constructor(
        packageNameE: String,
        descriptionE: String,
        daysE: String,
        locationE: String,
        priceE: String,
        keyE: String,

    ) {
        this.packageName = packageNameE
        this.description = descriptionE
        this.days = daysE
        this.location = locationE
        this.price = priceE
        this.key = keyE
    }

    constructor() {

    }
}