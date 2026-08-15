package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration

import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frog Butler
 * {1}{G}
 * Creature — Frog Spirit
 * 1/1
 *
 * Deathtouch
 * {T}: Add one mana of any color.
 * {2}: This creature gains reach until end of turn.
 */
val FrogButler = card("Frog Butler") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Frog Spirit"
    oracleText = "Deathtouch\n{T}: Add one mana of any color.\n{2}: This creature gains reach until end of turn."
    power = 1
    toughness = 1

    keywords(Keyword.DEATHTOUCH)

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.GrantKeyword(Keyword.REACH, EffectTarget.Self, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Mirko Failoni"
        flavorText = "\"With faithful Alberto's assistance, this shall be a gathering of untold delight! Why would you ever wish to leave?\"\n—Toad Baron"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1a72d09-9cfc-463a-a9ec-3359003d54da.jpg?1771502693"
    }
}
