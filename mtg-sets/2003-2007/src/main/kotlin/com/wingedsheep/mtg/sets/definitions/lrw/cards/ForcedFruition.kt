package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Forced Fruition
 * {4}{U}{U}
 * Enchantment
 * Whenever an opponent casts a spell, that player draws seven cards.
 *
 * The draw lands on the caster, not on Forced Fruition's controller, so the payoff is bound to
 * [Player.TriggeringPlayer]. The trigger fires on the cast itself — it resolves before the spell
 * does, and it still resolves if that spell is later countered.
 */
val ForcedFruition = card("Forced Fruition") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever an opponent casts a spell, that player draws seven cards."

    triggeredAbility {
        trigger = Triggers.OpponentCastsSpell
        effect = Effects.DrawCards(7, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        description = "Whenever an opponent casts a spell, that player draws seven cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "66"
        artist = "William O'Connor"
        flavorText = "\"Petals within petals within petals, tadpole. The truth lurks below an opulence of illusion.\"\n—Neerdiv, fallowsage"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7ea1c6e-c0af-40b0-b492-8d71f496903e.jpg?1783942903"
    }
}
