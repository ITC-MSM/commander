package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Daemogoth Titan — Strixhaven: School of Mages #174 (canonical printing)
 * {B/G}{B/G}{B/G}{B/G} · Creature — Demon · 11/10
 *
 * Whenever this creature attacks or blocks, sacrifice a creature.
 *
 * "Attacks or blocks" is two triggered abilities sharing one effect — [Triggers.Attacks] and
 * [Triggers.Blocks] are separate events, the Elder Gargaroth shape. The bare imperative
 * "sacrifice a creature" is [Effects.SacrificeOwn]: the Titan's controller chooses, and the Titan
 * itself is a legal choice.
 */
val DaemogothTitan = card("Daemogoth Titan") {
    manaCost = "{B/G}{B/G}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Creature — Demon"
    oracleText =
        "Whenever this creature attacks or blocks, sacrifice a creature."
    power = 11
    toughness = 10

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
    }

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "174"
        artist = "Chris Cold"
        flavorText = "\"Of course it offered you power. Demons always do. But trust me—the sweeter the prize, the more ruinous the price.\"\n—Professor Onyx"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2f18828-ade7-4b99-97b2-e34bc2fdb68c.jpg?1783927319"
    }
}
