package com.example.storage_mini_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storage_mini_project.R;
import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Movie;
import com.example.storage_mini_project.entities.Showtime;
import com.example.storage_mini_project.entities.Ticket;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {

    private Context context;
    private List<Ticket> tickets;
    private AppDB database;
    private ExecutorService executorService;

    public TicketsAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
        this.database = AppDB.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void updateData(List<Ticket> newTickets) {
        this.tickets = newTickets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        holder.tvSeat.setText("Ghế: " + ticket.getSeatNumber());
        holder.tvStatus.setText(ticket.getStatus());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvBookingDate.setText(dateFormat.format(new Date(ticket.getBookingDate())));
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(format.format(ticket.getPrice()));

        executorService.execute(() -> {
            Showtime showtime = database.showtimeDAO().getById(ticket.getShowtimeId());
            if (showtime != null) {
                Movie movie = database.movieDAO().getById(showtime.getMovieId());
                
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (movie != null) {
                        holder.tvMovieTitle.setText(movie.getTitle());
                        holder.tvShowtimeInfo.setText(showtime.getShowDate() + " - " + showtime.getShowTime() + " - Phòng " + showtime.getScreenNumber());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvShowtimeInfo, tvSeat, tvBookingDate, tvPrice, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvShowtimeInfo = itemView.findViewById(R.id.tvShowtimeInfo);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
