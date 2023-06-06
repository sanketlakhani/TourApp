package com.example.masterprojectapp.models

class UserModel {

    var name: String = ""
    var email: String = ""
    var number: String = ""
    var address: String = ""
    var paymentId: String = ""
    var personNumber: String = ""
    var date: String = ""

    //    var note: String = ""
    var gender: String = ""
    var key: String = ""

    constructor(
        nameE: String,
        emailE: String,
        numberE: String,
        addressE: String,
        paymentIdE: String,
        personNumberE: String,
        dateE: String,
//        noteE: String,
        genderE: String,
        keyE: String
    ) {
        this.name = nameE
        this.email = emailE
        this.number = numberE
        this.address = addressE
        this.paymentId = paymentIdE
        this.personNumber = personNumberE
        this.date = dateE
//        this.noteU = noteE
        this.gender = genderE
        this.key = keyE
    }

    constructor() {

    }
}