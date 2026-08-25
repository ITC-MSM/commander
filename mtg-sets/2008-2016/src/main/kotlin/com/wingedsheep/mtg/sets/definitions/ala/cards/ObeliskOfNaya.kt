package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Obelisk of Naya
 * {3}
 * Artifact
 * {T}: Add {R}, {G}, or {W}.
 *
 * One of the Alara shard obelisks, and a direct sibling of `ObeliskOfEsper`. The printed "or" is a
 * choice between three mana abilities, so it is authored as three separate [Effects.AddMana]
 * abilities on [Costs.Tap], each flagged `manaAbility` with [TimingRule.ManaAbility].
 */
val ObeliskOfNaya = card("Obelisk of Naya") {
    manaCost = "{3}"
    colorIdentity = "GRW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {R}, {G}, or {W}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
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
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Steve Prescott"
        flavorText = "Centuries have passed since the plane shattered, yet the obelisks of each shard faithfully serve their long-forgotten purpose."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df6317b0-15fd-4924-9302-41bed2354546.jpg"
    }
}
