package com.dinhconghien.getitadmin.Screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dinhconghien.getitadmin.Adapter.RoomChat_Adapter
import com.dinhconghien.getitadmin.Model.RoomChat
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.Util.SharePreference_Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RoomChatFragment : Fragment() {
    lateinit var swipeRL : SwipeRefreshLayout
    lateinit var rcView_roomChat : RecyclerView
    val DB_ROOMCHAT = FirebaseDatabase.getInstance().getReference("roomChat")
    lateinit var adapterRoomChat : RoomChat_Adapter
    var listRoomChat = ArrayList<RoomChat>()
    lateinit var idAdmin : String
    val TAG_GETLISTROOM = "DbError_getListRoomChat_RoomChatFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view =  inflater.inflate(R.layout.fragment_room_chat, container, false)
        anhXa(view)
        setUpRoomAdapter(view)
        swipeRL.setOnRefreshListener {
            swipeRL.isRefreshing = false
            setUpRoomAdapter(view)
        }
        return view
    }

    private fun anhXa(view: View){
        swipeRL = view.findViewById(R.id.swipeRL_roomChatFrag)
        rcView_roomChat = view.findViewById(R.id.rcView_roomChatFrag)
        val utils = SharePreference_Utils(view.context)
        idAdmin = utils.getSession()
    }

    private fun setUpRoomAdapter(view: View){
        adapterRoomChat = RoomChat_Adapter(view.context,listRoomChat,idAdmin)
        rcView_roomChat.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rcView_roomChat.setHasFixedSize(true)
        rcView_roomChat.adapter = adapterRoomChat
        getListRoomChat()
        adapterRoomChat.setOnItemClickedListener(object : RoomChat_Adapter.OnItemClickedListener{
            override fun onClicked(position: Int) {
                val idRoomChat = listRoomChat[position].idRoomChat
                DB_ROOMCHAT.child(idRoomChat).child("wasSeenAdmin").setValue(true)
                DB_ROOMCHAT.child(idRoomChat).child("countUnreadMesAdmin").setValue(0)
            }

        })
    }

    private fun getListRoomChat(){
        DB_ROOMCHAT.orderByChild("idAdmin").equalTo(idAdmin).addValueEventListener(object :
            ValueEventListener {
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETLISTROOM,error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                listRoomChat.clear()
                getRoomModel(snapshot)
            }
        })
    }

    private fun getRoomModel(snapshot: DataSnapshot){
        for (param in snapshot.children){
            val roomCHatModel = param.getValue(RoomChat::class.java)
            if (roomCHatModel != null){
                listRoomChat.add(roomCHatModel)
                adapterRoomChat.setListRoomChatNew(listRoomChat)
            }
        }
    }


}