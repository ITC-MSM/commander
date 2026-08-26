package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Soaring Seacliff
 * Land
 * This land enters tapped.
 * When this land enters, target creature gains flying until end of turn.
 * {T}: Add {U}.
 *
 * The Zendikar "enters tapped, then a one-shot rider on a creature" land cycle — the trigger
 * targets, so the land is a Falter/pump effect stapled to a land drop.
 */
val SoaringSeacliff = card("Soaring Seacliff") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, target creature gains flying until end of turn.\n" +
        "{T}: Add {U}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "225"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bf0de91-765a-49e3-bd8a-94f266b3bdd8.jpg"
    }
}
