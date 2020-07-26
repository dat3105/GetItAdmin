package com.dinhconghien.getitadmin.Screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Adapter.ListLaptop_Adapter
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_lap.*
import kotlinx.android.synthetic.main.activity_update_lap.*
import kotlinx.coroutines.Job
import java.lang.Exception

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UpdateLapActivity : AppCompatActivity() {

    val DB_BRANDLAP = FirebaseDatabase.getInstance().getReference("brandLap")
    val DB_LAPTOP = FirebaseDatabase.getInstance().getReference("laptop")
    val DB_LAPDETAIL = FirebaseDatabase.getInstance().getReference("laptopDetail")
    val STORE_LAPTOP = FirebaseStorage.getInstance().getReference("laptop")
    lateinit var listLap: ArrayList<Laptop>
    lateinit var adapterListLap: ListLaptop_Adapter
    var idBrandLap = ""
    var idLap = ""
    var nameLap = ""
    var priceLap = ""
    var quantity = ""
    var avaLap = ""
    var nameBrand = ""
    lateinit var job: Job
    val TAG_GETBRAND = "DbErrorGetBrandUpdateLap"
    val TAG_GETLAPDETAIL = "DbErrorGetLapDetailUpdateLap"
    val TAG_UPDATELAPDETAIL = "DbErrorUpdateLapDetail_UpdateLap"
    lateinit var lapModel: Laptop
    lateinit var lapDetailModel: LaptopDetail
    lateinit var brandModel: BrandLap
    val IMAGE_PICK_CODE = 1000
    val PERMISSION_CODE = 1001
    var uri: Uri = Uri.parse("android.resource://com.dinhconghien.getitadmin/" + R.drawable.acer)
    val durationToast = Toast.LENGTH_LONG


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_lap)
        setSupportActionBar(toolbar_updateLap)
        toolbar_updateLap.setNavigationOnClickListener {
            val intent = Intent(this, ListLapActivity::class.java)
            startActivity(intent)
            finish()
        }
        initUI_infoLap_firstTime()
        initUI_infoLapDetail_firstTime()

        btn_addImageLap_updateLap.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    imageFromGallery()
                }
            } else {
                imageFromGallery()
            }
        }

        btn_confirm_updateLap.setOnClickListener {
            updateInfo()
        }

        tv_brandName_updateLap.setOnClickListener {
            val inten = Intent(this,UpdateBrandActivity::class.java)
            inten.putExtra("idBrandLap",idBrandLap)
            startActivity(inten)
        }

    }

    private fun initUI_infoLap_firstTime() {
        //get Data from list lap [ListLaptop_Adapter]
        idBrandLap = intent.getStringExtra("idBrandLap")
        idLap = intent.getStringExtra("idLap")
        nameLap = intent.getStringExtra("nameLap")
        priceLap = intent.getIntExtra("priceLap", 0).toString()
        quantity = intent.getIntExtra("quantity", 0).toString()
        avaLap = intent.getStringExtra("avaLap")
        nameBrand = intent.getStringExtra("nameBrand")
        //init UI for Laptop's info
        DB_BRANDLAP.orderByChild("idBrandLap").equalTo(idBrandLap)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("LongLogTag")
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG_GETBRAND, error.toString())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (param in snapshot.children) {
                        brandModel = param.getValue(BrandLap::class.java)!!
                        tv_brandName_updateLap.text = brandModel.nameBrand
                    }
                }
            })

        ed_nameLap_updateLap.setText(nameLap)
        ed_priceLap_updateLap.setText(priceLap)
        ed_quantityLap_updateLap.setText(quantity)
        Glide.with(this)
            .load(avaLap)
            .fitCenter()
            .into(imv_avaLap_updateLap)
    }

    private fun initUI_infoLapDetail_firstTime() {
        DB_LAPDETAIL.orderByChild("idLap").equalTo(idLap)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("LongLogTag")
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG_GETLAPDETAIL, error.toString())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (param in snapshot.children) {
                        lapDetailModel = param.getValue(LaptopDetail::class.java)!!
                        ed_cpuTech_updateLap.setText(lapDetailModel.congNgheCPU)
                        ed_speedCPU_updateLap.setText(lapDetailModel.tocDoCPU)
                        ed_hangCPU_updateLap.setText(lapDetailModel.hangCPU)
                        ed_boNhoDem_updateLap.setText(lapDetailModel.boNhoDem)
                        ed_dungLuongRAM_updateLap.setText(lapDetailModel.dungLuongRAM)
                        ed_loaiRAM_updateLap.setText(lapDetailModel.loaiRAM)
                        ed_tocDoBUS_updateLap.setText(lapDetailModel.tocDoBus)
                        ed_soLuongRAM_updateLap.setText(lapDetailModel.soLuongRAM)
                        ed_xuatXu_updateLap.setText(lapDetailModel.xuatXu)
                        ed_namSanXuat_updateLap.setText(lapDetailModel.namSanXuat)
                    }
                }
            })
    }

    private fun updateInfo() {
        val nameLap = ed_nameLap_updateLap.text.toString()
        val priceLap = ed_priceLap_updateLap.text.toString()
        val quantityLap = ed_quantityLap_updateLap.text.toString()
        val congNgheCPU = ed_cpuTech_updateLap.text.toString()
        val tocDoCPU = ed_speedCPU_updateLap.text.toString()
        val hangCPU = ed_hangCPU_updateLap.text.toString()
        val boNhoDem = ed_boNhoDem_updateLap.text.toString()
        val dungLuongRAM = ed_dungLuongRAM_updateLap.text.toString()
        val loaiRAM = ed_loaiRAM_updateLap.text.toString()
        val tocDoBus = ed_tocDoBUS_updateLap.text.toString()
        val soLuongRAM = ed_soLuongRAM_updateLap.text.toString()
        val xuatXu = ed_xuatXu_updateLap.text.toString()
        val namSanXuat = ed_namSanXuat_updateLap.text.toString()
        checkValidateForm(
            nameLap, congNgheCPU, tocDoCPU, hangCPU, boNhoDem
            , dungLuongRAM, loaiRAM, tocDoBus, soLuongRAM, xuatXu, namSanXuat, priceLap, quantityLap
        )
        val checkForm = checkValidateForm(
            nameLap, congNgheCPU, tocDoCPU, hangCPU, boNhoDem, dungLuongRAM
            , loaiRAM, tocDoBus, soLuongRAM, xuatXu, namSanXuat, priceLap, quantityLap
        )
        if (checkForm == true) {
            val dialogLoading = DialogLoading(this)
            dialogLoading.show()
            DB_LAPTOP.orderByChild("nameLap").equalTo(nameLap)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("LongLogTag")
                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG_UPDATELAPDETAIL, error.toString())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            CustomToast.makeText(
                                this@UpdateLapActivity,
                                "Tên laptop đã tồn tại",
                                durationToast,
                                3
                            )
                                ?.show()
                            dialogLoading.dismiss()
                        } else {
                            try {
                                var store : StorageReference = STORE_LAPTOP.child(idLap)
                                store.putFile(uri).addOnCompleteListener {
                                    store.downloadUrl.addOnSuccessListener {
                                        //update laptop
                                            uri -> avaLap = uri.toString()
                                        val lapModel = Laptop(idLap, idBrandLap, nameLap, priceLap.toInt()
                                            , quantity.toInt(), avaLap,nameBrand = nameBrand)
                                        DB_LAPTOP.child(idLap).setValue(lapModel)

                                        //update laptop detail
                                        DB_LAPDETAIL.orderByChild("idLap").equalTo(idLap)
                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                @SuppressLint("LongLogTag")
                                                override fun onCancelled(error: DatabaseError) {
                                                    Log.d(TAG_UPDATELAPDETAIL, error.toString())
                                                    dialogLoading.dismiss()
                                                }
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    try {
                                                        for (param in snapshot.children) {
                                                            val lapDetail = param.getValue(LaptopDetail::class.java)!!
                                                            val idLapDetail = lapDetail.idLapDetail
                                                            val lapDeModel = LaptopDetail(
                                                                idLapDetail,idLap,congNgheCPU,tocDoCPU,hangCPU,boNhoDem,dungLuongRAM,
                                                                loaiRAM,tocDoBus,soLuongRAM,xuatXu,namSanXuat)
                                                            DB_LAPDETAIL.child(idLapDetail).setValue(lapDeModel)
                                                            CustomToast.makeText(this@UpdateLapActivity,"Update thông tin Lap thành công"
                                                                ,durationToast,1)?.show()
                                                            dialogLoading.dismiss()
                                                            val intent = Intent(this@UpdateLapActivity,ListLapActivity::class.java)
                                                            startActivity(intent)
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                        dialogLoading.dismiss()
                                                    }
                                                }
                                            })
                                    }
                                }.addOnFailureListener {
                                    CustomToast.makeText(this@UpdateLapActivity, it.toString(), Toast.LENGTH_LONG, 3)!!.show()
                                    dialogLoading.dismiss()
                                }



                            } catch (e: Exception) {
                                e.printStackTrace()
                                dialogLoading.dismiss()
                            }
                        }
                    }

                })
        }
    }

    fun checkValidateForm(
        nameLap: String, congNgheCPU: String, tocDoCPU: String, hangCPU: String, boNhoDem: String,
        dungLuongRAM: String, loaiRAM: String, tocDoBus: String,
        soLuongRAM: String, xuatXu: String, namSanXuat: String, priceLap: String, quantity: String
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
        }  else if (priceLap.startsWith("0")) {
            CustomToast.makeText(this, "Không được đễ 0 ở đầu 'Đơn giá'", Toast.LENGTH_LONG, 2)!!
                .show()
            return false
        } else if (quantity.startsWith("0")) {
            CustomToast.makeText(this, "Không được đễ 0 ở đầu 'Số lượng'", Toast.LENGTH_LONG, 2)!!
                .show()
            return false
        } else if (priceLap.length <= 6) {
            CustomToast.makeText(this, "'Đơn giá' phải lớn hơn 6 con số", Toast.LENGTH_LONG, 2)!!
                .show()
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
            Glide.with(this).load(uri).placeholder(R.drawable.acer).into(imv_avaLap_updateLap)
        }
    }

}