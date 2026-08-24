package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Covenant of Blood
 * {6}{B}
 * Sorcery
 * Convoke
 * Covenant of Blood deals 4 damage to any target and you gain 4 life.
 */
val CovenantOfBlood = card("Covenant of Blood") {
    manaCost = "{6}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Covenant of Blood deals 4 damage to any target and you gain 4 life."

    keywords(Keyword.CONVOKE)

    spell {
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, victim)
            .then(Effects.GainLife(4))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Seb McKinnon"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/752b7d05-19d7-4765-9bf8-05a7cb539c3f.jpg?1783939185"
    }
}
