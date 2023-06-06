package com.example.masterprojectapp

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.masterprojectapp.adapters.MyAdapter
import com.example.masterprojectapp.databinding.ActivityDashboardBinding
import com.example.masterprojectapp.models.MyModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ActivityDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var refrence: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    var list: ArrayList<MyModel> = ArrayList()
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        addDetails()

        showDetails()

        navDrawer()

        popUpMenu()

    }

    override fun onBackPressed() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert !")
        builder.setMessage("Are you sure want to exit ?")
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Yes "
        ) { dialog, which -> finish() }
        builder.setNegativeButton(
            "No"
        ) { dialog, which -> dialog!!.dismiss() }
        builder.show()

    }

    private fun popUpMenu() {

        binding.imgMore.setOnClickListener {

            val popupMenu = PopupMenu(this@ActivityDashboard, binding.imgMore)

            popupMenu.menuInflater.inflate(R.menu.menu_item, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {

                mAuth = FirebaseAuth.getInstance()

                if (it.itemId == R.id.menuLogout) {

                    mAuth.signOut()

                    Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                true
            }
            popupMenu.show()
        }
    }


    private fun navDrawer() {

        binding.imgMenu.setOnClickListener {

            binding.drawerLayout.openDrawer(Gravity.LEFT)

            binding.txtVersion.setText(
                "version : " + BuildConfig.VERSION_NAME
            )
        }

        binding.linearRating.setOnClickListener {


            val uri: Uri = Uri.parse("market://details?id=com.tripadvisor.tripadvisor")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)

            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )

            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.tripadvisor.tripadvisor")
                    )
                )
            }
        }

        binding.linearShare.setOnClickListener {

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=com.tripadvisor.tripadvisor"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }

    private fun initView() {

        var sp = getSharedPreferences("UserInfo", MODE_PRIVATE)

        var myUsername: String = sp.getString("Username", "Admin").toString()

        binding.txtUsername.text = myUsername.toString()

    }

    private fun addDetails() {

        binding.txtUsername.setOnClickListener {

            var intent = Intent(this, ActivityAddDetails::class.java)
            startActivity(intent)
        }
    }

    private fun showDetails() {

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading....")
        progressDialog.show()

        refrence = FirebaseDatabase.getInstance().reference

        refrence.root.child("InfoTb")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    progressDialog.hide()
                    for (child in snapshot.children) {

                        val c = child.getValue(MyModel::class.java)
                        var packageNameR = c?.packageName
                        var descriptionR = c?.description
                        var daysR = c?.days
                        var locationR = c?.location
                        var priceR = c?.price
                        var keyR = c?.key

                        list.add(c!!)

                        val manager = LinearLayoutManager(
                            this@ActivityDashboard,
                            LinearLayoutManager.VERTICAL,
                            false
                        )

                        var adapter =
                            MyAdapter(
                                list,
                                onViewMoreClick = { packagename, description, days, price, location ->

                                    var intent =
                                        Intent(this@ActivityDashboard, ActivityViewMore::class.java)
                                    intent.putExtra("PACKAGE NAME", packagename)
                                    intent.putExtra("DESCRIPTION", description)
                                    intent.putExtra("DAYS", days)
                                    intent.putExtra("PRICE", price)
                                    intent.putExtra("LOCATION", location)
                                    startActivity(intent)

                                },
                                onBookClick = {

                                    val intent = Intent(
                                        this@ActivityDashboard,
                                        ActivityUserDetail::class.java
                                    )
                                    startActivity(intent)
                                })
                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.layoutManager = manager
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ActivityDashboard, "Canceled", Toast.LENGTH_SHORT).show()
                }

            })
    }
}