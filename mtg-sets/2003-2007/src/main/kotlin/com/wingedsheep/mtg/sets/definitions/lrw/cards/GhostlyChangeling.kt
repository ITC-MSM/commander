package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghostly Changeling
 * {2}{B}
 * Creature — Shapeshifter
 * 2/2
 * Changeling (This card is every creature type.)
 * {1}{B}: This creature gets +1/+1 until end of turn.
 */
val GhostlyChangeling = card("Ghostly Changeling") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shapeshifter"
    power = 2
    toughness = 2
    oracleText = "Changeling (This card is every creature type.)\n{1}{B}: This creature gets +1/+1 " +
        "until end of turn."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "{1}{B}: This creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "116"
        artist = "Chuck Lukacs"
        flavorText = "In desolate places, changelings may take the shape of fancies, or memories, or fears."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7218726-dfa2-4d13-a210-5d54024e2d27.jpg?1783942889"
    }
}
