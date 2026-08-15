package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Clergy en-Vec
 * {1}{W}
 * Creature — Human Cleric
 * 1/1
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 */
val ClergyEnVec = card("Clergy en-Vec") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to any target this turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", AnyTarget())
        effect = Effects.PreventNextDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Heather Hudson"
        flavorText = "\"Faith's shield is hammered out by the blows of unbelievers.\"\n—Oracle en-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fcb0e068-16d0-4e1c-acad-0a6d34148c5a.jpg"
    }
}
