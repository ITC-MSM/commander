package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Hapatra, Vizier of Poisons
 * {B}{G}
 * Legendary Creature — Human Cleric
 * 2/2
 * Whenever Hapatra deals combat damage to a player, you may put a -1/-1 counter on target creature.
 * Whenever you put one or more -1/-1 counters on a creature, create a 1/1 green Snake creature token with deathtouch.
 *
 * The second trigger is the per-recipient shape of [Triggers.countersPlacedOn] — the current Oracle
 * text says "on **a** creature", so an effect that hits three creatures fires it three times, and
 * `batch` stays at its default `false`. `firstTimeEachTurn` is passed explicitly because the facade
 * defaults it to `!batch`, which is the Stalwart Successor rider this card does not print. The
 * recipient filter carries no `youControl()` — any creature qualifies; it is `placedBy` that scopes
 * the trigger to counters *you* put (CR 122.6a).
 */
val HapatraVizierOfPoisons = card("Hapatra, Vizier of Poisons") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Human Cleric"
    oracleText = "Whenever Hapatra deals combat damage to a player, you may put a -1/-1 counter on target creature.\n" +
            "Whenever you put one or more -1/-1 counters on a creature, create a 1/1 green Snake creature token with deathtouch."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        optional = true
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t)
    }

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature,
            counterType = Counters.MINUS_ONE_MINUS_ONE,
            firstTimeEachTurn = false,
            placedBy = Player.You,
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Snake"),
            keywords = setOf(Keyword.DEATHTOUCH),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "199"
        artist = "Tyler Jacobson"
        flavorText = "Her subtle smile is suffused with venom."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56fbbcc9-db23-4902-b0f7-cea78a2a36af.jpg?1783936464"
    }
}
