package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fuming Effigy — Strixhaven: School of Mages #103 (canonical printing)
 * {3}{R} · Creature — Spirit · 4/3
 *
 * Whenever one or more cards leave your graveyard, this creature deals 1 damage to each opponent.
 *
 * "One or more … leave" is CR 603.2c batch wording, so the trigger is the batching
 * [Triggers.CardsLeaveYourGraveyard] with its default any-card filter: a mass reanimation or a
 * graveyard-exiling sweep fires it exactly once. The damage is a single [Effects.DealDamage]
 * aimed at [Player.EachOpponent].
 */
val FumingEffigy = card("Fuming Effigy") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    oracleText =
        "Whenever one or more cards leave your graveyard, this creature deals 1 damage to each opponent."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard()
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Ryan Alexander Lee"
        flavorText = "Some spirits share ancient wisdom. Others just get mad you touched their stuff."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7ce76a38-fc5f-4e29-ba24-4a877179a62c.jpg?1783927355"
    }
}
