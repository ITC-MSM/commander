package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Netherborn Phalanx
 * {5}{B}
 * Creature — Horror
 * 2/4
 * When this creature enters, each opponent loses 1 life for each creature they control.
 * Transmute {1}{B}{B}
 *
 * "Each opponent loses 1 life for each creature *they* control" is a per-player amount, not one
 * total, so it is a [Effects.ForEachPlayer] sweep over [Player.EachOpponent] rather than a single
 * `LoseLife` with a global count. Each iteration rebinds the controller, which makes
 * [EffectTarget.Controller] the opponent being processed and `Player.You` inside the
 * [DynamicAmount.Count] *that same* opponent — so a player with three creatures loses 3 while a
 * player with none loses 0, instead of both losing the table-wide total.
 */
val NetherbornPhalanx = card("Netherborn Phalanx") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 2
    toughness = 4
    oracleText = "When this creature enters, each opponent loses 1 life for each creature they control.\n" +
        "Transmute {1}{B}{B} ({1}{B}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachPlayer(
            Player.EachOpponent,
            listOf(
                Effects.LoseLife(
                    DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature),
                    EffectTarget.Controller
                )
            )
        )
    }

    transmute("{1}{B}{B}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b665e905-cb06-48b7-9f50-5916277e237d.jpg?1783943666"
    }
}
