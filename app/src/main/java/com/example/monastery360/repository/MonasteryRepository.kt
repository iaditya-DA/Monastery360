package com.example.monastery360.repository

import com.example.monastery360.R
import com.example.monastery360.model.Monastery

object MonasteryRepository {

    private val allMonasteries = listOf(
        Monastery(
            imageRes = R.drawable.bg2,
            name = "Rumtek Monastery",
            distance = "24 km away",
            rating = 4.9f,
            reviews = 455,
            location = "Gangtok, Sikkim",
            address = "Gangtok, East Sikkim",
            description = "Rumtek Monastery, located in Sikkim, is one of the most awe-inspiring monasteries in the region. Built in traditional Buddhist architectural style with stunning mountain views and rich cultural heritage. The monastery is known for its beautiful murals, golden stupa, and peaceful atmosphere.",
            price = "₹500/person"
        ),
        Monastery(
            imageRes = R.drawable.bg3,
            name = "Pemayangtse Monastery",
            distance = "110 km away",
            rating = 4.7f,
            reviews = 328,
            location = "Pelling, West Sikkim",
            address = "Pelling, West Sikkim",
            description = "Ancient monastery with rich Buddhist history and breathtaking views of Kanchenjunga. One of the oldest and most important monasteries in Sikkim, featuring intricate wood carvings and traditional architecture.",
            price = "₹300/person"
        ),
        Monastery(
            imageRes = R.drawable.bg1,
            name = "Tashiding Monastery",
            distance = "85 km away",
            rating = 4.6f,
            reviews = 290,
            location = "West Sikkim",
            address = "Tashiding, West Sikkim",
            description = "Sacred Buddhist pilgrimage site located on a hilltop with panoramic views of the Himalayas. The monastery is known for its spiritual significance and hosts the annual Bumchu festival.",
            price = "₹200/person"
        ),
        Monastery(
            imageRes = R.drawable.bg4,
            name = "Enchey Monastery",
            distance = "3 km away",
            rating = 4.4f,
            reviews = 412,
            location = "Gangtok, Sikkim",
            address = "Gangtok, East Sikkim",
            description = "Beautiful monastery with stunning mountain views, known for its peaceful atmosphere and religious significance. The monastery hosts colorful masked dance performances during festivals.",
            price = "₹150/person"
        ),
        Monastery(
            imageRes = R.drawable.bg1,
            name = "Dubdi Monastery",
            distance = "150 km away",
            rating = 4.5f,
            reviews = 186,
            location = "Yuksom, West Sikkim",
            address = "Yuksom, West Sikkim",
            description = "Oldest monastery in Sikkim, built in 1701. A must-visit for history enthusiasts and spiritual seekers. The monastery offers a glimpse into Sikkim's rich Buddhist heritage.",
            price = "₹250/person"
        ),
        Monastery(
            imageRes = R.drawable.bg2,
            name = "Ralang Monastery",
            distance = "65 km away",
            rating = 4.3f,
            reviews = 201,
            location = "South Sikkim",
            address = "Ravangla, South Sikkim",
            description = "Peaceful monastery near Ravangla offering serene environment for meditation and prayer. The monastery is surrounded by lush green forests and offers spectacular views.",
            price = "₹180/person"
        ),
        Monastery(
            imageRes = R.drawable.bg3,
            name = "Phensang Monastery",
            distance = "45 km away",
            rating = 4.4f,
            reviews = 156,
            location = "Kabi, North Sikkim",
            address = "Kabi, North Sikkim",
            description = "Serene hilltop monastery with beautiful architecture and peaceful surroundings. The monastery is known for its tranquil environment and traditional Buddhist practices.",
            price = "₹220/person"
        ),
        Monastery(
            imageRes = R.drawable.bg4,
            name = "Do-Drul Chorten",
            distance = "2 km away",
            rating = 4.6f,
            reviews = 523,
            location = "Gangtok, Sikkim",
            address = "Gangtok, East Sikkim",
            description = "Important stupa in Gangtok surrounded by 108 prayer wheels. A significant religious landmark visited by devotees and tourists alike. The stupa contains important Buddhist relics.",
            price = "₹100/person"
        ),
        Monastery(
            imageRes = R.drawable.bg1,
            name = "Phodong Monastery",
            distance = "38 km away",
            rating = 4.5f,
            reviews = 267,
            location = "North Sikkim",
            address = "Phodong, North Sikkim",
            description = "Historic monastery with beautiful murals and traditional Buddhist art. The monastery is known for its annual masked dance festival and stunning mountain backdrop.",
            price = "₹200/person"
        ),
        Monastery(
            imageRes = R.drawable.bg2,
            name = "Sangachoeling Monastery",
            distance = "135 km away",
            rating = 4.4f,
            reviews = 178,
            location = "Pelling, West Sikkim",
            address = "Pelling, West Sikkim",
            description = "Second oldest monastery in Sikkim with spectacular views of Mount Kanchenjunga. The monastery offers a peaceful retreat and stunning views of the surrounding landscapes.",
            price = "₹280/person"
        )
    )

    fun getPreviewList(): List<Monastery> = allMonasteries.take(5)

    fun getAllMonasteries(): List<Monastery> = allMonasteries
}