package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Nomad Outpost
 * Land
 * This land enters tapped.
 * {T}: Add {R}, {W}, or {B}.
 */
val NomadOutpost = card("Nomad Outpost") {
    typeLine = "Land"
    colorIdentity = "WBR"
    oracleText = "This land enters tapped.\n{T}: Add {R}, {W}, or {B}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Noah Bradley"
        flavorText = "Only the weak imprison themselves behind walls. We live free under the wind, and our freedom makes us strong."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb6ae4a5-227d-465b-9e99-bae158b7d410.jpg?1562796466"
    }
}
