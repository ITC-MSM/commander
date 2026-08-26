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
 * Graypelt Refuge
 * Land
 * This land enters tapped.
 * When this land enters, you gain 1 life.
 * {T}: Add {G} or {W}.
 *
 * The Zendikar "Refuge" gainland shape: an [EntersTapped] replacement effect for the printed
 * first line, an enters trigger carrying the life gain, and the mana line spelled as one
 * [Effects.AddMana] ability per colour sharing a [Costs.Tap] cost — the majority SDK form for a
 * plain "Add {X} or {Y}." with no rider on the ability.
 */
val GraypeltRefuge = card("Graypelt Refuge") {
    manaCost = ""
    colorIdentity = "WG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, you gain 1 life.\n" +
        "{T}: Add {G} or {W}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Philip Straub"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c46324e3-52e7-4b21-a9a0-a3aae9f7dd5b.jpg"
    }
}
