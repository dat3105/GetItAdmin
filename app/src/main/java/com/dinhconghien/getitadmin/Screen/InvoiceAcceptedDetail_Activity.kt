package com.dinhconghien.getitadmin.Screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Adapter.BillAccepted_Adapter
import com.dinhconghien.getitadmin.MainActivity
import com.dinhconghien.getitadmin.Model.Bill
import com.dinhconghien.getitadmin.Model.Laptop
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.CustomToast
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.dinhconghien.getitadmin.Util.SharePreference_Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_invoice_accepted_detail_.*
import kotlinx.coroutines.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class InvoiceAcceptedDetail_Activity : AppCompatActivity() {

    lateinit var idUser : String
    lateinit var idBill : String
    lateinit var sumPrice : String
    lateinit var addressOrder : String
    var avaUser = ""
    var listLapOrder = ArrayList<Laptop>()
    val DB_BILL =  FirebaseDatabase.getInstance().getReference("bill")
    val DB_USER =  FirebaseDatabase.getInstance().getReference("user")
    private lateinit var adapterLapPayment: BillAccepted_Adapter
    val TAG_GETUSER = "DbError_getUser_InvoiceWatingDetail"
    val TAG_GETLAPPAYMENT = "DbError_getLapCart_InvoiceWatingDetail"
    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_accepted_detail_)
        GlobalScope.launch(Dispatchers.Main) { updateUI() }
        swipeRL_billAcceptedDetail.setOnRefreshListener {
            swipeRL_billAcceptedDetail.isRefreshing = false
            GlobalScope.launch(Dispatchers.Main) { updateUI() }
        }
        toolbar_invoiceAccepted.setNavigationOnClickListener {
            val intet = Intent(this, MainActivity::class.java)
            startActivity(intet)
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    suspend fun updateUI(){
        val dialogLoading = DialogLoading(this)
        dialogLoading.show()
        delay(500L)
        init()
//        setUpUserUI()
        getListLapItem()
        dialogLoading.dismiss()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        idUser = intent.getStringExtra("idUser")
        idBill = intent.getStringExtra("idBill")
        sumPrice =  intent.getStringExtra("sumPrice")
        addressOrder = intent.getStringExtra("addressOrder")
        job = Job()
        tv_idInvoice_Accepted.text = "Mã hóa đơn : $idBill"
        tv_address_billAcceptedDetail.text = addressOrder
        tv_sumPrice_billAcceptedDetail.text = sumPrice
    }

    private fun setUpUserUI(){
        DB_USER.orderByChild("userID").equalTo(idUser).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETUSER,error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                getUserModel(snapshot)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun getUserModel(snapshot: DataSnapshot){
        for (param in snapshot.children){
            val userModel = param.getValue(User::class.java)
            if (userModel != null){
                avaUser = userModel.avaUser
                tv_userName_billAcceptedDetail.text = userModel.userName
                tv_phoneNumber_billAcceptedDetail.text = userModel.phone
                if (userModel.avaUser != ""){
                    Glide.with(this).load(userModel.avaUser).fitCenter().into(imv_avaUser_billAcceptedDetail)
                }
            }
        }
    }

    private fun getListLapItem(){
        adapterLapPayment = BillAccepted_Adapter(listLapOrder)
        rcView_invoiceAcceptedScreen.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        rcView_invoiceAcceptedScreen.adapter = adapterLapPayment
        setListLapOrder()
    }

    private fun setListLapOrder(){
        DB_BILL.orderByChild("idBill").equalTo(idBill).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETLAPPAYMENT,error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                listLapOrder.clear()
                getListLapModel(snapshot)
            }
        })
    }

    private fun getListLapModel(snapshot: DataSnapshot){
        for (param in snapshot.children){
            val billModel = param.getValue(Bill::class.java)
            if (billModel != null){
                listLapOrder = billModel.listLapOrder
                adapterLapPayment.setListBillAccep(listLapOrder)
            }
        }
    }
}