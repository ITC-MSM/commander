package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Obelisk of Bant
 * {3}
 * Artifact
 * {T}: Add {G}, {W}, or {U}.
 *
 * One of the Alara shard obelisks, and a direct sibling of `ObeliskOfEsper`. The printed "or" is a
 * choice between three mana abilities, so it is authored as three separate [Effects.AddMana]
 * abilities on [Costs.Tap], each flagged `manaAbility` with [TimingRule.ManaAbility].
 */
val ObeliskOfBant = card("Obelisk of Bant") {
    manaCost = "{3}"
    colorIdentity = "GUW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {G}, {W}, or {U}."

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
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "212"
        artist = "David Palumbo"
        flavorText = "Whether incorporated into the structure of castles or admired as lone symbols, the obelisks stand for Bant's values of loyalty, honor, and truth."
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0cefe6ab-c018-4b87-8948-295a28f63cb1.jpg"
    }
}
