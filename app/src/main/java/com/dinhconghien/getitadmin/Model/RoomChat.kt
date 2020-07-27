package com.dinhconghien.getitadmin.Model

data class RoomChat(
    var idRoomChat: String = "", //idRoomChat = idUser + idAdmin = keyFirebase
    var idUser: String = "",//ko đổi khi click vào item room chat
    var idAdmin : String = "",//ko đổi khi click vào item room chat
    var wasSeenAdmin : Boolean = false,
    var wasSeenUser : Boolean = false,
    var avaUser : String = "",//ko đổi khi click vào item room chat
    var avaAdmin : String = "",
    var countUnreadMesAdmin : Int = 0,
    var countUnreadMesUser: Int = 0,// đổi khi click vào item room chat
    var userName : String = "", //ko đổi khi click vào item room chat
    var adminName : String = "",
    var lastMessage: String = "",
    var lastDate : String = "",
    var idSenderLastMes : String = "",
    var wasReadLastMes : Boolean = false//nhớ đổi khi click vào item roomchat nếu idSenderLastMes = idAdmin
) {
}