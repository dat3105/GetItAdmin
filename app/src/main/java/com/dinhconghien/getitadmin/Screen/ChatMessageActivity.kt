package com.dinhconghien.getitadmin.Screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Adapter.ChatMessage_Adapter
import com.dinhconghien.getitadmin.MainActivity
import com.dinhconghien.getitadmin.Model.ChatMessage
import com.dinhconghien.getitadmin.Model.RoomChat
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.dinhconghien.getitadmin.Util.SharePreference_Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat_message.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChatMessageActivity : AppCompatActivity() {
    lateinit var adapterMesChat : ChatMessage_Adapter
    val listMesContent = ArrayList<ChatMessage>()
    var userName = ""
    var avaUser = ""
    var avaAdmin = ""
    var idUser = ""
    var idAdmin = ""
    var idRoomChat = ""
    val DB_CHATMESSAGE = FirebaseDatabase.getInstance().getReference("chatMessage")
    val DB_ADMIN = FirebaseDatabase.getInstance().getReference("user")
    val DB_ROOMCHAT = FirebaseDatabase.getInstance().getReference("roomChat")
    var listChatMes = ArrayList<ChatMessage>()
    val TAG_GETLISTCHATMES = "DbError_getListChat_MesContent"
    val TAG_GETADMIN = "DbError_getAdmin_mesContent"
    val TAG_GETROOM = "DbError_getRoom_mesContent"
    var adminName = ""
    var listRoomChat = ArrayList<RoomChat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)
        init()
        updateUI()
        swipeRL_chatMesContent.setOnRefreshListener {
            swipeRL_chatMesContent.isRefreshing = false
            updateUI()
        }
        imv_send_mesContent.setOnClickListener {
            val messageUser = et_chatBox_mesContent.text.toString()
            sendMes(messageUser)
            et_chatBox_mesContent.setText("")
        }

        imv_back_mesContent.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            DB_ROOMCHAT.child(idRoomChat).child("wasSeenAdmin").setValue(false)
            DB_ROOMCHAT.child(idRoomChat).child("avaAdmin").setValue(avaAdmin)
            DB_ROOMCHAT.child(idRoomChat).child("adminName").setValue(adminName)
            startActivity(intent)
            finish()
        }
    }

    private fun init(){
        val utils = SharePreference_Utils(this)
        idAdmin = utils.getSession()
        idUser = intent.getStringExtra("idUser")
        idRoomChat = intent.getStringExtra("idRoomChat")
        adminName = intent.getStringExtra("adminName")
        avaAdmin = intent.getStringExtra("avaAdmin")
    }

    fun updateUI(){
        val dialogLoading = DialogLoading(this)
        dialogLoading.show()
        getUser()
        getMesItem()
        dialogLoading.dismiss()
    }

    private fun sendMes(messageUser : String)
    {
        DB_ROOMCHAT.orderByChild("idRoomChat").equalTo(idRoomChat).addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETROOM,error.toString())
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                listRoomChat.clear()
                getRoomModel(snapshot,messageUser)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRoomModel(snapshot: DataSnapshot,messageUser : String){
        for (param in snapshot.children){
            val roomModel = param.getValue(RoomChat::class.java)!!
            val avaUser = roomModel.avaUser
            val userName = roomModel.userName
            val wasSeenUser = roomModel.wasSeenUser
            val countUnreadUser = roomModel.countUnreadMesUser
            adapterMesChat.setAvaUserNew(avaUser)
            if (wasSeenUser == true){
                setUpMes(0,messageUser,true,avaUser,userName,true)
            }
            else{
                setUpMes(countUnreadUser + 1,messageUser,false,avaUser,userName,false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpMes(countUnreadUser : Int,messageUser :String,wasSeenUser : Boolean,
                         avaUser : String,userName : String,wasReadLastMes : Boolean){
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        var idChatMessage = DB_CHATMESSAGE.push().key.toString()
        var date = current.format(formatter)
        val chatMesModel = ChatMessage(idChatMessage,idRoomChat,messageUser,date,idAdmin,idUser)
        val roomModel = RoomChat(idRoomChat,idUser,idAdmin,true,
            wasSeenUser,avaUser,avaAdmin,0,countUnreadUser
            ,userName,adminName,messageUser,date,idAdmin,wasReadLastMes)
        DB_CHATMESSAGE.child(idChatMessage).setValue(chatMesModel)
        DB_ROOMCHAT.child(idRoomChat).setValue(roomModel)
        getMesItem()
    }

    private fun getMesItem(){
        adapterMesChat = ChatMessage_Adapter(listMesContent,idAdmin,avaUser)
        rcView_mesContent.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)

        rcView_mesContent.adapter = adapterMesChat
        getListChatMes()
    }

    private fun getListChatMes(){
        DB_CHATMESSAGE.orderByChild("idRoomChat").equalTo(idRoomChat).addValueEventListener(object : ValueEventListener{
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETLISTCHATMES,error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                listChatMes.clear()
                getChatMesModel(snapshot)
            }

        })
    }

    private fun getChatMesModel(snapshot: DataSnapshot){
        for (param in snapshot.children){
            val chatMesModel = param.getValue(ChatMessage::class.java)
            if (chatMesModel != null){
                listChatMes.add(chatMesModel)
                adapterMesChat.setListChatMessage(listChatMes)
                rcView_mesContent.scrollToPosition(listChatMes.count() -1)
            }
        }
    }

    private fun getUser(){
        DB_ADMIN.orderByChild("userID").equalTo(idUser).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETADMIN,error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                getUserModel(snapshot)
            }
        })
    }

    private fun getUserModel(snapshot: DataSnapshot){
        for (param in snapshot.children){
            val userModel = param.getValue(User::class.java)
            if (userModel != null){
                userName = userModel.userName
                avaUser = userModel.avaUser
                tv_nameUser_mesContent.text = userName
                DB_ROOMCHAT.child(idRoomChat).child("userName").setValue(userName)
                DB_ROOMCHAT.child(idRoomChat).child("avaUser").setValue(avaUser)
                if (avaUser != ""){
                    Glide.with(this).load(avaUser).fitCenter().into(imv_avaUser_mesContent)
                    adapterMesChat.setAvaUserNew(avaUser)
                }
            }
        }
    }


}