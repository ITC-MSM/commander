package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skeletal Changeling
 * {1}{B}
 * Creature — Shapeshifter
 * 1/1
 * Changeling (This card is every creature type.)
 * {1}{B}: Regenerate this creature.
 */
val SkeletalChangeling = card("Skeletal Changeling") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)\n{1}{B}: Regenerate this creature."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{1}{B}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Alan Pollack"
        flavorText = "Though they lack true flesh and bone of their own, changelings imitate either with equal ease."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f1d8e26-304b-4571-9b33-7713265d9bbf.jpg?1783942884"
    }
}
