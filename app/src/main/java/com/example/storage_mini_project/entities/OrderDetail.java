package com.example.storage_mini_project.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "order_details",
    primaryKeys = {"order_id", "product_id"},
    foreignKeys = {
        @ForeignKey(
            entity = Order.class,
            parentColumns = "id",
            childColumns = "order_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Product.class,
            parentColumns = "id",
            childColumns = "product_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = "order_id"),
        @Index(value = "product_id")
    }
)
public class OrderDetail {

    @ColumnInfo(name = "order_id")
    private int orderId;

    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "unit_price")
    private double unitPrice;

    public OrderDetail() {}

    public OrderDetail(int orderId, int productId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
