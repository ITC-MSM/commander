package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Turntimber Grove
 * Land
 * This land enters tapped.
 * When this land enters, target creature gets +1/+1 until end of turn.
 * {T}: Add {G}.
 *
 * The Zendikar "enters tapped, then a one-shot rider on a creature" land cycle — the trigger
 * targets, so the land is a Falter/pump effect stapled to a land drop.
 */
val TurntimberGrove = card("Turntimber Grove") {
    manaCost = ""
    colorIdentity = "G"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, target creature gets +1/+1 until end of turn.\n" +
        "{T}: Add {G}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, creature)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "227"
        artist = "Rob Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc92d6ef-a578-4320-b5c8-3da192eea1f3.jpg"
    }
}
