package com.dinhconghien.getitadmin.Screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Adapter.ListLapPayment_Adapter
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
import kotlinx.android.synthetic.main.activity_invoice_wating_detail_.*
import kotlinx.coroutines.*
import java.lang.Exception

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class InvoiceWatingDetail_Activity : AppCompatActivity() {
    lateinit var idUser : String
    lateinit var idBill : String
    lateinit var date : String
    lateinit var sumPrice : String
    lateinit var status : String
    var idAdmin = ""
    lateinit var addressOrder : String
    var listLapOrder = ArrayList<Laptop>()
    val DB_BILL =  FirebaseDatabase.getInstance().getReference("bill")
    val DB_USER =  FirebaseDatabase.getInstance().getReference("user")
    val DB_LAP =  FirebaseDatabase.getInstance().getReference("laptop")
    private lateinit var adapterLapPayment: ListLapPayment_Adapter
    val TAG_GETUSER = "DbError_getUser_InvoiceWatingDetail"
    val TAG_GETLAPPAYMENT = "DbError_getLapCart_InvoiceWatingDetail"
    lateinit var job: Job
//    var onConfirmBill = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_wating_detail_)
        setSupportActionBar(toolbar_invoiceWatingDetail)
        GlobalScope.launch(Dispatchers.Main) { updateUI() }
        swipeRL_billWatingScreen.setOnRefreshListener {
            swipeRL_billWatingScreen.isRefreshing = false
            GlobalScope.launch(Dispatchers.Main) { updateUI() }
        }

        btn_cancel_invoiceWatingDetail.setOnClickListener {
            DB_BILL.child(idBill).child("status").setValue("Đã hủy")
            DB_BILL.child(idBill).child("idPersonCancel").setValue(idAdmin)
            CustomToast.makeText(this,"Đơn hàng đã bị hủy", Toast.LENGTH_LONG,1)?.show()
            val intet = Intent(this, MainActivity::class.java)
            startActivity(intet)
            finish()
        }//onClick cancel

        toolbar_invoiceWatingDetail.setNavigationOnClickListener {
            val intet = Intent(this,MainActivity::class.java)
            startActivity(intet)
            finish()
        }//return main

        imv_phone_billWating.setOnClickListener {

           val phone = tv_phoneNumber_billWatingDetai.text.toString()
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$phone")
            startActivity(callIntent)
        }//onClick call user

        btn_accept_invoiceWatingDetail.setOnClickListener {
                val dialogLoading = DialogLoading(this)
                dialogLoading.show()
                try {
                    GlobalScope.launch(Dispatchers.IO) {  setUpConfirmBill() }
                    DB_BILL.child(idBill).child("status").setValue("Đã xác nhận")
                    CustomToast.makeText(this,"Hóa đơn đã xác nhận thành công",Toast.LENGTH_LONG,1)
                        ?.show()
                    dialogLoading.dismiss()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }catch (e : Exception){
                    CustomToast.makeText(this,e.toString(),Toast.LENGTH_LONG,1)
                        ?.show()
                    dialogLoading.dismiss()
                }

        } //onClick confirm

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
        setUpUserUI()
        getListLapItem()
        dialogLoading.dismiss()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        val utils = SharePreference_Utils(this)
        idAdmin = utils.getSession()
        idUser = intent.getStringExtra("idUser")
        idBill = intent.getStringExtra("idBill")
        date =  intent.getStringExtra("date")
        sumPrice =  intent.getStringExtra("sumPrice")
        status = intent.getStringExtra("status")
        addressOrder = intent.getStringExtra("addressOrder")
        job = Job()
        tv_idInvoiceWatingDetail.text = idBill
        tv_address_billWatingDetai.text = addressOrder
        tv_status_invoiceWatingDetail.text = status
        tv_timeOrder_invoiceWatingDetail.text = date
        tv_sumPrice_invoiceWatingDetail.text = sumPrice
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
                tv_userName_billWatingDetai.text = userModel.userName
                tv_phoneNumber_billWatingDetai.text = userModel.phone
                if (userModel.avaUser != ""){
                    Glide.with(this).load(userModel.avaUser).fitCenter().into(imv_avaUser_billWatingDetail)
                }
            }
        }
    }

    private fun setUpConfirmBill(){
        for (i in 0 until listLapOrder.size){
                val idLap = listLapOrder[i].idLap
                val quantity = listLapOrder[i].quantity
                val amountInCart = listLapOrder[i].amountInCart
                val amountSell =  listLapOrder[i].amountSell
                val quantityLapAfterConfirm = quantity - amountInCart
                DB_LAP.child(idLap).child("quantity").setValue(quantityLapAfterConfirm)
                DB_LAP.child(idLap).child("amountSell").setValue(amountSell+amountInCart)
        }
    }

    private fun getListLapItem(){
        adapterLapPayment = ListLapPayment_Adapter(listLapOrder)
        rcView_listLapWating.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        rcView_listLapWating.adapter = adapterLapPayment
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
                adapterLapPayment.setListLapPaymentNew(listLapOrder)
            }
        }
    }
}