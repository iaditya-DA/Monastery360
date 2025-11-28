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
            description = "Dubdi Monastery, also known as Yuksom Monastery, is one of the oldest and most historically significant monasteries in Sikkim. Established in 1701 by Chogyal Namgyal, the first king of Sikkim, it stands as a symbol of the origins of Sikkimese Buddhism. Located on a quiet hilltop above Yuksom, the monastery is surrounded by dense forests, soft mountain breeze, and peaceful natural beauty. Built in traditional Tibetan style, it features whitewashed walls, a tiered golden bell-shaped roof, ancient manuscripts, and beautifully preserved statues of Buddhist deities. The walk to the monastery itself is scenic and calming, passing through forest trails and serene landscapes. Known as the “Hermit’s Cell,” Dubdi Monastery played a key role in the coronation of the first Sikkimese king and is deeply tied to the spiritual history of the region. Today, it is admired for its tranquility, heritage value, and its timeless atmosphere that lets visitors experience the early roots of Buddhism in Sikkim.",
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
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083672/dubdi5_ny7rqv.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083672/dubdi4_di708f.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083669/dubdi3_ez9xsv.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083666/dubdi2_mwaumy.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083666/dubdi1_qhqi9s.webp"

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
            description = "Enchey Monastery, located on a quiet hill above Gangtok, is one of Sikkim’s most important and spiritually revered monasteries. Built over 200 years ago and later renovated in 1909, it belongs to the Nyingma sect of Tibetan Buddhism. The name “Enchey” means “the solitary temple,” reflecting its peaceful and secluded setting surrounded by pine trees and panoramic views of the Kanchenjunga range. According to local belief, the monastery stands on a blessed site consecrated by the famous tantric master Lama Drupthob Karpo, known for his flying powers. Architecturally, Enchey showcases traditional Bhutanese-style design with vibrant colours, carved windows, and a sacred prayer hall with statues of Guru Padmasambhava, Lokeshwara, and Buddha. The monastery is also famous for the annual Cham Dance (mask dance) performed by monks during Losoong and Pang Lhabsol festivals. Calm, historic, and spiritually rich, Enchey Monastery offers a perfect blend of culture, faith, and Himalayan serenity.",
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

                    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083676/enchey2_mabh0c.webp",
                    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083675/enchey1_qziej3.webp",
                    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083679/enchey3_zrosqx.webp",
                    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083680/enchey4_rj7mec.webp",
                    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083682/enchey5_lek75x.webp"
                ),



            nearbyAttractions = listOf(
                "MG Marg",
                "Ganesh Tok",
                "Hanuman Tok"
            )
        ),


    Monastery(
            imageRes = 0,
    name = "Gonjang Monastery",
    distance = "—",
    rating = 4.5f,
    reviews = 0,
    latitude = 27.369,
    longitude = 88.6136,
    location = "Near Tashi View Point, Gangtok",
    address = "East Sikkim",
    description = "Gonjang Monastery is a relatively modern but significant monastery located near the famous Tashi View Point, just a short drive from Gangtok. It was established in 1981 A.D. by H.E. Tingkye Gonjang Rimpoche, and belongs to the Nyingma sect, maintaining a strong connection to the lineage of the 4th Gonjang Rimpoche. Despite its recent founding, it has quickly become a vital centre for the local Buddhist community and a popular stop for tourists visiting the Tashi View Point.\n\nThe monastery is beautifully constructed with vibrant colors and traditional Tibetan architectural elements. The main assembly hall is spacious and decorated with murals depicting scenes from Buddhist scriptures and the lives of past masters. It houses magnificent statues of Guru Padmasambhava, Lord Buddha, and other deities important to the Nyingma tradition. The monastery complex also includes accommodation for the young monks who reside there and receive traditional Buddhist education.\n\nOne of the greatest appeals of Gonjang Monastery is its elevated location, which offers panoramic, breathtaking views of the surrounding Himalayan peaks, including Mount Khangchendzonga, especially during sunrise and sunset. The peaceful environment provides an excellent setting for meditation and spiritual contemplation. The monastery regularly hosts prayer sessions and religious ceremonies, which visitors are often welcome to observe. Its accessibility and scenic setting make it an essential addition to any spiritual or cultural itinerary in East Sikkim.",
    price = "₹150/person",

    _id = "6926a772bed139586fbb2ac4",
    villageOrTown = "Near Tashi View Point, Gangtok",
    district = "East Sikkim",
    state = "Sikkim",
    googleMapsLink = "https://www.google.com/search?q=https://maps.google.com/%3Fcid%3D5539637463465575891%26entry%3Dgps8",
    altitude = "2,050m (6,725 ft) approx.",
    buddhistSect = "Nyingma",
    foundedYear = 1981,
    history = "Gonjang Monastery, though relatively modern compared to the ancient monasteries of Sikkim, carries significant spiritual relevance. It was founded in 1981 A.D. by H.E. Tingkye Gonjang Rinpoche, who is considered the 16th incarnation in the revered lineage of the Gonjang Rinpoche tradition. This lineage traces back many centuries, giving the monastery a strong foundation in the ancient heritage of Tibetan Buddhism despite its recent establishment.\n\nThe monastery was founded with a vision to preserve and promote the teachings of the Nyingma school, particularly the traditions brought to Sikkim by the early lamas such as Lhatsun Chenpo. Gonjang Monastery also emphasizes scholastic and philosophical training for young monks. Over the years, it has developed into a major educational center, offering a complete monastic curriculum that includes Buddhist philosophy, ritual practice, meditation, and Tibetan language studies.\n\nIts location near Tashi View Point was intentionally chosen for its spiritual energy and panoramic views of the Himalayan range, including Mount Khangchendzonga. The monastery’s growth has been steady, attracting followers, students, and tourists from around the world. It has also become known for hosting special prayer ceremonies, retreats, and annual rituals dedicated to Guru Padmasambhava and other deities of the Nyingma tradition.\n\nToday, Gonjang Monastery stands as a bridge between the old and the new—preserving ancient teachings while functioning as an important contemporary cultural and educational institution in East Sikkim.",
    architecture = "Gonjang Monastery is an excellent example of modern Tibetan architecture inspired by classical monastic designs. While constructed in 1981, the monastery maintains the grandeur, color, and symbolism typical of traditional Himalayan monasteries. Its striking exterior features bold red, yellow, and white hues, intricate wooden window frames, and detailed cornices adorned with auspicious motifs such as lotus petals, dragons, and wheel-of-dharma symbols.\n\nThe main assembly hall is spacious and adorned with vibrant murals illustrating various deities, protectors, and historical scenes from the lives of Buddhist masters. These paintings follow classical Tibetan styles, emphasizing symmetrical patterns and deep spiritual symbolism. The central shrine room houses large, intricately crafted statues of Guru Padmasambhava, Lord Buddha, and other deities important to the Nyingma lineage. The craftsmanship of these statues reflects both traditional artistry and modern precision.\n\nThe monastery complex also includes residential quarters for monks, classrooms, a meditation hall, and a courtyard used for prayer gatherings and ceremonial rituals. Its elevated location enhances the architectural experience—providing natural lighting, open spaces, and stunning panoramic views of Gangtok and the snow-clad Himalayan peaks. The entire design emphasizes harmony between the structure and its surroundings, creating a peaceful and contemplative environment suitable for monastic life.\n\nThough built recently, Gonjang Monastery harmonizes authenticity with modern functionality, making it a unique architectural landmark in East Sikkim.",

    images = listOf(
    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083692/gonjgang2_oxclov.webp",
    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083691/Gonjang1_eypslg.webp",
    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083688/gongjang5_jiqicq.webp",
    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083686/gongjang4_imgnpt.webp",
    "https://res.cloudinary.com/djeospbqe/image/upload/v1764083684/gongjang3_gdnitg.webp"
    ),

    nearbyAttractions = listOf(
    "Tashi View Point",
    "Ganesh Tok",
    "Himalayan Zoological Park",
    "Enchey Monastery",
    "Hanuman Tok",
    "Bakthang Waterfall"
    )
    ),
        Monastery(
            imageRes = 0,
            name = "Kewzing Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,
            latitude = 27.2518,
            longitude = 88.3627,
            location = "Kewzing Village, South Sikkim",
            address = "Kewzing Village, South Sikkim",
            description = "Kewzing Monastery, located in the quiet, scenic Kewzing village in South Sikkim, is a charming and historically significant monastery belonging to the Nyingma sect of Buddhism. It was established in 1888 A.D. and stands as a testament to the strong Buddhist traditions maintained by the local Lepcha and Bhutia communities in the area. The name 'Kewzing' itself is derived from the local Lepcha dialect, meaning 'land of the wheat field.'\n\nThe monastery complex features traditional Sikkimese and Tibetan architecture, characterized by a sloping roof, intricate woodwork, and a main assembly hall (Lhakhang) adorned with vibrant frescoes and religious iconography. Inside the Lhakhang, visitors can observe ancient Thangkas and statues of Guru Padmasambhava.\n\nKewzing Monastery is known for its peaceful atmosphere and serves as a key spiritual centre for the surrounding villages. The annual Chaam festival, held in December, attracts devotees who witness ritual masked dances performed by monks to ward off evil spirits and invoke blessings.",
            price = "",

            _id = "6926a780bed139586fbb2ac5",
            villageOrTown = "Kewzing Village",
            district = "South Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://www.google.com/search?q=https://maps.google.com/%3Fcid%3D5539637463465575891%26entry%3Dgps8",
            altitude = "1,550m (5,085 ft) approx.",
            buddhistSect = "Nyingma",
            foundedYear = 1888,
            history = "Kewzing Monastery, founded in 1888 A.D., is an important spiritual centre for the remote and culturally rich Kewzing village in South Sikkim. The monastery was built by the local Buddhist community to preserve the teachings of the Nyingma tradition—the oldest school of Tibetan Buddhism.\n\nHistorically, the monastery played a crucial role in maintaining the religious and cultural identity of the Lepcha community. It also served as a place where monks trained in Buddhist philosophy, rituals, and meditation. Over time, it became a focal point for local festivals and oral traditions.\n\nDespite being smaller than major monasteries of Sikkim, it maintains an unbroken lineage of monastic leadership. The annual Chaam festival highlights its historical significance through traditional masked dances that date back centuries.",
            architecture = "Kewzing Monastery features traditional Tibetan and Sikkimese architecture with a sloping roof, carved wooden beams, and symbolic decorative motifs. The design reflects early Nyingma monastic influence, focusing on simplicity, spiritual depth, and harmony with nature.\n\nThe main prayer hall (Lhakhang) features frescoes, Thangkas, and statues of Guru Padmasambhava and Buddha Shakyamuni. Surrounding monk quarters and meditation rooms maintain classic stone-and-timber construction. The compact layout includes an open courtyard used for ceremonies and the Chaam masked dances.\n\nIts elevated location allows natural light to enhance the beauty of murals and statues, adding to the peaceful monastic setting.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083701/kew5_f0wkul.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083700/kew4_pkbiqs.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083697/kew3_keiebu.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083695/kew2_n26zfi.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083693/kew1_wettup.webp"
            ),

            nearbyAttractions = listOf(
                "Doling Monastery",
                "Ravangla (Buddha Park)",
                "Bon Monastery, Kewzing",
                "Tendong Hill",
                "Ralang Monastery",
                "Maenam Wildlife Sanctuary"
            )
        ),
        Monastery(
            imageRes = 0,
            name = "Labrang Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,
            latitude = 27.41806,
            longitude = 88.57944,
            location = "Near Phodong, North Sikkim Highway",
            address = "Near Phodong, North Sikkim, Sikkim",
            description = "Labrang Monastery is a significant religious site located near Phodong, just off the North Sikkim Highway. Established in 1814 A.D., it belongs to the Nyingma sect, setting it apart from the nearby Phodong Monastery, which follows the Kagyu sect. This unique coexistence of Nyingma and Kagyu monasteries in close proximity reflects the region's history of religious harmony and cultural depth.\n\nThe monastery was built by the eighth Chogyal of Sikkim, Tsugphud Namgyal, in memory of his tutor Lama Jamyang Tashi. Labrang Monastery also played an important historical role, as it was the site where the coronation of the eleventh Chogyal of Sikkim, Tashi Namgyal, took place in 1914.\n\nKnown for its peaceful setting and traditional Tibetan architecture, the monastery features intricate woodwork, vibrant murals, and beautifully painted frescoes. Its calm surroundings and forested hillside location make it an ideal place for spiritual retreat and meditation. Though smaller than some major monasteries in the region, Labrang remains an important spiritual center for the Nyingma lineage.",
            price = "",

            _id = "6926a793bed139586fbb2ac6",
            villageOrTown = "Near Phodong",
            district = "North Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.google.com/?cid=10890525437266382408&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQ",
            altitude = "1,700m (5,577 ft) approx.",
            buddhistSect = "Nyingma",
            foundedYear = 1814,
            history = "Labrang Monastery, also known as Palden Phuntshog Phodrang, was constructed in memory of Lhatsun Chempo, an eminent lama from Kongpo, Tibet. While many sources cite 1814 A.D. as its founding year, others suggest construction began in 1826 and was completed around 1843. It was initiated by Gyalshe Rigzing Chempa of the Sikkimese royal family to honor his spiritual teacher.\n\nThe monastery has long served as a hub of religious education and spiritual practice for the Nyingma sect in North Sikkim. It has withstood natural calamities over the centuries and underwent a major government-supported renovation in 1978, which preserved its authentic architecture while strengthening the structure. Labrang continues to be a repository of ancient scriptures, murals, and sacred ritual objects.",
            architecture = "Labrang Monastery showcases classical Tibetan monastic architecture, enhanced by Sikkimese craftsmanship. Located on a quiet forested hill, the monastery is built with whitewashed walls, sloping roofs, carved wooden beams, and brightly colored window frames. The prayer hall features richly detailed murals and frescoes depicting Buddhist deities, lineage masters, and Vajrayana cosmology.\n\nInside, a prominent bronze statue of Karma Guru (a form of Guru Padmasambhava) stands surrounded by ancient manuscripts and ritual objects. The monastery complex also includes a small museum-like structure displaying scriptures, printing blocks, and historical artifacts. Recent renovations incorporated steel framing for structural safety while carefully preserving the traditional aesthetics.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083712/lab6_xcmzfr.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083710/lab4_wilffo.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083708/lab3_tfnlka.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083706/lab2_soktmy.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083705/lab1_fgkyad.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083703/laab5_ndifqd.webp"
            ),

            nearbyAttractions = listOf(
                "Phodong Monastery",
                "Tumlong Palace Ruins",
                "Kabi Longstok",
                "Hilltop viewpoints",
                "North Sikkim ridge trails"
            )
        ),
        Monastery(
            imageRes = 0,
            name = "Phodong Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,

            latitude = 27.3833,
            longitude = 88.6135,

            location = "Phodong, North Sikkim Highway",
            address = "Phodong, North Sikkim Highway",
            description = "Phodong Monastery is one of the six most important monasteries in Sikkim, historically significant for its proximity to the former capital, Tumlong. It is a major monastery of the Kagyu sect, established by Chogyal Gyurmed Namgyal in 1740 A.D. The original structure remains an important religious landmark, with the main complex rebuilt in the early 20th century to accommodate a growing community.\n\nRenowned for splendid murals, exquisite frescoes, and ancient wall paintings, Phodong houses over 260 monks and serves as a major center for Buddhist learning in North Sikkim. It hosts the annual Kagyed Chaam masked dance festival in December, featuring monks in silk costumes and masks performing ritual dances symbolizing the destruction of evil forces and welcoming prosperity. The monastery's peaceful surroundings amid green hills make it a tranquil location for visitors.",
            price = "",

            _id = "6926a7a1bed139586fbb2ac7",
            villageOrTown = "Phodong, North Sikkim Highway",
            district = "North Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.google.com/?cid=17267254591658616209&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQ&authuser=2",
            altitude = "1,500m (4,920 ft) approx.",
            buddhistSect = "Kagyu",
            foundedYear = 1740,

            history = "Phodong Monastery is one of the six premier monasteries of Sikkim and holds immense historical significance due to its proximity to Tumlong, the former capital. Established around 1740 A.D. by Chogyal Gyurmed Namgyal, it served as a major spiritual seat for the Karma Kagyu lineage and a hub for monastic education.\n\nThe old structure deteriorated over time, but in the early 20th century it was extensively rebuilt under royal patronage, preserving cultural traditions. Phodong became home to more than 260 monks, who uphold ancient rituals and Buddhist scholastic learning. Alongside Labrang and Tumlong monasteries, it shaped North Sikkim's religious landscape and was accessible to pilgrims and travelers from Tibet and Bhutan.\n\nToday, it remains a living testament to Sikkim’s Buddhist heritage, with its Kagyed Chaam festival drawing large crowds and preserving centuries-old traditions.",

            architecture = "Phodong Monastery is acclaimed for vibrant murals, intricate frescoes, and traditional monastic craftsmanship. The main prayer hall features brightly colored murals depicting Buddhist cosmology, lineage masters, deities, and Kagyu symbols. The architecture follows classic Tibeto-Sikkimese style with multi-tiered roofs, carved wooden windows, and facades painted in deep reds, yellows, and whites. The entrance showcases guardian deities and protective symbols.\n\nThe Dukhang contains statues of Buddha Shakyamuni, Guru Rinpoche, Karma Kagyu lamas, and protector deities. Ancient scriptures and relics are preserved in the sacred treasury. The courtyard serves as the venue for the Kagyed Chaam ritual dances. The complex includes monk quarters, meditation rooms, and prayer wheels. Nestled in lush green hills, Phodong’s architecture blends beautifully with its natural surroundings, offering a serene environment.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083724/phod6_hwnqji.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083722/phod5_nwjts6.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083722/phod5_nwjts6.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083719/phod4_irqb6w.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083718/phod3_opkblj.webp"
            ),

            nearbyAttractions = listOf(
                "Labrang Monastery",
                "Seven Sisters Waterfall",
                "Mangan Town",
                "Kabi Lungchok",
                "Phensang Monastery",
                "Gangtok City"
            )
        ),
        Monastery(
            imageRes = 0,
            name = "Rinchenpong Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,

            latitude = 27.3196,
            longitude = 88.2073,

            location = "Rinchenpong",
            address = "Rinchenpong, West Sikkim",
            description = "Rinchenpong Monastery is a historic and serene monastery located near the village of Rinchenpong in West Sikkim, offering stunning views of the Khangchendzonga range. Founded in 1730 A.D., it belongs to the Kagyu order of Tibetan Buddhism. The monastery is relatively small but holds significant historical importance due to its connection with the early spread of Buddhism in the region.\n\nIt is famously known for its sacred statue of Lord Buddha and its association with a local legend involving the British. According to the legend, local forces used poisonous water near the British camp during the Anglo-Sikkimese War to prevent their advance. The site is peaceful, surrounded by dense forests including the unique Poison Oak tree, a local attraction.\n\nThe monastery hosts an annual festival with traditional Chaam masked dances performed by the monks. Visitors appreciate the religious artifacts, tranquility, and spectacular natural scenery. Its elevated position provides one of the best views of the surrounding mountain peaks and valleys in West Sikkim.",
            price = "",

            _id = "6926a7b3bed139586fbb2ac8",
            villageOrTown = "Rinchenpong",
            district = "West Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.google.com/?cid=6224151835828090122&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQ&authuser=2",
            altitude = "1,740m (5,709 ft) approx.",
            buddhistSect = "Kagyu",
            foundedYear = 1730,

            history = "Rinchenpong Monastery, founded in 1730 A.D., is one of the historically significant monasteries of West Sikkim. Built during a period of Buddhist expansion in the region, it belongs to the Kagyu school, one of the oldest Tibetan Buddhist lineages. Its establishment is linked to the rise of Buddhism in the Rinchenpong–Kaluk area, once a key trade and cultural route.\n\nDuring the Anglo-Sikkimese War, locals used a poisonous plant's sap to poison a water source near the British camp, halting their advance. This event is commemorated by the nearby Poison Lake. Over centuries, Rinchenpong Monastery has remained small but culturally important, housing ancient scriptures, relics, and a revered statue of Lord Buddha in the Maitreya form.",

            architecture = "Rinchenpong Monastery reflects traditional Kagyu architectural principles combined with the simplicity of early Sikkimese monastic buildings. Situated on a gentle hilltop surrounded by dense forest, the monastery blends with its natural setting. Its exterior is stone and wood with a sloped tin roof suited for Himalayan weather.\n\nThe prayer hall features colorful murals of Buddhist deities and scenes from the Buddha's life. The inner sanctum houses a serene statue of Maitreya Buddha, one of the oldest in the region. Walls display thangkas, and wooden beams have hand-carved auspicious motifs. The simple design highlights the monastery's role as a spiritual retreat.\n\nNearby stands the famous Poison Oak tree, tied to local legend. The grounds include a small courtyard for prayers and Chaam ritual dances, creating an atmosphere of quiet contemplation distinct from grander monasteries.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083733/rinc5_fdaxpk.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083730/rinc4_epsek6.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083729/rinc3_djdnlh.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083727/rinc2_ufilqk.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083725/rinc1_wpewta.webp"
            ),

            nearbyAttractions = listOf(
                "Poison Lake",
                "Kaluk Viewpoint",
                "Reesum Monastery",
                "Rabindra Smriti Van",
                "Trek routes around Rinchenpong & Bermiok Forest"
            )
        ),
        Monastery(
            imageRes = 0,
            name = "Rumtek Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,

            latitude = 27.3153,
            longitude = 88.3893,

            location = "Rumtek, near Gangtok",
            address = "Rumtek, near Gangtok, East Sikkim",
            description = "Rumtek Monastery, officially the Dharma Chakra Centre, is the largest and most important monastery in Sikkim. Located 24 km from Gangtok on a ridge overlooking the valley, it serves as the main seat-in-exile of the Gyalwa Karmapa, head of the 900-year-old Karma Kagyu lineage.\n\nOriginally founded in 1734 by the 9th Karmapa, the monastery fell into ruin until the 1960s when the 16th Karmapa built the current complex after fleeing Tibet. It is a replica of the Tsurphu Monastery in Tibet and includes the main temple, monks' quarters, a retreat center, and the Karma Shri Nalanda Institute for Higher Buddhist Studies.\n\nThe main shrine is a three-story structure adorned with murals, thangkas, and ancient manuscripts. The Golden Stupa contains relics of the 16th Karmapa. Pilgrims and tourists visit for its spiritual importance, serene environment, and stunning views. Prayer flags and wheels line the walkways, creating a contemplative mood.",
            price = "",

            _id = "6926a7c2bed139586fbb2ac9",
            villageOrTown = "Rumtek, near Gangtok",
            district = "East Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://www.google.com/earth/rpc/entity?lat=27.3052133&lng=88.25156179999999&fid=0x39e685d35d570dcf:0xbba9d8f51db0ca38&q=Pemayangtse&authuser=2",
            altitude = "1,662m (5,450 ft)",
            buddhistSect = "Kagyu",
            foundedYear = 1962,

            history = "Rumtek Monastery has a rich history dating to 1734, founded by the 9th Karmapa, Wangchuk Dorje. The original monastery eventually fell into ruin. After the Chinese invasion of Tibet in 1959, the 16th Karmapa, Rangjung Rigpe Dorje, fled Tibet and chose Rumtek as the new seat-in-exile for the Karma Kagyu lineage.\n\nThe present monastery was built in the 1960s with support from the Sikkim royal family, devotees, and the Government of India. Modeled after the Tsurphu Monastery in Tibet, it became a global center for Buddhist teachings, rituals, and culture. It houses relics, sacred texts, and the relic-stupa of the 16th Karmapa, serving as a refuge and spiritual hub.",

            architecture = "The architecture of Rumtek Monastery embodies grandeur and Tibetan Buddhist symbolism. The large complex is built on a slope with commanding views. The main shrine is a three-story structure modeled on Tsurphu Monastery, featuring vibrant colors, high walls, golden finials, and intricate woodwork.\n\nThe prayer hall holds large golden statues of Buddha, Guru Padmasambhava, and lineage masters, surrounded by thangkas and mandalas. Brocade-wrapped pillars add warmth. The Golden Stupa enshrines the 16th Karmapa's relics.\n\nFacilities include the Karma Shri Nalanda Institute for Buddhist Studies, monks' quarters, a courtyard for rituals, and a retreat center. Prayer wheels and fluttering prayer flags enhance spiritual ambiance. The architecture balances tradition with structural reinforcements for Himalayan conditions.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083744/rumtek6_pibljh.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083741/rumtek5_p11vuj.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083740/rumtek4_pqq3vq.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083738/rumtek3_otmetl.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083736/rumtek2_nazthl.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083734/rumtek1_maqs0e.webp"
            ),

            nearbyAttractions = listOf(
                "Lingdum (Ranka) Monastery",
                "Nehru Botanical Garden",
                "Ban Jhakri Falls Park",
                "Gangtok Ropeway",
                "Jawaharlal Nehru Institute of Mountaineering",
                "Rumtek Dharma Chakra Centre Park"
            )
        ),
        Monastery(
            imageRes = 0,
            name = "Sangachoeling Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,

            latitude = 27.3036,
            longitude = 88.2208,

            location = "Pelling",
            address = "Pelling, West Sikkim",
            description = "Sangachoeling Monastery, also spelled Sange Choeling, is considered the second oldest monastery in Sikkim, founded in 1697 A.D. The name means \"Island of the Guhyamantra teachings\" or \"Place of Secret Spells,\" referring to Vajrayana Buddhism. Established by Lama Lhatsun Chempo, who also founded Pemayangtse Monastery, it is located near Pelling. The monastery requires a scenic uphill hike through forest, culminating in a tranquil site with panoramic views of the Himalayan ranges including Mount Kanchenjunga.\n\nDespite multiple reconstructions due to fire, Sangachoeling preserves unique 17th-century clay statues and tantric relics. The monastery belongs to the Nyingma sect and has historically been restricted to male monks from Bhutia and Lepcha communities. It remains a vital pilgrimage site known for its spiritual atmosphere and connection to early Buddhist history in Sikkim.",
            price = "",

            _id = "6926a7d0bed139586fbb2aca",
            villageOrTown = "Pelling",
            district = "West Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.google.com/?cid=3956240192005177657&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQ&authuser=2",
            altitude = "2,100m (6,890 ft) approx.",
            buddhistSect = "Nyingma",
            foundedYear = 1697,

            history = "Founded in 1697 A.D. by Lama Lhatsun Chempo, Sangachoeling Monastery is one of Sikkim's oldest religious sites and a cornerstone of the early spread of Vajrayana Buddhism. It served as an exclusive center for Nyingma teachings, attracting hermits and practitioners from Tibet and Bhutan. Built according to sacred geomantic principles, the monastery suffered multiple fires but was rebuilt to preserve its spiritual legacy.\n\nIt houses rare 17th-century clay statues and tantric texts connected to Guru Padmasambhava’s lineage. Historically, the inner sanctum was reserved for Bhutia and Lepcha monks to maintain ritual purity. The monastery continues to be a key destination on the West Sikkim pilgrimage circuit.",

            architecture = "Sangachoeling Monastery showcases classic early Himalayan Buddhist architecture, emphasizing simplicity, sacred symbolism, and harmony with nature. Constructed from local stone, timber, and mud, the rectangular building has slightly sloping roofs supported by sturdy wooden beams suited for the monsoon climate.\n\nInside, ancient clay statues crafted with the \"zin\" technique depict tantric deities and manifestations. The prayer hall features wooden floors, painted beams, and murals reflecting Nyingma iconography, including wrathful deities and protectors. The entrance is adorned with colorful lintels and carved motifs symbolizing good fortune and protection.\n\nThe monastery's elevated ridge location offers panoramic views of the Kanchenjunga range and faces Pemayangtse Monastery across the valley, symbolizing spiritual dialogue between two important Buddhist centers.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083757/sanga7_zsd29v.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083755/sanga6_hzpwkf.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083753/sanga5_cg8kcm.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083751/sanga4_j1qf0k.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083749/sanga3_wpboq4.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083747/sanga2_g9wc9x.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083745/sanga1_jmos4b.webp"
            ),

            nearbyAttractions = listOf(
                "Pemayangtse Monastery",
                "Rabdentse Ruins",
                "Pelling Skywalk & Chenrezig Statue",
                "Sewaro Rock Garden",
                "Singshore Bridge",
                "Khecheopalri Lake",
                "Pelling Helipad Viewpoint",
                "Rimbi Waterfall"
            )
        ),Monastery(
            imageRes = 0,
            name = "Tashiding Monastery",
            distance = "",
            rating = 0f,
            reviews = 0,

            latitude = 27.347,
            longitude = 88.2585,

            location = "Tashiding Village",
            address = "Tashiding Village, West Sikkim",
            description = "Tashiding Monastery, known as 'The Devoted Central Glory,' is considered the most sacred monastery in Sikkim and is revered as the 'Heart of Sikkim.' Located atop a heart-shaped hill at the confluence of the Rathong and Rangit rivers, it is sanctified by Guru Padmasambhava who blessed the site in the 8th century. Founded in 1641 A.D. by Ngadak Sempa Chempo, one of the three lamas who consecrated Sikkim's first Chogyal, Tashiding is a key monastery of the Nyingma order.\n\nThe monastery complex includes numerous stupas and chortens, with the famous Thongwa Rangdrol chorten believed to cleanse sins by mere sight. The annual Bum Chu festival, involving the opening of a sacred vase of holy water blessed by Guru Padmasambhava, draws thousands of pilgrims and marks Tashiding as a vital religious site.",
            price = "",

            _id = "6926a7e0bed139586fbb2acb",
            villageOrTown = "Tashiding Village",
            district = "West Sikkim",
            state = "Sikkim",
            googleMapsLink = "https://maps.google.com/?cid=10526336605057038471&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQ&authuser=2",
            altitude = "1,465m (4,800 ft) approx.",
            buddhistSect = "Nyingma",
            foundedYear = 1641,

            history = "Tashiding Monastery holds the highest spiritual status in Sikkimese Buddhism, with origins dating to the 8th century when Guru Padmasambhava blessed the site. Formally founded in 1641 A.D. by Ngadak Sempa Chempo, a revered lama who consecrated Sikkim’s first Chogyal, it became central to the spiritual foundation of Sikkim.\n\nThe monastery’s hilltop location between the Rathong and Rangit rivers is of great geomantic significance. It houses the legendary Thongwa Rangdrol chorten and numerous ancient stupas and inscriptions. The monastery has weathered centuries of natural and political changes and remains a pilgrimage center, especially noted for the annual Bum Chu festival.",

            architecture = "Tashiding Monastery features classic Nyingma architectural simplicity and sacred geometry. Built on a naturally heart-shaped ridge, its stone and timber structures feature intricately carved wooden windows painted red and gold. The prayer hall contains ancient thangkas, statues of Guru Padmasambhava, and lineage masters, with murals of protectors and Buddhist narratives.\n\nNumerous stupas and chortens dot the hilltop, including the whitewashed Thongwa Rangdrol surrounded by mani walls inscribed with sacred mantras. The architecture blends seamlessly with the natural surroundings, creating a peaceful and holy atmosphere that has captivated pilgrims for centuries.",

            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083766/tas5_w1vrah.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083764/tas4_yr5g34.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083762/tas3_syy8gn.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083761/tas2_ap3652.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764083760/tas1_bdclvl.webp"
            ),

            nearbyAttractions = listOf(
                "Yuksom (First Capital of Sikkim)",
                "Pemayangtse Monastery",
                "Rabdentse Ruins",
                "Khecheopalri Lake",
                "Legship Hot Springs",
                "Pelling Skywalk & Chenrezig Statue"
            )
        )








    )






    fun getAllMonasteries(): List<Monastery> = monasteries
}
