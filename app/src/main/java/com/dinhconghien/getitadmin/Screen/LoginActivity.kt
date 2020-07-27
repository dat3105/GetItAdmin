package com.dinhconghien.getitadmin.Screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dinhconghien.getitadmin.MainActivity
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.CustomToast
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.dinhconghien.getitadmin.Util.SharePreference_Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val email_pattern = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$"
    lateinit var patternEmail: Pattern
    lateinit var matcherEmail: Matcher
    lateinit var dialogLoading: DialogLoading
    var dbReference = FirebaseDatabase.getInstance().getReference("user")
    var listUser = ArrayList<User>()
    var listUserKey = ArrayList<String>()
    var user : User = User()
    lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        job = Job()


        GlobalScope.launch(Dispatchers.IO) {
            dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("DbError", error.toString())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listUser.clear()
                    listUserKey.clear()
                    getUser(snapshot)
                }

            })
        } //fetch data from firebase

        tv_loginHere_loginScreen.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_loginScreen.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                checkLogin()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    suspend fun checkLogin() {
        val email = et_email_loginScreen.text.toString()
        val pass = et_pass_loginScreen.text.toString()
        var noti = ""
        checkValidateForm(email, pass)
        val checkValidate = checkValidateForm(email, pass)
        if (checkValidate == true) {
            dialogLoading = DialogLoading(this@LoginActivity)
            dialogLoading.show()
            delay(1000L)
            withContext(Dispatchers.IO) {
                noti = async { checkInfo(email, pass) }.await()
            }
            if (noti == "Email hoặc Mật khẩu không chính xác") {
                CustomToast.makeText(
                    this@LoginActivity,
                    noti,
                    Toast.LENGTH_LONG,
                    3
                )?.show()
                dialogLoading.dismiss()
            } else if (noti == "Tài khoản đang đăng nhập ở nơi khác") {
                CustomToast.makeText(
                    this@LoginActivity,
                    noti,
                    Toast.LENGTH_LONG,
                    3
                )?.show()
                dialogLoading.dismiss()
            }
            else if (noti == "Chỉ Admin mới đăng nhập được"){
                CustomToast.makeText(
                    this@LoginActivity,
                    noti,
                    Toast.LENGTH_LONG,
                    3
                )?.show()
                dialogLoading.dismiss()
            }
            else {
                CustomToast.makeText(
                    this@LoginActivity,
                    noti,
                    Toast.LENGTH_LONG,
                    1
                )?.show()
                dialogLoading.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
    }

    fun checkValidateForm(email: String, pass: String): Boolean {
        patternEmail = Pattern.compile(email_pattern)
        matcherEmail = patternEmail.matcher(email)
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            CustomToast.makeText(this, "Không được để trống thông tin", Toast.LENGTH_LONG, 2)
                ?.show()
            return false
        } else if (email.contains(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở 'Email' ",
                Toast.LENGTH_LONG,
                2
            )?.show()
            return false
        } else if (pass.contains(" ")) {
            CustomToast.makeText(
                this,
                "Không được để khoảng trắng ở 'Mật khẩu' ",
                Toast.LENGTH_LONG,
                2
            )?.show()
            return false
        } else if (!matcherEmail.matches()) {
            CustomToast.makeText(this, "Sai định dạng email", Toast.LENGTH_LONG, 2)?.show()
            return false
        }
        return true
    }

    fun getUser(snapShot: DataSnapshot) {
        for (param in snapShot.children) {
            user = param.getValue(User::class.java)!!
            listUser.add(user)
            listUserKey.add(param.key.toString())
        }
    }

    fun checkInfo(email: String, pass: String): String {
        var noti = "Email hoặc Mật khẩu không chính xác"
        for (i in 0 until listUser.size) {
            if (listUser[i].email.equals(email) && listUser[i].password.equals(pass)) {
                if (!listUser[i].role.equals("Admin",true)){
                    noti = "Chỉ Admin mới đăng nhập được"
                }
                else if (listUser[i].wasOnline == true) {
                    noti = "Tài khoản đang đăng nhập ở nơi khác"
                }
                else {
                    noti = "Đăng nhập thành công"
                    dbReference.child(listUser[i].userID).child("wasOnline").setValue(true)
                    val sessionManagement = SharePreference_Utils(this@LoginActivity)
                    sessionManagement.saveSession(listUser,i)
                }
            }
        }
        return noti
    }

    private fun checkSession() {
        //check if user is logged in
        //if user is logged in --> move to mainActivity
        val sessionManagement = SharePreference_Utils(this@LoginActivity)
        val currentEmail = sessionManagement.getSession()
        if (currentEmail != "") {
            //user id logged in and so move to mainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}