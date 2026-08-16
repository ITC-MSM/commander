package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Meteorite
 * {5}
 * Artifact
 * When this artifact enters, it deals 2 damage to any target.
 * {T}: Add one mana of any color.
 */
val Meteorite = card("Meteorite") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "When this artifact enters, it deals 2 damage to any target.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("any target", AnyTarget())
        effect = Effects.DealDamage(2, t)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Scott Murphy"
        flavorText = "\"And if I'm lying,\" he began . . ."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7a39c1a-7615-4ac8-8984-b8459d201cb2.jpg?1783939157"
    }
}
