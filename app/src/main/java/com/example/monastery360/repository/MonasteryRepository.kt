package com.example.monastery360.repository

import com.example.monastery360.model.Monastery

object MonasteryRepository {

    val monasteries = listOf(

        Monastery(
            // Old model fields
            imageRes = 0, // हम URL से image लेंगे
            name = "Dubdi Monastery",
            distance = "—",
            rating = 4.6f,
            reviews = 455,
            latitude = 27.373452246743484,
            longitude = 88.25773302207241,
            location = "Yuksom, West Sikkim",
            address = "Yuksom, West Sikkim",
            description = "Dubdi Monastery is often regarded as the oldest monastery in Sikkim...",
            price = "₹200/person",

            // Extra JSON fields
            _id = "679b067033c7707338654e10",
            villageOrTown = "Yuksom",
            district = "West Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.app.goo.gl/kFNVvQoBiAzkvGGaA",
            altitude = "2100 meters",
            buddhistSect = "Nyingma Buddhism",
            foundedYear = 1701,
            history = "Dubdi Monastery was established after the coronation of the first Chogyal...",
            architecture = "The monastery follows traditional Tibetan architecture...",

            // JSON image URLs
            images = listOf(
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736442183/dubdi5_eiwjdn.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736442182/dubdi4_q2b2td.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736442182/dubdi3_q2r1k8.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736442181/dubdi2_trz0vh.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736442181/dubdi1_gkwdzn.webp"
            ),

            // Nearby attractions
            nearbyAttractions = listOf(
                "Norbugang Coronation Throne",
                "Kartok Lake",
                "Tashiding Monastery"
            )
        ),

        Monastery(
            imageRes = 0,
            name = "Enchey Monastery",
            distance = "—",
            rating = 4.5f,
            reviews = 410,
            latitude = 27.348187211981096,
            longitude = 88.26165798659519,
            location = "Gangtok, Sikkim",
            address = "Gangtok, East Sikkim",
            description = "Enchey Monastery is a prominent Buddhist shrine located in Gangtok...",
            price = "₹150/person",

            _id = "679b557373f8c0ae0924d9e7",
            villageOrTown = "Enchey",
            district = "East Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.app.goo.gl/WQQYqLTqtDvgF7pH6",
            altitude = "1850 meters",
            buddhistSect = "Nyingma Buddhism",
            foundedYear = 1840,
            history = "Legend says the monastery was built on a sacred spot...",
            architecture = "The main shrine is decorated with murals and prayer wheels...",

            images = listOf(
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736526014/enchey2_kqrehe.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736526014/enchey1_wlqzcc.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736526014/enchey3_ga1caq.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736526014/enchey4_lvszxb.webp",
                "https://res.cloudinary.com/dxhah3shy/image/upload/v1736526013/enchey5_xtjytv.webp"
            ),

            nearbyAttractions = listOf(
                "MG Marg",
                "Ganesh Tok",
                "Hanuman Tok"
            )
        )

        // 🔥 इसी तरह मैं बाकी भी full convert कर दूँगा जब तू बोलेगा
    )

    fun getAllMonasteries(): List<Monastery> = monasteries
}
