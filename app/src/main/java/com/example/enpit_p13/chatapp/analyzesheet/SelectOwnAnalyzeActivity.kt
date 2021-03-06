package com.example.enpit_p13.chatapp.analyzesheet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.enpit_p13.chatapp.R
import com.example.enpit_p13.chatapp.models.Check_online
import com.example.enpit_p13.chatapp.models.User
import com.example.enpit_p13.chatapp.toppage.TopPageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_select_own_analyze.*
import kotlinx.android.synthetic.main.analyze_list_item_view.view.*
import org.jetbrains.anko.startActivity
import java.sql.Date
import java.text.SimpleDateFormat


class SelectOwnAnalyzeActivity : AppCompatActivity() {
    val list: MutableList<String> = mutableListOf()
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_own_analyze)
        set_ListView()

        FirebaseDatabase.getInstance().getReference()?.child("/users/")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            val data = it?.getValue(User::class.java)
                            if(data?.uid.toString() == FirebaseAuth.getInstance().uid.toString()){
                                val reference =FirebaseDatabase.getInstance().getReference("/Address/${FirebaseAuth.getInstance().uid.toString()}")
                                reference.setValue(Check_online("SelectOwnAnalyzeActivity",data?.username.toString(),false))
                                FirebaseDatabase.getInstance().getReference("/Address/${FirebaseAuth.getInstance().uid.toString()}")
                                        .onDisconnect()
                                        .setValue(Check_online("OFF",data?.username.toString(),false))
                            }}
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })

        adapter.setOnItemClickListener{item, view ->
            val time_item = item as TimeItem
            val intent = Intent(view.context, LookBackAnalyzeActivity::class.java)
            intent.putExtra(TIME_KEY, item.Quser.timestamp)

            Log.d("time", "user from New b ${item.Quser.timestamp.toString()}")
            startActivity(intent)

        }
        toppage_select.setOnClickListener {
            startActivity<TopPageActivity>()
        }

    }

    companion object {
        val TIME_KEY = "TIME_KEY"
    }

    fun set_ListView() {
        val ref = FirebaseDatabase.getInstance().reference.child("AnalyzeSheet/${FirebaseAuth.getInstance().uid}")
        val listener = object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                for (data in p0.children) {
                    Log.d("timedata",data.key)
                    val quetion = data.getValue<Question>(Question::class.java)

                    val time_data = quetion?.let { it} ?:continue
                    Log.d("item",time_data.timestamp.toString())
                    adapter.add(TimeItem(time_data))
                }

                val manager = LinearLayoutManager(this@SelectOwnAnalyzeActivity)
                Analyze_ListView.setLayoutManager(manager)
                Analyze_ListView.setHasFixedSize(true)
                Analyze_ListView.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {}
        }
        ref.addListenerForSingleValueEvent(listener)

    }

    class TimeItem(val Quser: Question): Item<ViewHolder>(){
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val time = SimpleDateFormat("yyyy年MM月dd日 a hh時mm分").format(Date(Quser.timestamp))
            viewHolder.itemView.analyze_item.text =  SimpleDateFormat("yyyy年MM月dd日 a hh時mm分").format(Date(Quser.timestamp))
        }
        override fun getLayout(): Int {
            return R.layout.analyze_list_item_view
        }
    }
}
