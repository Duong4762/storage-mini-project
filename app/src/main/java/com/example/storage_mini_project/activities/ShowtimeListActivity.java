package com.example.storage_mini_project02_v1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_mini_project02_v1.R;
import com.example.storage_mini_project02_v1.adapters.ProductAdapter;
import com.example.storage_mini_project02_v1.entities.Product;

import java.util.List;

public class ShowtimeListActivity extends AppCompatActivity {
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_CATEGORY_NAME = "category_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        TextView tvProductListTitle = findViewById(R.id.tvProductListTitle);

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1);
        String categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);
        tvProductListTitle.setText(categoryName != null ? categoryName : "Danh sách sản phẩm");

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        ProductAdapter adapter = new ProductAdapter(product -> {
            Intent detailIntent = new Intent(ShowtimeListActivity.this, ShowtimeDetailActivity.class);
            detailIntent.putExtra("product_id", product.getId());
            startActivity(detailIntent);
        });
        rvProducts.setAdapter(adapter);

        ProductViewModel viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        viewModel.getProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.submitList(products);
            }
        });
        viewModel.loadProductsByCategory(categoryId);
    }
}
