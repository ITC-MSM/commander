package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val ArtifistAcumen = card("Artifist Acumen") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Creatures you control gain first strike until end of turn.\nDraw a card."

    spell {
        effect = Patterns.Group.grantKeywordToAll(Keyword.FIRST_STRIKE, Filters.Group.creaturesYouControl)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Anna Podedworna"
        flavorText = "\"It's important to emphasize core principles, but to win duels you must extrapolate your designs to work in a variety of formats.\"\n—*Advanced Tethering*"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d3b720d-f27c-462a-8f80-15748e5086e1.jpg?1789729762"
        inBooster = false
    }
}
