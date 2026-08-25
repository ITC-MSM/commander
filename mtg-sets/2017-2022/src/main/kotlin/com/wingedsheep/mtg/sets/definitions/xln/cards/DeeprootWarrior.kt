package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deeproot Warrior
 * {1}{G}
 * Creature — Merfolk Warrior
 * 2/2
 *
 * Whenever this creature becomes blocked, it gets +1/+1 until end of turn.
 */
val DeeprootWarrior = card("Deeproot Warrior") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Warrior"
    oracleText = "Whenever this creature becomes blocked, it gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Slawomir Maniak"
        flavorText = "\"We breathe from our soul and bones to give strength to the jungle. The jungle breathes from its roots and rivers to give strength to us.\"\n—Shaper Falani"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c1a1963-ec46-4ed3-9be1-e4cc09687922.jpg"
    }
}
