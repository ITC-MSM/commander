package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect

/**
 * Channeled Dragonfire — Tarkir: Dragonstorm #102
 * {R} · Sorcery
 *
 * Channeled Dragonfire deals 2 damage to any target.
 * Harmonize {5}{R}{R} (You may cast this card from your graveyard for its harmonize
 * cost. You may tap a creature you control to reduce that cost by {X}, where X is its
 * power. Then exile this spell.)
 */
val ChanneledDragonfire = card("Channeled Dragonfire") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Channeled Dragonfire deals 2 damage to any target.\n" +
        "Harmonize {5}{R}{R} (You may cast this card from your graveyard for its harmonize cost. " +
        "You may tap a creature you control to reduce that cost by {X}, where X is its power. Then exile this spell.)"

    spell {
        val t = target("target", Targets.Any)
        effect = DealDamageEffect(2, t)
    }

    keywordAbility(KeywordAbility.harmonize("{5}{R}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Jorge Jacinto"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24204881-690c-4043-8771-20cb93385072.jpg?1743258630"
    }
}
