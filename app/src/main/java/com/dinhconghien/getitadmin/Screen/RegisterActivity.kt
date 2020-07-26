package com.dinhconghien.getitadmin.Screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.UI.CustomToast
import com.dinhconghien.getitadmin.UI.DialogLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Job
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private val email_pattern = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$"
    lateinit var patternEmail: Pattern
    lateinit var matcherEmail: Matcher
    var dbReference = FirebaseDatabase.getInstance().getReference("user")
    var user =User()
    var dialogLoading: DialogLoading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        tv_loginHere_registerScreen.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_register_registerScreen.setOnClickListener {
            registerUser()
        }
    }

    fun registerUser() {
        val userName = et_userName_registerScreen.text.toString()
        val email = et_email_registerScreen.text.toString()
        val phone = et_phoneNumber_registerScreen.text.toString()
        val password = et_password_registerScreen.text.toString()
        val confirmPass = et_retypePassword_registerScreen.text.toString()
        checkValidateFormRegister(userName, email, phone, password, confirmPass)
    }

    fun checkValidateFormRegister(
        userName: String, email: String, phone: String,
        password: String, confirmPass: String
    ) {
        patternEmail = Pattern.compile(email_pattern)
        matcherEmail = patternEmail.matcher(email)

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || phone.isEmpty()) {
            CustomToast.makeText(this, "Bạn điền thiếu thông tin", Toast.LENGTH_LONG, 2)!!.show()
            return
        } else if (phone.length != 10) {
            CustomToast.makeText(this, "Sai định dạng số phone", Toast.LENGTH_LONG, 2)?.show()
            return
        } else if (!confirmPass.equals(password)) {
            CustomToast.makeText(this, "Mật khẩu chưa khớp", Toast.LENGTH_LONG, 2)!!.show()
            return
        } else if (!matcherEmail.matches()) {
            CustomToast.makeText(this, "Sai định dạng email", Toast.LENGTH_LONG, 2)!!.show()
            return
        } else if (userName.startsWith(" ") || userName.endsWith(" ")) {
            CustomToast.makeText(
                this, "Không được để khoảng trắng đầu và cuối ở 'Tên của bạn'",
                Toast.LENGTH_LONG, 2
            )?.show()
            return
        } else if (email.contains(" ")) {
            CustomToast.makeText(
                this, "Không được để khoảng trắng ở 'Email'",
                Toast.LENGTH_LONG, 2
            )?.show()
            return
        } else if (phone.contains(" ")) {
            CustomToast.makeText(
                this, "Không được để khoảng trắng ở 'Số điện thoại'",
                Toast.LENGTH_LONG, 2
            )!!.show()
            return
        } else if (password.contains(" ")) {
            CustomToast.makeText(
                this, "Không được để khoảng trắng ở 'Mật khẩu'",
                Toast.LENGTH_LONG, 2
            )?.show()
            return
        } else {
            checkDuplcateEmail(email, phone, userName, password)
        }
    }

    fun checkDuplcateEmail(
        email: String,
        phone: String,
        userName: String,
        password: String
    ) {
        val userId = dbReference.push().getKey().toString()
        dialogLoading = DialogLoading(this@RegisterActivity)
        dialogLoading!!.show()
        dbReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    CustomToast.makeText(
                        this@RegisterActivity,
                        error.toString(),
                        Toast.LENGTH_LONG,
                        3
                    )?.show()
                    dialogLoading!!.dismiss()
                    return
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        CustomToast.makeText(
                            this@RegisterActivity,
                            "Email đã tồn tại",
                            Toast.LENGTH_LONG,
                            3
                        )?.show()
                        dialogLoading!!.dismiss()
                        return
                    } else {
                        try {
                            user = User(userId,email,userName,phone,password)
                            dbReference.child(userId).setValue(user)
                            CustomToast.makeText(
                                this@RegisterActivity,
                                "Đăng kí thành công",
                                Toast.LENGTH_LONG,
                                1
                            )?.show()
                            dialogLoading!!.dismiss()
                            return
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@RegisterActivity,
                                e.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("DbError", "$e")
                            dialogLoading!!.dismiss()
                        }
                    }
                }
            })
    }
}