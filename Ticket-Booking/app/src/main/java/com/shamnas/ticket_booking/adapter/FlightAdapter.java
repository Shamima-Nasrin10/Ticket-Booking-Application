package com.shamnas.ticket_booking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shamnas.ticket_booking.databinding.ViewholderFlightBinding;
import com.shamnas.ticket_booking.model.Flight;

import java.util.ArrayList;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.Viewholder> {
    private final ArrayList<Flight> flights;
    private Context context;

    public FlightAdapter(ArrayList<Flight> flights) {
        this.flights = flights;
    }

    @NonNull
    @Override
    public FlightAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderFlightBinding binding = ViewholderFlightBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightAdapter.Viewholder holder, int position) {
        Flight flight = flights.get(position);
        Glide.with(context)
                .load(flight.getAirlineLogo())
                .into(holder.binding.logo);
        holder.binding.fromTxt.setText(flight.getFrom());
        holder.binding.fromShortTxt.setText(flight.getFromShort());
        holder.binding.toTxt.setText(flight.getTo());
        holder.binding.toShortTxt.setText(flight.getToShort());

    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private final ViewholderFlightBinding binding;

        public Viewholder(ViewholderFlightBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
