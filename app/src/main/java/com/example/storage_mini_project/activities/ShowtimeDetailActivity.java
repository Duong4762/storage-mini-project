package com.example.storage_mini_project02_v1.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.storage_mini_project02_v1.R;
import com.example.storage_mini_project02_v1.dal.AppDB;
import com.example.storage_mini_project02_v1.entities.Product;
import java.util.concurrent.Executors;

public class ShowtimeDetailActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = this;

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(context, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvProductNameBar = findViewById(R.id.tvProductNameBar);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvProductDescription = findViewById(R.id.tvProductDescription);
        TextView tvQuantity = findViewById(R.id.tvQuantity);
        ImageButton btnDecrease = findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = findViewById(R.id.btnIncrease);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        Executors.newSingleThreadExecutor().execute(() -> {
            Product product = AppDB.getInstance(context.getApplicationContext()).productDAO().getById(productId);
            runOnUiThread(() -> {
                if (product == null) {
                    Toast.makeText(context, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                tvProductNameBar.setText(product.getName());
                tvProductName.setText(product.getName());
                tvProductPrice.setText(String.format("%,.0f đ", product.getPrice()));
                tvProductDescription.setText(product.getDescription());
                Glide.with(context)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgProduct);
            });
        });

        // Xử lý tăng giảm số lượng
        final int[] quantity = {1};
        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });
        btnIncrease.setOnClickListener(v -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(context, "Đã thêm vào giỏ hàng (demo)", Toast.LENGTH_SHORT).show();
        });
    }
}
