package com.example.taskflow.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey val id : String,
    val title: String,
    val status :String = "Pending"
)