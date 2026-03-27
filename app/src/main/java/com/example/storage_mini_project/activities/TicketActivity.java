package com.example.storage_mini_project.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_mini_project02_v1.R;
import com.example.storage_mini_project02_v1.adapters.InvoiceProductAdapter;
import com.example.storage_mini_project02_v1.dal.AppDB;
import com.example.storage_mini_project02_v1.dal.OrderDAO;
import com.example.storage_mini_project02_v1.dal.OrderDetailDAO;
import com.example.storage_mini_project02_v1.entities.Order;
import com.example.storage_mini_project02_v1.entities.OrderDetail;
import com.example.storage_mini_project02_v1.entities.Product;
import com.example.storage_mini_project02_v1.entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        Context context = this;
        Intent intent = getIntent();

        int ticketId = intent.getIntExtra("ticket_id", -1);
        if (ticketId == -1) {
            finish();
            return;
        }

        TextView tvTicketCode = findViewById(R.id.tvTicketCode);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvSeat = findViewById(R.id.tvSeat);
        TextView tvShowtime = findViewById(R.id.tvShowtime);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvBookingDate = findViewById(R.id.tvBookingDate);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDB db = AppDB.getInstance(context);

            // Lấy ticket
            Ticket ticket = db.ticketDAO().getById(ticketId);
            if (ticket == null) {
                runOnUiThread(this::finish);
                return;
            }

            // Lấy user
            User user = db.userDAO().getById(ticket.getUserId());

            // Lấy showtime
            Showtime showtime = db.showtimeDAO().getById(ticket.getShowtimeId());

            runOnUiThread(() -> {

                // Mã vé
                tvTicketCode.setText("Ticket #" + ticket.getId());

                // User
                if (user != null) {
                    String name = user.getFullName() != null && !user.getFullName().isEmpty()
                            ? user.getFullName()
                            : user.getUsername();
                    tvUserName.setText(name);
                }

                // Ghế
                tvSeat.setText("Ghế: " + ticket.getSeatNumber());

                // Suất chiếu
                if (showtime != null) {
                    tvShowtime.setText("Suất: " + showtime.getStartTime());
                }

                // Giá
                tvPrice.setText(String.format("%,.0f đ", ticket.getPrice()));

                // Trạng thái
                tvStatus.setText("Trạng thái: " + ticket.getStatus());

                // Ngày đặt
                java.text.SimpleDateFormat sdf =
                        new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

                tvBookingDate.setText("Đặt lúc: " +
                        sdf.format(new java.util.Date(ticket.getBookingDate())));
            });
        });
    }
}