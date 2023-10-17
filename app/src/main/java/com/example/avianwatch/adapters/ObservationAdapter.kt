package com.example.avianwatch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avianwatch.R
import com.example.avianwatch.data.BirdObservation
import com.example.avianwatch.objects.Image
import com.example.avianwatch.data.ObservationItem

class ObservationAdapter(private val observation: List<BirdObservation>) : RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder>() {

    inner class ObservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvBirdName)
        val date: TextView = itemView.findViewById(R.id.tvDateObserved)
        val bird_image: ImageView = itemView.findViewById((R.id.imgBirdImage))
        val location: TextView = itemView.findViewById(R.id.txtLocation)

        fun bind(observation: BirdObservation) {
            name.text = observation.birdSpecies
            date.text = observation.dateTime.toString()
            location.text = observation.hotspot.locName
            if (observation.birdImage.equals("")) {
                bird_image.visibility = View.INVISIBLE
            } else {
                bird_image.visibility = View.VISIBLE
                Image.setBase64Image(observation.birdImage,bird_image)
            }
        }

        init {
            // Set click listener for the itemView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val bird = observation[position]
                    // Call the onItemClick method of the listener with the clicked observation
                    onItemClickListener?.onItemClick(bird)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_observation, parent, false)
        return ObservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) {
        val observation = observation[position]
        holder.bind(observation)
    }


    override fun getItemCount(): Int {
        return observation.size
    }

    fun updateObservationList(observationList: MutableList<BirdObservation>) {
        var list: MutableList<BirdObservation> = observationList.toMutableList()
        list.clear()
        list.addAll(observationList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(bird: BirdObservation)
    }
    private var onItemClickListener: OnItemClickListener? = null

    // Setter for the click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}