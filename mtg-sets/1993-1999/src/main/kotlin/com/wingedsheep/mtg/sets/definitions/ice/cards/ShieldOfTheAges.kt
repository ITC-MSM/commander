package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shield of the Ages
 * {2}
 * Artifact
 *
 * {2}: Prevent the next 1 damage that would be dealt to you this turn.
 *
 * [Effects.PreventNextDamage] onto the ability's controller is the whole card — the unified
 * prevention shield already defaults to the end-of-turn duration and to any damage source.
 */
val ShieldOfTheAges = card("Shield of the Ages") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}: Prevent the next 1 damage that would be dealt to you this turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.PreventNextDamage(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "335"
        artist = "Anson Maddocks"
        flavorText = "\"This shield is a true rarity: an artifact whose purpose is obvious.\"\n—Arcum Dagsson, Soldevi Machinist"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/7411ab40-47f6-44d1-8e33-9ff5301dcd9b.jpg"
    }
}
