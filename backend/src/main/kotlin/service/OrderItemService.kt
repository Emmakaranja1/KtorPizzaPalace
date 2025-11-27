package com.emmascode.services

import com.emmascode.repository.OrderItemRepository
import com.emmascode.dto.*

class OrderItemService(private val repository: OrderItemRepository = OrderItemRepository()) {
    fun getAllOrderItems() = repository.findAll()
    fun getOrderItemById(id: Int) = repository.findById(id)
    fun getOrderItemsByOrderId(orderId: Int) = repository.findByOrderId(orderId)

    fun updateOrderItem(id: Int, dto: UpdateOrderItemDTO): OrderItemDTO? {
        return repository.update(id, dto.quantity, dto.specialInstructions)
    }

    fun deleteOrderItem(id: Int) = repository.delete(id)
}