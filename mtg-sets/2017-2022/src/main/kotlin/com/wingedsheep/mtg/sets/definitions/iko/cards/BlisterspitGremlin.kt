package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blisterspit Gremlin — Ikoria: Lair of Behemoths #108
 * {R} · Creature — Gremlin · 1/1
 *
 * {1}, {T}: This creature deals 1 damage to each opponent.
 * Whenever you cast a noncreature spell, untap this creature.
 *
 * The two halves are a loop: the untap trigger refunds the tap cost, so every noncreature spell
 * cast after an activation lets the Gremlin ping again. The untap is a plain
 * [Effects.Untap] on [EffectTarget.Self] — it doesn't care whether the Gremlin is actually tapped,
 * and the trigger still goes on the stack either way (CR 603.2).
 */
val BlisterspitGremlin = card("Blisterspit Gremlin") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Gremlin"
    power = 1
    toughness = 1
    oracleText = "{1}, {T}: This creature deals 1 damage to each opponent.\n" +
        "Whenever you cast a noncreature spell, untap this creature."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.Untap(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap
        )
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Simon Dominic"
        flavorText = "\"Ah, we have a critic.\"\n—Orthion, Lavabrink captain"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ec65b97-d5c0-4609-8a60-3c4daa3e59c1.jpg"
    }
}
