package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Siege Zombie
 * {1}{B}
 * Creature — Zombie — Common (MID #121)
 * 2/2
 *
 * "Tap three untapped creatures you control: Each opponent loses 1 life."
 *
 * Implementation notes:
 *  - The cost is [Costs.TapPermanents] (3 creatures), **not** `{T}` plus two others: the printed
 *    cost has no tap symbol, so Siege Zombie may be one of the three tapped creatures and none of
 *    them need haste (CR 118.3c / 302.6 — the summoning-sickness restriction applies only to the
 *    `{T}` symbol in an activation cost).
 *  - "Each opponent loses 1 life" is a life-loss effect (not damage), scoped to
 *    [Player.EachOpponent] so it hits every opponent in multiplayer.
 */
val SiegeZombie = card("Siege Zombie") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 2
    oracleText = "Tap three untapped creatures you control: Each opponent loses 1 life."

    activatedAbility {
        cost = Costs.TapPermanents(3, Filters.Creature)
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "121"
        artist = "Johann Bodin"
        flavorText = "A barricade only buys time. So the wealthy buy more barricades."
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d94b5270-840b-4905-815a-057029d7352f.jpg?1783925609"
    }
}
