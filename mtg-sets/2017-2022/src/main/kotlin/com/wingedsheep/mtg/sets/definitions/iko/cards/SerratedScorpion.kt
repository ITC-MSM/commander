package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Serrated Scorpion
 * {B}
 * Creature — Scorpion
 * 1/2
 *
 * When this creature dies, it deals 2 damage to each opponent and you gain 2 life.
 *
 * A single dies trigger carrying both halves as one composite effect — the damage is dealt to
 * every opponent at once (so it scales in multiplayer) while the life gain is a flat 2 for the
 * controller regardless of how many opponents were hit.
 */
val SerratedScorpion = card("Serrated Scorpion") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Scorpion"
    power = 1
    toughness = 2
    oracleText = "When this creature dies, it deals 2 damage to each opponent and you gain 2 life."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Chris Seaman"
        flavorText = "Each scorpion's venom is unique, thwarting sanctuary healers' attempts to develop an antidote."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc8f0242-35e1-4409-9321-56e742e8fef4.jpg"
    }
}
