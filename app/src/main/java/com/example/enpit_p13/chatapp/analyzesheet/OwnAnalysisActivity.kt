package com.example.enpit_p13.chatapp.analyzesheet


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.enpit_p13.chatapp.R
import com.example.enpit_p13.chatapp.models.Check_online
import com.example.enpit_p13.chatapp.models.User
import com.example.enpit_p13.chatapp.toppage.TopPageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_own_analysis.*
import kotlinx.android.synthetic.main.content_own_analysis.*
import org.jetbrains.anko.startActivity

class OwnAnalysisActivity : AppCompatActivity() {

    var mDatabase = FirebaseDatabase.getInstance().getReference("/AnalyzeSheet/${FirebaseAuth.getInstance().uid}")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(com.example.enpit_p13.chatapp.R.layout.activity_own_analysis)
        setSupportActionBar(toolbar_own)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        FirebaseDatabase.getInstance().getReference()?.child("/users/")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            val data = it?.getValue(User::class.java)
                            if(data?.uid.toString() == FirebaseAuth.getInstance().uid.toString()){
                                val reference =FirebaseDatabase.getInstance().getReference("/Address/${FirebaseAuth.getInstance().uid.toString()}")
                                reference.setValue(Check_online("OwnAnalysisActivity",data?.username.toString(),false))
                                FirebaseDatabase.getInstance().getReference("/Address/${FirebaseAuth.getInstance().uid.toString()}")
                                        .onDisconnect()
                                        .setValue(Check_online("OFF",data?.username.toString(),false))
                            }}
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
        setTopLabel()

        Q2_spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener{

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val spinner = parent as? Spinner
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        save_button.setOnClickListener{

            Toast.makeText(this,"保存しました。",Toast.LENGTH_LONG).show()
            writeNewQuestion()
        }
        toppage_own.setOnClickListener {
            startActivity<TopPageActivity>()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.analyze_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.look_back ->{
                val ref = FirebaseDatabase.getInstance().reference.child("/AnalyzeSheet")

                ref.addListenerForSingleValueEvent(object :ValueEventListener
                {
                    override fun onDataChange(p0: DataSnapshot)
                    {
                        Log.d("Main","実行")
                        if(p0.hasChild(FirebaseAuth.getInstance().uid.toString())) {
                            startActivity<SelectOwnAnalyzeActivity>()
                        }else{
                            Toast.makeText(this@OwnAnalysisActivity,"保存されているデータがありません",Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun writeNewQuestion()
    {
        val q1:String = Q1_edit_num.text.toString()
        val q2_1:String = Q2_edit_num.text.toString()
        val q2_2:Int = Q2_spinner.selectedItemPosition
        val q3:String = Q3_edit_num.text.toString()
        val q5:Int = when {
            Q5_noButtom.isChecked -> 0
            Q5_yesButtom.isChecked -> 1
            else -> 3
        }
        val q6_1:Int = when {
            Q6_noButtom.isChecked -> 0
            Q6_yesButtom.isChecked -> 1
            else -> 3
        }
        val q6_2:String =Q6_edit_detail.text.toString()
        val q7:String = Q7_edit_text.text.toString()
        val q8_1:String = Q8Edit_num.text.toString()
        val q8_2:Int = Q2_spinner.selectedItemPosition
        val q8_3:String  = Q8Edit_detail.text.toString()
        val q9:String = Q9_edit_detail.text.toString()

        //データベースに保存
        var quetion = Question(q1,q2_1,q2_2,q3,q5,q6_1,q6_2,q7,q8_1,q8_2,q8_3,q9)
        mDatabase.child(quetion.timestamp.toString()).setValue(quetion)

    }

    private fun fetchuser()
    {
        val ref = FirebaseDatabase.getInstance().reference.child("/AnalyzeSheet")

        ref.addValueEventListener(object :ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.key == FirebaseAuth.getInstance().uid) {
                    Log.d("exe", p0.key)
                    Log.d("p0chkey", p0.child("1542701485246").key)

                }
            }

        })

        ref.addListenerForSingleValueEvent(object :ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                Log.d("Main","実行")
                for(data in p0.children)
                {
                    val userdata = data.getValue<Question>(Question::class.java)
                    val read_quetion = userdata?.let { it } ?:continue
                    if(read_quetion.userId.toString() == FirebaseAuth.getInstance().uid.toString()) {
                        readQuestion(read_quetion)
                    }

                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun setTopLabel(){
        val ref = FirebaseDatabase.getInstance().reference.child("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {

                for(data in p0.children)
                {
                    val userdata = data.getValue<User>(User::class.java)
                    val users = userdata?.let { it } ?:continue
                    if(users.uid.toString() == FirebaseAuth.getInstance().uid.toString()) {
                        Label.text = users.username + " さんの分析シート"
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })

    }

    fun readQuestion(question: Question){
        Q1_edit_num.setText(question.q1)
        Q2_edit_num.setText(question.q2_1)
        Q2_spinner.setSelection(question.q2_2)
        Q3_edit_num.setText(question.q3)

        when {
            question.q5 == 0 -> {
                Q5_yesButtom.isChecked = false
                Q5_noButtom.isChecked = true
            }
            question.q5 == 1 -> {
                Q5_yesButtom.isChecked = true
                Q5_noButtom.isChecked = false
            }
            else -> {
                Q5_yesButtom.isChecked = false
                Q5_noButtom.isChecked = false
            }
        }

        when {
            question.q6_1 == 0 -> {
                Q6_yesButtom.isChecked = false
                Q6_noButtom.isChecked = true
            }
            question.q6_1 == 1 -> {
                Q6_yesButtom.isChecked = true
                Q6_noButtom.isChecked = false
            }
            else -> {
                Q6_yesButtom.isChecked = false
                Q6_noButtom.isChecked = false
            }
        }

        Q6_edit_detail.setText(question.q6_2)
        Q7_edit_text.setText(question.q7)
        Q8Edit_num.setText(question.q8_1)
        Q8_spinner.setSelection(question.q8_2)
        Q8Edit_detail.setText(question.q8_3)
        Q9_edit_detail.setText(question.q9)
    }
}