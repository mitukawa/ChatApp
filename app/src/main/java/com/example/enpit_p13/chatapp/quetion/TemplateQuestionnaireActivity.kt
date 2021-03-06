package com.example.enpit_p13.chatapp.quetion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.enpit_p13.chatapp.R
import com.example.enpit_p13.chatapp.messages.DeleteConfirmDialog
import com.example.enpit_p13.chatapp.messages.LatestMessagesActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.activity_qustiontemp2.*
import kotlinx.android.synthetic.main.activity_template_questionnaire.*
import org.jetbrains.anko.startActivity

class TemplateQuestionnaireActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_questionnaire)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        temp_send_Button.setOnClickListener {
            if(!temp_Q1_yesButton.isChecked && !temp_Q1_noButton.isChecked){
                Toast.makeText(this,"質問１に回答してください",Toast.LENGTH_SHORT).show()
            }else{
                writeDatabase()
                val dialog = ThanksDialog()
                dialog.show(supportFragmentManager,"Thanks_dialog")
            }
        }
    }

    override fun onBackPressed() {
        startActivity<LatestMessagesActivity>()
    }

    fun writeDatabase(){
        var q1:String = "none"
        val q2:String
        val q3:String
        val q4:String

        if(temp_Q1_noButton.isChecked){
            q1 = "no"
        }else if(temp_Q1_yesButton.isChecked){
            q1 = "yes"
        }
        q2 = temp_spinner.selectedItem.toString()
        q3 = temp_Q2_editText.text.toString()
        q4 = temp_Q3_editText.text.toString()



        val questionnaire = Questionnaire(q1,q2,q3,q4)
        val ref = FirebaseDatabase.getInstance().getReference("Questionnaires")
        ref.push().setValue(questionnaire)
    }
}
