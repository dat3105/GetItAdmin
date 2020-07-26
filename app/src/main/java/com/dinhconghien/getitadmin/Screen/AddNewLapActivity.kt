package com.dinhconghien.getitadmin.Screen

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Adapter.SpinnerBrandLap_Adapter
import com.dinhconghien.getitadmin.MainActivity
import com.dinhconghien.getitadmin.Model.BrandLap
import com.dinhconghien.getitadmin.Model.Laptop
import com.dinhconghien.getitadmin.Model.LaptopDetail
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.CustomToast
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_brand_lap.*
import kotlinx.android.synthetic.main.activity_add_new_lap.*
import kotlinx.coroutines.*

class AddNewLapActivity : AppCompatActivity() {

    val DB_BRANDLAP = FirebaseDatabase.getInstance().getReference("brandLap")
    val DB_LAPTOP = FirebaseDatabase.getInstance().getReference("laptop")
    val DB_LAPTOPDETAIL = FirebaseDatabase.getInstance().getReference("laptopDetail")
    var listBrand = ArrayList<BrandLap>()
    lateinit var listLap : ArrayList<Laptop>

    lateinit var job: Job
    var idBrandLap = ""
    val durationToast = Toast.LENGTH_LONG
    val IMAGE_PICK_CODE =1000
    val PERMISSION_CODE = 1001
    var uri : Uri = Uri.parse("android.resource://com.dinhconghien.getitadmin/"+ R.drawable.acer)
    lateinit var LAPTOP_STORE : StorageReference
    var noti = ""
    var avaLap =""
    var idNameBrand = ""
    var nameBrand = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_lap)
        setSupportActionBar(toolbar_addNewLap)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listLap = ArrayList()
        toolbar_addNewLap.setNavigationOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        job = Job()
        val adapterSpinBrand = SpinnerBrandLap_Adapter(this, listBrand)
        LAPTOP_STORE = FirebaseStorage.getInstance().getReference("laptop")
        GlobalScope.launch(Dispatchers.IO) {
                DB_BRANDLAP.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("DbError", error.toString())
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listBrand.clear()
                        getBrandLap(snapshot)
                        adapterSpinBrand.addList(listBrand)
                        //Because at first time listBrand is empty and it need to refresh
                        // after received data from firebase. So, it need to call notifyDataSetChanged() to update data
                    }
                })
        }//fetch data from firebase
        spinner_BrandLap.adapter = adapterSpinBrand

        btn_addImageLap_addNewLap.setOnClickListener {
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



    }

    override fun onResume() {
        super.onResume()
        spinner_BrandLap.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                idBrandLap = listBrand.get(spinner_BrandLap.getSelectedItemPosition()).idBrandLap!!
                idNameBrand = listBrand.get(spinner_BrandLap.getSelectedItemPosition()).nameBrand!!
                nameBrand = listBrand.get(spinner_BrandLap.selectedItemPosition).nameBrand!!
            }
        }

        btn_confirm_addNewLap.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                addNewLap()
            }
        }
    }

    fun checkInfo(nameLap: String): String {
        var noti = "Thêm mới Laptop thành công"
        for (i in 0 until listLap.size) {
                if (listLap[i].nameLap.equals(nameLap)) {
                    noti = "Tên laptop đã tồn tại"
                }
        }
        return noti
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun getBrandLap(snapShot: DataSnapshot) {
        for (param in snapShot.children) {
            var brandModel = param.getValue(BrandLap::class.java)!!
            listBrand.add(brandModel)
        }
    }

    private fun getLap(snapShot: DataSnapshot){
        for (param in snapShot.children){
                var laptopModel = param.getValue(Laptop::class.java)!!
                listLap.add(laptopModel)
        }
    }



    suspend fun addNewLap(){
        val nameLap = ed_nameLap_addNewLap.text.toString()
        val priceLap = ed_priceLap_addNewLap.text.toString()
        val quantityLap = ed_quantityLap_addNewLap.text.toString()
        val congNgheCPU = ed_cpuTech_addNewLap.text.toString()
        val tocDoCPU = ed_speedCPU_addNewLap.text.toString()
        val hangCPU = ed_hangCPU_addNewLap.text.toString()
        val boNhoDem = ed_boNhoDem_addNewLap.text.toString()
        val dungLuongRAM = ed_dungLuongRAM_addNewLap.text.toString()
        val loaiRAM = ed_loaiRAM_addNewLap.text.toString()
        val tocDoBus = ed_tocDoBUS_addNewLap.text.toString()
        val soLuongRAM = ed_soLuongRAM_addNewLap.text.toString()
        val xuatXu = ed_xuatXu_addNewLap.text.toString()
        val namSanXuat = ed_namSanXuat_addNewLap.text.toString()

        checkValidateForm(nameLap,congNgheCPU,tocDoCPU,hangCPU,boNhoDem
            ,dungLuongRAM,loaiRAM,tocDoBus,soLuongRAM,xuatXu,namSanXuat,priceLap,quantityLap)
        val checkForm =   checkValidateForm(nameLap,congNgheCPU,tocDoCPU,hangCPU,boNhoDem,dungLuongRAM
            ,loaiRAM,tocDoBus,soLuongRAM,xuatXu,namSanXuat,priceLap,quantityLap)
        if (checkForm == true){
            GlobalScope.launch(Dispatchers.IO){
                DB_LAPTOP.orderByChild("idBrandLap").equalTo(idBrandLap).addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("DbErrorAddLap","$error")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listLap.clear()
                        getLap(snapshot)
                    }
                })
            }
            val dialogLoading = DialogLoading(this)
            dialogLoading.show()
            withContext(Dispatchers.IO){
                noti = async { checkInfo(nameLap) }.await()
            }
            if (noti == "Tên laptop đã tồn tại"){
                CustomToast.makeText(this, noti, durationToast, 3)?.show()
                dialogLoading.dismiss()
            }
            else{
                val idLaptop = DB_LAPTOP.push().getKey().toString()
                val idLapDetail = DB_LAPTOPDETAIL.push().key.toString()
                var store : StorageReference = LAPTOP_STORE.child(idLaptop)
                store.putFile(uri).addOnFailureListener {
                    CustomToast.makeText(this,it.toString(), durationToast, 3)?.show()
                    dialogLoading.dismiss()
                }
                    .addOnCompleteListener {
                        store.downloadUrl.addOnSuccessListener {
                            //add laptop
                            uri ->  avaLap = uri.toString()
                            val lapModel = Laptop(idLaptop,idBrandLap,nameLap,priceLap.toInt()
                                ,quantityLap.toInt(),avaLap,0,0,0,nameBrand)
                            DB_LAPTOP.child(idLaptop).setValue(lapModel)
                            // add laptop detail
                            val laptopDetail = LaptopDetail(idLapDetail,idLaptop,congNgheCPU,tocDoCPU,
                                hangCPU,boNhoDem,dungLuongRAM,loaiRAM,tocDoBus,soLuongRAM,xuatXu,namSanXuat)
                            DB_LAPTOPDETAIL.child(idLapDetail).setValue(laptopDetail)
                            CustomToast.makeText(this,noti, durationToast, 1)?.show()
                            dialogLoading.dismiss()
                        }.addOnFailureListener{
                            CustomToast.makeText(this,it.toString(), durationToast, 1)?.show()
                            dialogLoading.dismiss()
                        }
                    }
            }
        }
    }

    fun checkValidateForm(
        nameLap: String, congNgheCPU: String, tocDoCPU: String, hangCPU: String, boNhoDem: String,
        dungLuongRAM: String, loaiRAM: String, tocDoBus: String,
        soLuongRAM: String, xuatXu: String, namSanXuat: String , priceLap : String , quantity : String
    ): Boolean {
        if (nameLap.equals("") || congNgheCPU.equals("") || tocDoCPU.equals("") || hangCPU.equals(
                ""
            )
            || boNhoDem.equals("") || dungLuongRAM.equals("") || loaiRAM.equals("") || tocDoBus.equals(
                ""
            )
            || soLuongRAM.equals("") || xuatXu.equals("") || namSanXuat.equals("")
        ) {
            CustomToast.makeText(this, "Không được để trống thông tin", durationToast, 2)?.show()
            return false
        } else if (nameLap.contains(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở 'Tên laptop'",
                durationToast,
                2
            )
                ?.show()
            return false
        } else if (boNhoDem.contains(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở 'Bộ nhớ đệm'",
                durationToast,
                2
            )
                ?.show()
            return false
        } else if (dungLuongRAM.startsWith(" ") || dungLuongRAM.endsWith(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở đầu hoặc cuối trong 'Dung lượng RAM'",
                durationToast,
                2
            )
                ?.show()
            return false
        } else if (loaiRAM.contains(" ")) {
            CustomToast.makeText(this, "Không được để khoảng trắng ở 'Loại RAM'", durationToast, 2)
                ?.show()
            return false
        } else if (tocDoBus.contains(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở 'Tốc độ BUS'",
                durationToast,
                2
            )
                ?.show()
            return false
        } else if (soLuongRAM.contains(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở 'Số lượng RAM'",
                durationToast,
                2
            )
                ?.show()
            return false
        } else if (xuatXu.startsWith(" ") || xuatXu.endsWith(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở đầu hoặc cuối trong 'Xuất xứ'",
                durationToast,
                2
            )
                ?.show()
            return false
        }
        else if (uri == null || uri == Uri.parse("android.resource://com.dinhconghien.getitadmin/"+ R.drawable.acer)){
            CustomToast.makeText(this,"Không được đễ trống hình",Toast.LENGTH_LONG,2)!!.show()
            return false
        }
        else if (priceLap.startsWith("0")){
            CustomToast.makeText(this,"Không được đễ 0 ở đầu 'Đơn giá'",Toast.LENGTH_LONG,2)!!.show()
            return false
        }
        else if (quantity.startsWith("0")){
            CustomToast.makeText(this,"Không được đễ 0 ở đầu 'Số lượng'",Toast.LENGTH_LONG,2)!!.show()
            return false
        }
        else if (priceLap.length <= 6){
            CustomToast.makeText(this,"'Đơn giá' phải lớn hơn 6 con số",Toast.LENGTH_LONG,2)!!.show()
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
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0]==
                    PackageManager.PERMISSION_DENIED){
                    imageFromGallery()
                }
                else{
                    Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            uri = data?.data!!
            Glide.with(this).load(uri).placeholder(R.drawable.acer).into(imv_avaLap_addNewLap)
        }
    }

}