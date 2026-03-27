package com.example.storage_mini_project02_v1.activities;

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
        int orderId = intent.getIntExtra("order_id", -1);
        if (orderId == -1) {
            finish();
            return;
        }
        TextView tvInvoiceCode = findViewById(R.id.tvInvoiceCode);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        TextView tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        TextView etCustomerAddress = findViewById(R.id.etCustomerAddress);
        TextView tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        RecyclerView rvInvoiceProducts = findViewById(R.id.rvInvoiceProducts);
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavBar);
        // Xử lý sự kiện cho bottomNavBar nếu cần
        bottomNavBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                // ...chuyển về Home
                finish();
                return true;
            } else if (id == R.id.menu_categories) {
                // ...chuyển về Categories
                finish();
                return true;
            } else if (id == R.id.menu_cart) {
                // ...chuyển về Cart
                finish();
                return true;
            } else if (id == R.id.menu_orders) {
                // ...chuyển về Orders
                finish();
                return true;
            } else if (id == R.id.menu_profile) {
                // ...chuyển về Profile
                finish();
                return true;
            }
            return false;
        });
        InvoiceProductAdapter adapter = new InvoiceProductAdapter();
        rvInvoiceProducts.setAdapter(adapter);
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDB db = AppDB.getInstance(context.getApplicationContext());
            OrderDAO orderDAO = db.orderDAO();
            OrderDetailDAO orderDetailDAO = db.orderDetailDAO();
            Order order = orderDAO.getById(orderId);
            if (order == null) {
                runOnUiThread(this::finish);
                return;
            }
            List<OrderDetail> orderDetails = orderDetailDAO.getByOrder(order.getId());
            List<Product> products = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();
            double total = 0;
            if (orderDetails != null) {
                for (OrderDetail od : orderDetails) {
                    if (od == null) continue;
                    Product p = db.productDAO().getById(od.getProductId());
                    if (p != null) {
                        products.add(p);
                        quantities.add(od.getQuantity());
                        total += p.getPrice() * od.getQuantity();
                    }
                }
            }
            User user = db.userDAO().getById(order.getUserId());
            final double totalFinal = total;
            runOnUiThread(() -> {
                tvInvoiceCode.setText(getString(R.string.invoice_code, order.getId()));
                tvTotalAmount.setText(String.format(java.util.Locale.getDefault(), "%,.0f đ", totalFinal));
                adapter.setData(products, quantities);
                if (user != null) {
                    // Sử dụng fullName nếu có, nếu không thì dùng username
                    String displayName = user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername();
                    tvCustomerName.setText(getString(R.string.customer_name, displayName));
                    tvCustomerPhone.setText(getString(R.string.customer_phone, user.getPhone()));
                    // Nếu User có trường địa chỉ, hãy lấy ra, nếu không thì để trống
                    // etCustomerAddress.setText(user.getAddress()); // Không có trường address trong entity User
                    etCustomerAddress.setText("");
                } else {
                    tvCustomerName.setText("");
                    tvCustomerPhone.setText("");
                    etCustomerAddress.setText("");
                }
                String status = order.getStatus();
                tvPaymentStatus.setVisibility(status != null && status.equals("PAID") ? View.VISIBLE : View.GONE);
            });
        });
    }
}
