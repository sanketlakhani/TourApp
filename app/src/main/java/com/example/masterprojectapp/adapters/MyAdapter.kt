package com.example.masterprojectapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.masterprojectapp.R
import com.example.masterprojectapp.models.MyModel

class MyAdapter(
    var list: ArrayList<MyModel>,
    var onViewMoreClick: (String, String, String, String, String) -> Unit,
    var onBookClick: () -> Unit,
    // var onLikeClick: (String, Int) -> Unit

) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        var txtPackageName = view.findViewById<TextView>(R.id.txtPackageName)
        var txtPrice = view.findViewById<TextView>(R.id.txtPrice)
        var txtDays = view.findViewById<TextView>(R.id.txtDays)
        var txtLocation = view.findViewById<TextView>(R.id.txtLocation)
        var txtDescription = view.findViewById<TextView>(R.id.txtDescription)
        var appBtnViewMore = view.findViewById<AppCompatButton>(R.id.appBtnViewMore)
        var appBtnBook = view.findViewById<AppCompatButton>(R.id.appBtnBook)
        var imgLike = view.findViewById<ImageView>(R.id.imgLike)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var v =
            LayoutInflater.from(parent.context).inflate(R.layout.detail_item_file, parent, false)

        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtPackageName.setText(list[position].packageName)
        holder.txtPrice.setText(list[position].price)
        holder.txtDays.setText(list[position].days)
        holder.txtLocation.setText(list[position].location)
        holder.txtDescription.setText(list[position].description)

        //  holder.imgLike.setOnClickListener {

        /* if (list[position].status == 1) {

             holder.imgLike.setImageResource(R.drawable.heartfill)
         } else {

             holder.imgLike.setImageResource(R.drawable.heart)

         }
         holder.imgLike.setOnClickListener {


             if (list[position].status == 1) {

                 onLikeClick.invoke(list[position].key, 0)

             } else {
                 onLikeClick.invoke(list[position].key, 1)
             }


         }*/

        holder.appBtnViewMore.setOnClickListener {

            onViewMoreClick.invoke(

                list[position].packageName,
                list[position].description,
                list[position].days,
                list[position].price,
                list[position].location,
               // list[position].imageUri
            )
        }

        holder.appBtnBook.setOnClickListener {

            onBookClick.invoke()

        }
    }

    override fun getItemCount(): Int {

        return list.size
    }
}