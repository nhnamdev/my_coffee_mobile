package com.example.mycoffeeapp.Activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.CartAdapter
import com.example.mycoffeeapp.Helper.ManagmentOrder
import com.example.mycoffeeapp.databinding.ActivityCartBinding
import com.example.project1762.Helper.ManagmentCart
import com.uilover.project195.Helper.ChangeNumberItemsListener

class CartActivity : AppCompatActivity(){
    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart
    lateinit var managmentOrder: ManagmentOrder
    private var tax: Double = 0.0

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
        
        binding.button3.setOnClickListener {
            val cartItems = managmentCart.getListCart()
            if (cartItems.isNotEmpty()) {
                val totalAmount = managmentCart.getTotalFee() + tax + 15 // 15 is delivery fee
                managmentOrder.insertOrder(cartItems, totalAmount, tax, 15.0)
                managmentCart.getListCart().clear() // Clear cart after successful payment
                // Chuyển đến màn hình thanh toán
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("total_amount", totalAmount)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initCartList() {
        binding.apply {
            cartView.layoutManager=
                LinearLayoutManager(this@CartActivity,LinearLayoutManager.VERTICAL,false)
            cartView.adapter = CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object :ChangeNumberItemsListener{
                    override fun onChanged() {
                        calculateCart()
                    }
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        managmentOrder = ManagmentOrder(this)

        calculateCart()
        setVariable()
        initCartList()
    }
    
    private fun calculateCart(){
        val percentTax=0.02
        val delivery=15
        tax= (Math.round(managmentCart.getTotalFee()*percentTax)*100).toDouble()/100.0
        val total = Math.round((managmentCart.getTotalFee() + tax + delivery)*100)/100
        val itemTotal = Math.round(managmentCart.getTotalFee()*100)/100
        binding.apply {
            totalFeeTxt.text = "$itemTotal" + " VNĐ"
            taxTxt.text = "$tax" + " VNĐ"
            deliveryTxt.text = "$delivery" + " VNĐ"
            totalTxt.text = "$total" + " VNĐ"
        }
    }
}