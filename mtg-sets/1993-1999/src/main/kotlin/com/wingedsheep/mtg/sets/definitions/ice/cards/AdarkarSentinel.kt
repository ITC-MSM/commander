package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Adarkar Sentinel
 * {5}
 * Artifact Creature — Soldier
 * 3/3
 *
 * {1}: This creature gets +0/+1 until end of turn.
 *
 * The firebreathing shape with the pump on the toughness half: a mana-only activated ability whose
 * effect is `Effects.ModifyStats` onto `EffectTarget.Self`, taking the facade's default
 * `Duration.EndOfTurn`.
 */
val AdarkarSentinel = card("Adarkar Sentinel") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Soldier"
    power = 3
    toughness = 3
    oracleText = "{1}: This creature gets +0/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.ModifyStats(0, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "306"
        artist = "Melissa A. Benson"
        flavorText = "\"We encountered the Sentinels in the wastes, near no living thing. Their purpose was inscrutable.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff62754b-f4f0-4731-8dd7-327a820f60a8.jpg"
    }
}
