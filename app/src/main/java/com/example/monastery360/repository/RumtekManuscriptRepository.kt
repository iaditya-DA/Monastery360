package com.example.monastery360.repository

import com.example.monastery360.model.DigitalArchive

/**
 * Repository for Rumtek Monastery Manuscripts Only
 * Hard coded data for Rumtek Monastery ID: 6926a7c2bed139586fbb2ac9
 */
object RumtekManuscriptRepository {

    private const val RUMTEK_MONASTERY_ID = "6926a7c2bed139586fbb2ac9"

    val rumtekManuscripts = listOf(
        DigitalArchive(
            _id = "69290579bed139586fbb4fc4",
            monasteryId = RUMTEK_MONASTERY_ID,
            title = "Rumtek Monastery: Historical Foundation and Dharma Chakra Centre Establishment (1966)",
            type = "Historical Archive / Lineage Seat Document",
            fileUrl = "https://res.cloudinary.com/djeospbqe/image/upload/v1764190502/rumtek_monastery_vbk9vo.pdf",
            description = "Documents related to the Original Founding (1734 AD) by the 9th Karmapa, and the Re-establishment in 1966 by the 16th Karmapa, Rangjung Rigpe Dorje, as the seat-in-exile and Dharma Chakra Centre. Includes documentation on its near-replica design of Tsurphu Monastery (Tibet).",
            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152159/rumteck2_vjonui.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152159/rumteck3_nfdje3.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152160/rumteck4_ocksqk.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152160/rumteck5_dkxoos.webp"
            )
        ),
        DigitalArchive(
            _id = "69290586bed139586fbb4fc5",
            monasteryId = RUMTEK_MONASTERY_ID,
            title = "Rumtek Monastery: Relics and Golden Stupa of the 16th Karmapa",
            type = "Religious Relics / Sacred Objects Inventory",
            fileUrl = "https://res.cloudinary.com/djeospbqe/image/upload/v1764190502/rumtek_monastery_vbk9vo.pdf",
            description = "Detailed inventory and visual documentation of the Golden Stupa containing the relics of the 16th Karmapa. Catalogue of precious objects and rare religious art (including statues and silk paintings) brought from Tsurphu Monastery in Tibet, making it an international repository.",
            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152161/rumteck6_qkfois.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152162/rumteck7_entkr6.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152163/Rumtekmanu1_ciwyhe.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152163/rumtekManu_zzr2fa.webp"
            )
        ),
        DigitalArchive(
            _id = "6929059abed139586fbb4fc6",
            monasteryId = RUMTEK_MONASTERY_ID,
            title = "Rumtek Monastery: Karma Shri Nalanda Institute for Higher Buddhist Studies Curriculum",
            type = "Educational Programme / Curriculum Guide",
            fileUrl = "https://res.cloudinary.com/djeospbqe/image/upload/v1764190494/rumtek_monastery_3_zkrq42.pdf",
            description = "Detailed curriculum of the 9-year advanced course offered by the Karma Shri Nalanda Institute, covering Buddhist philosophy, logic, meditation, and debate within the Karma Kagyu lineage. Includes information on monastic scholarship and its role in disseminating teachings.",
            images = listOf(
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152163/rumtek1_wrnddy.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152164/rumtekmanu2_memszw.webp",
                "https://res.cloudinary.com/djeospbqe/image/upload/v1764152164/rumtekmanu3_wndqie.webp"
            )
        )
    )

    /**
     * Get all Rumtek manuscripts
     */
    fun getAllRumtekManuscripts(): List<DigitalArchive> {
        return rumtekManuscripts
    }

    /**
     * Get manuscript by ID
     */
    fun getManuscriptById(id: String): DigitalArchive? {
        return rumtekManuscripts.find { it._id == id }
    }

    /**
     * Get total count of manuscripts
     */
    fun getManuscriptCount(): Int {
        return rumtekManuscripts.size
    }
}