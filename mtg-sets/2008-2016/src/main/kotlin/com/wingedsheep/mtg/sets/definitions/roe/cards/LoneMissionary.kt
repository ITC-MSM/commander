package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lone Missionary
 * {1}{W}
 * Creature — Kor Monk
 * 2 / 1
 *
 * When this creature enters, you gain 4 life.
 *
 * Modeling notes:
 *  - "**When** this creature enters" is the one-shot [Triggers.EntersBattlefield]; there is no
 *    "whenever" and no other-permanent watcher to model.
 *  - "**You** gain 4 life" is [Effects.GainLife]'s default recipient (`EffectTarget.Controller`),
 *    so the target argument is left off — writing it explicitly would restate a default.
 */
val LoneMissionary = card("Lone Missionary") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Monk"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, you gain 4 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
        description = "When this creature enters, you gain 4 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Svetlin Velinov"
        flavorText = "His mission has become a grim pilgrimage, a tour of the Eldrazi-stricken outposts across Zendikar. But he marches on alone, stubborn as the daily dawn."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdc00f4b-ca9a-468a-91e1-c81fe6585765.jpg?1783942004"
    }
}
