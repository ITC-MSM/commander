package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bloodthirsty Conqueror
 * {3}{B}{B}
 * Creature — Vampire Knight
 * 5/5
 *
 * Flying, deathtouch
 * Whenever an opponent loses life, you gain that much life. (Damage causes loss of life.)
 *
 * The trigger fires once per opponent life-loss event ([Triggers.AnOpponentLosesLife]); the
 * amount lost is read from that event via [ContextPropertyKey.TRIGGER_LIFE_LOST] so you gain
 * exactly that much.
 */
val BloodthirstyConqueror = card("Bloodthirsty Conqueror") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    power = 5
    toughness = 5
    oracleText = "Flying, deathtouch\n" +
        "Whenever an opponent loses life, you gain that much life. (Damage causes loss of life.)"

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.AnOpponentLosesLife
        effect = Effects.GainLife(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_LIFE_LOST))
        description = "Whenever an opponent loses life, you gain that much life."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "58"
        artist = "Dmitry Burmak"
        flavorText = "\"This town swims with such exquisite blood. I will take every drop for my own.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce860ed4-a5bd-4347-9eab-dd716ea84db1.jpg?1782689215"
    }
}
