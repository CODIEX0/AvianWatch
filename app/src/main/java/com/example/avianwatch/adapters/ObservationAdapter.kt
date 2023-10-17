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

class ObservationAdapter(private val observations: List<BirdObservation>) : RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder>() {
    inner class ObservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvBirdName)
        val date: TextView = itemView.findViewById(R.id.tvDateObserved)
        val notes: TextView = itemView.findViewById(R.id.txtNotes)
        val bird_image: ImageView = itemView.findViewById((R.id.imgBirdImage))
        val location: TextView = itemView.findViewById(R.id.txtLocation)

        fun bind(observation: BirdObservation) {
            name.text = observation.birdSpecies
            date.text = observation.dateTime.toString()
            //location.text = observation.hotspot.toString()
            //have to implement location
            location.text = observation.hotspot.locName
            notes.text = observation.additionalNotes

            // Check if the image is null or empty, and hide the image view if it is
            if (observation.birdImage.isNullOrEmpty()) {
                bird_image.visibility = View.GONE
            } else {
                bird_image.visibility = View.VISIBLE
                Image.setBase64Image(observation.birdImage, bird_image)
            }
        }

        init {
            // Set click listener for the itemView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val bird = observations[position]
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
        val observation = observations[position]
        holder.bind(observation)
    }

    override fun getItemCount(): Int {
        return observations.size
    }

    // Define an interface for item click handling
    interface OnItemClickListener {
        fun onItemClick(bird: BirdObservation)
    }

    private var onItemClickListener: OnItemClickListener? = null

    // Setter for the click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}