package com.example.gachaproductiveapp.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTaskTier(tier: TaskTier): String = tier.name

    @TypeConverter
    fun toTaskTier(value: String): TaskTier = TaskTier.valueOf(value)

    @TypeConverter
    fun fromRarity(rarity: Rarity): String = rarity.name

    @TypeConverter
    fun toRarity(value: String): Rarity = Rarity.valueOf(value)
}