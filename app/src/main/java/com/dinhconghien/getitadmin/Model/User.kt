package com.dinhconghien.getitadmin.Model

data class User(
    var userID : String = "",
    var email : String ="",
    var userName : String ="",
    var phone : String ="",
    var password : String ="",
    var role : String = "Admin",
    var wasOnline : Boolean = false,
    var avaUser : String =""
) {
}