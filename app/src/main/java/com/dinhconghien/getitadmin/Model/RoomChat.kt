package com.dinhconghien.getitadmin.Model

data class RoomChat(
    var idRoomChat: String = "", //idRoomChat = idUser + idAdmin = keyFirebase
    var idUser: String = "",
    var idAdmin : String = "",
    var wasSeenAdmin : Boolean = false,
    var wasSeenUser : Boolean = false,
    var avaUser : String = "",
    var avaAdmin : String = "",
    var wasOnlineAdmin : Boolean = false,
    var wasOnlineUser : Boolean = false,
    var listUnreadMessageAdmin : ArrayList<ChatMessage> = ArrayList(),
    var listUnreadMessageUser : ArrayList<ChatMessage> = ArrayList(),
    var userName : String = "",
    var adminName : String = "",
    var lastMessage: String = "",
    var lastDate : String = "",
    var idSenderLastMes : String = "",
    var wasReadLastMes : Boolean = false
) {
}