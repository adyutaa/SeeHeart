package com.marshielo.seeheart

data class ReminderItem(
    val iconResId: Int,
    val description: String,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is ReminderItem) {
            this.description == other.description
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return description.hashCode()
    }
}
