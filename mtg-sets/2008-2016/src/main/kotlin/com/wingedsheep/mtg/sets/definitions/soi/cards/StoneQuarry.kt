package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Stone Quarry
 * Land
 *
 * This land enters tapped.
 * {T}: Add {R} or {W}.
 *
 * The common "gain land" shape: an unconditional [EntersTapped] replacement plus one mana ability
 * per producible color — "add {R} **or** {W}" is a choice between two abilities, not one ability
 * producing a choice, so it is two [activatedAbility] blocks.
 */
val StoneQuarry = card("Stone Quarry") {
    manaCost = ""
    colorIdentity = "RW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {R} or {W}."

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

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "279"
        artist = "Cliff Childs"
        flavorText = "In Gavony, headstones come only from quarries that have been blessed by chaplains."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e636cdc3-4b83-4f15-ad4a-6c8fa3533408.jpg?1783937697"
    }
}
