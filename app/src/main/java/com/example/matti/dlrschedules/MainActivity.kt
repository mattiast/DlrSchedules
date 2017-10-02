package com.example.matti.dlrschedules

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainUI(Adapter(emptyList())).setContentView(this)
    }
}

class MainUI(val listAdapter: Adapter) : AnkoComponent<MainActivity> {
    companion object {
        val stationIds = mapOf<String, String>(
                "Canary Wharf" to "940GZZDLCAN",
                "Tower Gateway" to "940GZZDLTWG",
                "Shadwell" to "940GZZDLSHA"
        )
    }

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        verticalLayout {
            val btn = button("Get arrivals")
            val station = spinner {
                adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, stationIds.keys.toList())
            }

            val list = recyclerView {
                val orientation = LinearLayout.VERTICAL
                layoutManager = LinearLayoutManager(context, orientation, false)
                adapter = listAdapter
            }

            btn.onClick {
                Log.d("mytag", station.selectedItem.toString())
                val arrs = TflApi.service.getArrivals(stationIds.get(station.selectedItem)!!)
                arrs.enqueue(object : Callback<List<Arrival>> {
                    override fun onFailure(call: Call<List<Arrival>>?, t: Throwable?) {
                        Log.d("mytag", "FAIL")
                    }

                    override fun onResponse(call: Call<List<Arrival>>?, response: Response<List<Arrival>>) {
                        Log.d("mytag", "onResponse")
                        response.body()?.let {
                            val texts = it.map {
                                a -> "${a.destinationName.replace(" DLR Station", "")} [${a.timeToStation / 60}m${a.timeToStation % 60}s]"
                            }.sorted()
                                    .distinct()
                            list.adapter = Adapter(texts)
                        }
                    }
                })
            }
        }
    }
}

class Adapter(val aList: List<String>) : RecyclerView.Adapter<Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TextView(parent.context).apply {
            textSize = 20f
        })
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.textView.text = aList.get(position)
    }

    override fun getItemCount(): Int {
        return aList.size
    }
}

class Holder(val textView: TextView) : RecyclerView.ViewHolder(textView)
