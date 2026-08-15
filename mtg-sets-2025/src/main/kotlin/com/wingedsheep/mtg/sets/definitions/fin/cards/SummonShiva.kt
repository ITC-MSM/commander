package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Summon: Shiva
 * {3}{U}{U}
 * Enchantment Creature — Saga Elemental
 * 4/5
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I, II — Heavenly Strike — Tap target creature an opponent controls. Put a stun counter on it.
 * III — Diamond Dust — Draw a card for each tapped creature your opponents control.
 */
val SummonShiva = card("Summon: Shiva") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Saga Elemental"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I, II — Heavenly Strike — Tap target creature an opponent controls. Put a stun counter on it.\n" +
        "III — Diamond Dust — Draw a card for each tapped creature your opponents control."
    power = 4
    toughness = 5

    sagaChapter(1) {
        val t = target("creature", TargetObject(filter = TargetFilter.CreatureOpponentControls))
        effect = Effects.Composite(
            Effects.Tap(t),
            Effects.AddCounters(Counters.STUN, 1, t),
        )
    }
    sagaChapter(2) {
        val t = target("creature", TargetObject(filter = TargetFilter.CreatureOpponentControls))
        effect = Effects.Composite(
            Effects.Tap(t),
            Effects.AddCounters(Counters.STUN, 1, t),
        )
    }
    sagaChapter(3) {
        effect = Effects.DrawCards(
            DynamicAmounts.battlefield(
                Player.Each,
                GameObjectFilter.Creature.tapped().opponentControls(),
            ).count(),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "78"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a80511f8-7cb1-4974-afde-8a5cebe13ad7.jpg?1748706054"
    }
}
