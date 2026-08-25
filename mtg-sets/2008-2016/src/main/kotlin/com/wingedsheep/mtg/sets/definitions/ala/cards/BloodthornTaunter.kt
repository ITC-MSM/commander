package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Bloodthorn Taunter
 * {1}{R}
 * Creature — Human Scout
 * 1 / 1
 * Haste
 * {T}: Target creature with power 5 or greater gains haste until end of turn.
 *
 * A bare [Costs.Tap] activated ability — the Taunter's own haste is what lets it be used the turn
 * it arrives. The power threshold is a restriction on the *target* rather than a condition on the
 * effect: `TargetFilter.Creature.powerAtLeast(5)`, so it is checked on announcement and rechecked
 * on resolution against projected state. The grant is [Effects.GrantKeyword] with its default
 * `Duration.EndOfTurn`.
 */
val BloodthornTaunter = card("Bloodthorn Taunter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Scout"
    power = 1
    toughness = 1
    oracleText = "Haste\n" +
        "{T}: Target creature with power 5 or greater gains haste until end of turn."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(5)))
        effect = Effects.GrantKeyword(Keyword.HASTE, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Jesper Ejsing"
        flavorText = "Naya's celebrants stoke the gargantuans into a rage, loosing a tide of muscle with the precision of an arrow."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/8769e8f6-15ad-4f1b-bb4d-848ae6b7549e.jpg"
    }
}
