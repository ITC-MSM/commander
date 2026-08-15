package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Blood Petal Celebrant
 * {1}{R}
 * Creature — Vampire
 * 2/1
 * This creature has first strike as long as it's attacking.
 * When this creature dies, create a Blood token.
 */
val BloodPetalCelebrant = card("Blood Petal Celebrant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 1
    oracleText = "This creature has first strike as long as it's attacking.\n" +
        "When this creature dies, create a Blood token. (It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")"

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter.source())
        condition = Conditions.SourceIsAttacking
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateBlood()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Fajareka Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c3a4927-f06c-424d-92a9-b40cf8e3e209.jpg?1782703086"
    }
}
