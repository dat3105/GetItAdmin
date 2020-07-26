package com.dinhconghien.getitadmin.Screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinhconghien.getitadmin.Adapter.ListLaptop_Adapter
import com.dinhconghien.getitadmin.Adapter.SpinnerBrandLap_Adapter
import com.dinhconghien.getitadmin.MainActivity
import com.dinhconghien.getitadmin.Model.BrandLap
import com.dinhconghien.getitadmin.Model.Laptop
import com.dinhconghien.getitadmin.Model.LaptopDetail
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.CustomToast
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_list_lap.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ListLapActivity : AppCompatActivity() {

    val DB_BRANDLAP = FirebaseDatabase.getInstance().getReference("brandLap")
    val DB_LAPTOP = FirebaseDatabase.getInstance().getReference("laptop")
    val DB_LAPDETAIL = FirebaseDatabase.getInstance().getReference("laptopDetail")
    lateinit var listLap: ArrayList<Laptop>
    lateinit var adapterListLap: ListLaptop_Adapter
    var listBrandLap = ArrayList<BrandLap>()
    var idBrandLap = ""
    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_lap)
        setSupportActionBar(toolbar_listLapScreen)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listLap = ArrayList()
        job = Job()

        toolbar_listLapScreen.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val adapterSpinBrand = SpinnerBrandLap_Adapter(this, listBrandLap)
        adapterListLap = ListLaptop_Adapter(this, listLap)
        GlobalScope.launch(Dispatchers.IO) {
            DB_BRANDLAP.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("DbError", error.toString())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listBrandLap.clear()
                    getBrand(snapshot)
                    adapterSpinBrand.addList(listBrandLap)
                }
            })
        }//fetch data from firebase

        spinner_listLapScreen.adapter = adapterSpinBrand


        spinner_listLapScreen.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                idBrandLap =
                    listBrandLap.get(spinner_listLapScreen.getSelectedItemPosition()).idBrandLap!!
                updateUI()
            }
        }
        updateUI() //init UI when start this activity in the first time

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        swipeRL_listLapScreen.setOnRefreshListener {
            swipeRL_listLapScreen.isRefreshing = false
            updateUI()
        }
    }

    fun updateUI() {
        val dialogLoading = DialogLoading(this)
        dialogLoading.show()
        DB_LAPTOP.orderByChild("idBrandLap").equalTo(idBrandLap)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("DbErrorListLap", "$error")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listLap.clear()
                    getLap(snapshot)
                    adapterListLap.setIDBrandLap(listLap)
                }
            })
        rcView_listLap.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcView_listLap.adapter = adapterListLap
        adapterListLap.setOnItemClickedListener(object : ListLaptop_Adapter.OnItemClickedListener {
            override fun onClicked(position: Int, typeFunction: Boolean) {
                if (typeFunction == true) {
                    try {
                        //delete laptop
                        var idLapIsClicked = listLap[position].idLap
                        DB_LAPTOP.child(idLapIsClicked).removeValue()
                        adapterListLap.setIDBrandLap(listLap)

                        //delete laptop detail
                        DB_LAPDETAIL.orderByChild("idLap").equalTo(idLapIsClicked)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("DbErrorDeleteLapItem", "$error")
                                }
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (param in snapshot.children) {
                                        var lapDetailModel =
                                            param.getValue(LaptopDetail::class.java)
                                        var listLapDetail = ArrayList<LaptopDetail>()
                                        lapDetailModel?.let { listLapDetail.add(it) }
                                        var idLapDetails = lapDetailModel?.idLapDetail
                                        idLapDetails?.let { DB_LAPDETAIL.child(it).removeValue() }
                                    }
                                }
                            })
                        CustomToast.makeText(
                            this@ListLapActivity,
                            "Xóa Laptop ${listLap.get(position).nameLap} thành công",
                            Toast.LENGTH_LONG, 1)?.show()
                    } catch (e: Exception) {
                        Log.d("DbErrorOnClickLapItem", "$e")
                    }
                }
            }
        })
        dialogLoading.dismiss()
    }

    fun getBrand(snapShot: DataSnapshot) {
        for (param in snapShot.children) {
            var brandModel = param.getValue(BrandLap::class.java)!!
            listBrandLap.add(brandModel)
        }
    }

    private fun getLap(snapshot: DataSnapshot) {
        for (param in snapshot.children) {
            val laptopModel = param.getValue(Laptop::class.java)!!
            listLap.add(laptopModel)
        }
    }

}