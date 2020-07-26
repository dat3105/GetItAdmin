package com.dinhconghien.getitadmin.Screen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.Util.SharePreference_Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    lateinit var imv_background : ImageView
    lateinit var imv_addBrandLap : ImageView
    lateinit var imv_addListItem : ImageView
    lateinit var imv_listItem : ImageView
    lateinit var linear_addBrandLap : RelativeLayout
    lateinit var linear_addListItem : RelativeLayout
    lateinit var linear_ListItem : RelativeLayout
    var dbReference = FirebaseDatabase.getInstance().getReference().child("user")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        initImage(view)

        linear_addBrandLap.setOnClickListener {
            val intent = Intent(view.context,AddBrandLapActivity::class.java)
            startActivity(intent)
        }

        var utils = SharePreference_Utils(view.context)
        var current_userID = utils.getSession()
//        dbReference.keepSynced(true)
        dbReference.child(current_userID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(User::class.java)
                if (user != null){
                    tv_welcome_home.text = "Xin chào,${user.userName}"
                }

            }
        })

        linear_addListItem.setOnClickListener {
            val intent = Intent(view.context,AddNewLapActivity::class.java)
            startActivity(intent)
        }

        linear_ListItem.setOnClickListener {
            val intent = Intent(view.context,ListLapActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun init(view: View){
        imv_background = view.findViewById(R.id.iv_auto_image_slider)
        imv_addBrandLap = view.findViewById(R.id.imv_addBrandLap)
        imv_addListItem = view.findViewById(R.id.imv_addlistItem)
        imv_listItem    = view.findViewById(R.id.imv_listItem)
        linear_addBrandLap = view.findViewById(R.id.linear_addBrand)
        linear_addListItem = view.findViewById(R.id.linear_addListItem)
        linear_ListItem = view.findViewById(R.id.linear_listItem)
    }

    private fun initImage(view : View){
        Glide.with(view.context)
            .load(R.drawable.icon_addbrand)
            .fitCenter()
            .into(imv_addBrandLap)

        Glide.with(view.context)
            .load(R.drawable.icon_add)
            .fitCenter()
            .into(imv_addListItem)

        Glide.with(view.context)
            .load(R.drawable.icon_listitem)
            .fitCenter()
            .into(imv_listItem)

        Glide.with(view.context)
            .load(R.drawable.laptopacer_slider)
            .fitCenter()
            .into(imv_background)
    }

    fun onCLick(view: View){
       if (view == linear_addBrandLap){

       }
       else if(view == linear_addListItem){

       }
   }



}