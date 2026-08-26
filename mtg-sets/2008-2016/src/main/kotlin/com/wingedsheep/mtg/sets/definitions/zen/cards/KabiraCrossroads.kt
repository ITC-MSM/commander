package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Kabira Crossroads
 * Land
 * This land enters tapped.
 * When this land enters, you gain 2 life.
 * {T}: Add {W}.
 *
 * The Zendikar "Refuge" gainland shape: an [EntersTapped] replacement effect for the printed
 * first line, an enters trigger carrying the life gain, and the mana line spelled as one
 * [Effects.AddMana] ability per colour sharing a [Costs.Tap] cost — the majority SDK form for a
 * plain "Add {X} or {Y}." with no rider on the ability.
 */
val KabiraCrossroads = card("Kabira Crossroads") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, you gain 2 life.\n" +
        "{T}: Add {W}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a776decb-cdb5-4f41-8a3a-c0cece18eb35.jpg"
    }
}
