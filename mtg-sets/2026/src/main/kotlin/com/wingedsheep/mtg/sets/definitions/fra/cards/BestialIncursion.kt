package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

val BestialIncursion = card("Bestial Incursion") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create a 4/4 green Beast creature token with trample.\nFlashback {5}{G} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Effects.CreateToken(power = 4, toughness = 4, colors = setOf(Color.GREEN), creatureTypes = setOf("Beast"), keywords = setOf(Keyword.TRAMPLE), imageUri = "https://cards.scryfall.io/normal/front/8/5/859bda9a-fa90-4ad3-b0c1-6fc62e27c12f.jpg?1789736256")
    }
    keywordAbility(KeywordAbility.flashback("{5}{G}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Justin Gerard"
        flavorText = "\"Petition the dean for additional seeds. Gaz and Dak got into the medical supplies. Again.\"\n—Divik, Vigorbloom cadet"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0de5f66-f0df-4866-9f73-104ce50411b4.jpg?1789127248"
        inBooster = false
    }
}
