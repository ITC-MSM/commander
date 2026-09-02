package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Slum Reaper
 * {3}{B}
 * Creature — Horror
 * 4/2
 *
 * When this creature enters, each player sacrifices a creature of their choice.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * "Each player sacrifices" names a player, so it is [Effects.Sacrifice]'s three-argument form —
 * a `ForceSacrificeEffect` — and not the bare imperative that makes the *controller* sacrifice.
 * The Reaper's own controller is included, and may well have to feed it the Reaper.
 */
val SlumReaper = card("Slum Reaper") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    oracleText = "When this creature enters, each player sacrifices a creature of their choice."
    power = 4
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Sacrifice(GameObjectFilter.Creature, 1, EffectTarget.PlayerRef(Player.Each))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "77"
        artist = "Karl Kopinski"
        flavorText = "It's sent into unguilded districts by the Orzhov to collect the souls of those no one will miss."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f0fea13-63cf-4574-8752-3c357eee4524.jpg?1783940361"
    }
}
