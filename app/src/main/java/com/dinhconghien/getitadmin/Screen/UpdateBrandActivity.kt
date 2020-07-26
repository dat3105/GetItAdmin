package com.dinhconghien.getitadmin.Screen

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.MainActivity
import com.dinhconghien.getitadmin.Model.BrandLap
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.CustomToast
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_brand_lap.*
import kotlinx.android.synthetic.main.activity_update_brand.*
import kotlinx.android.synthetic.main.activity_update_lap.*
import kotlinx.coroutines.*

class UpdateBrandActivity : AppCompatActivity() {
    val IMAGE_PICK_CODE =1000
    val PERMISSION_CODE = 1001
    var uri : Uri = Uri.parse("android.resource://com.dinhconghien.getitadmin/"+ R.drawable.acer)
    lateinit var BRANDLAP_STORE : StorageReference
     var listBrandLap = ArrayList<BrandLap>()
    var noti = ""
    var DB_BRANLAP = FirebaseDatabase.getInstance().getReference("brandLap")
    var avaUser =""
    var idBrandLap = ""
    lateinit var job : Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_brand)
        setSupportActionBar(toolbar_updateBrand)
        idBrandLap = intent.getStringExtra("idBrandLap")
        BRANDLAP_STORE = FirebaseStorage.getInstance().getReference("brandLap")
        job = Job()
        updateUI()
        toolbar_updateBrand.setNavigationOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        GlobalScope.launch(Dispatchers.IO) {
            DB_BRANLAP.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("DbError", error.toString())
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    listBrandLap.clear()
                    getBrandModel(snapshot)
                }

            })
        } //fetch data from firebase

        btn_updateImageBrand.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    imageFromGallery()
                }
            }
            else{
                imageFromGallery()
            }
        }

        btn_confirm_updateBrandScreen.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                updateBrand()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun updateUI(){
        val dialogLoading = DialogLoading(this)
        dialogLoading.show()
        getBrand()
        dialogLoading.dismiss()
    }

    fun checkInfo(brandName: String): String {
        var noti = "Thêm mới Loại Lap thành công"
        for (i in 0 until listBrandLap.size) {
            if (listBrandLap[i].nameBrand.equals(brandName)) {
                noti = "Tên thể loại đã tồn tại"
            }
        }
        return noti
    }

    fun getBrandModel(snapShot: DataSnapshot) {
        for (param in snapShot.children) {
            var brandModel = param.getValue(BrandLap::class.java)!!
            listBrandLap.add(brandModel)
        }
    }

    private fun getBrand(){
        DB_BRANLAP.orderByChild("idBrandLap").equalTo(idBrandLap).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d("DbErrorUpdateBrand",error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
               for (param in snapshot.children){
                   val brandModel = param.getValue(BrandLap::class.java)!!
                   ed_nameBrand_updateBrand.setText(brandModel.nameBrand)
                   Glide.with(this@UpdateBrandActivity)
                       .load(brandModel.avaBrandLap)
                       .fitCenter()
                       .into(imv_avaLap_updateBrandScreen)
               }
            }

        })
    }

    suspend fun updateBrand(){
        var brandName = ed_nameBrand_updateBrand.text.toString()
        checkValidateForm(brandName)
        val checkForm =   checkValidateForm(brandName)
        if (checkForm == true){
            val dialogLoading = DialogLoading(this)
            dialogLoading.show()
            withContext(Dispatchers.IO){
                noti = async { checkInfo(brandName) }.await()
            }
            if (noti == "Tên thể loại đã tồn tại")
            {
                CustomToast.makeText(this, noti, Toast.LENGTH_LONG, 3)!!.show()
                dialogLoading.dismiss()
            }
            else
            {
                var store : StorageReference = BRANDLAP_STORE.child(idBrandLap)
                store.putFile(uri).addOnCompleteListener{
                    store.downloadUrl.addOnSuccessListener {
                            uri -> avaUser = uri.toString()
                        val brandModel = BrandLap(idBrandLap,brandName,avaUser)
                        DB_BRANLAP.child(idBrandLap).setValue(brandModel)
                        CustomToast.makeText(this, noti, Toast.LENGTH_LONG, 1)!!.show()
                        dialogLoading.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener{it ->
                    CustomToast.makeText(this, it.toString(), Toast.LENGTH_LONG, 3)!!.show()
                    dialogLoading.dismiss()
                }
            }
        }
    }

    private fun checkValidateForm(
        brandName: String

    ) : Boolean  {
        if (TextUtils.isEmpty(brandName))
        {
            CustomToast.makeText(this, "Không được để trống 'Thể loại'", Toast.LENGTH_LONG, 2)!!.show()
            return false
        }else if (brandName.contains(" ")){
            CustomToast.makeText(this, "Không được để khoảng trắng ở 'Thể loại'", Toast.LENGTH_LONG, 2)!!.show()
            return false
        }
        return true
    }

    private fun imageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    imageFromGallery()
                } else {
                    Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uri = data?.data!!
            Glide.with(this).load(uri).placeholder(R.drawable.acer).into(imv_avaLap_updateBrandScreen)
        }
    }
}