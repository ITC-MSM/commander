package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.OneOrMoreDealCombatDamageToPlayerEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CastFromCollectionWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Vaan, Street Thief
 * {2}{R}
 * Legendary Creature — Human Scout
 * 2/2
 *
 * Whenever one or more Scouts, Pirates, and/or Rogues you control deal combat damage to a player,
 * exile the top card of that player's library. You may cast it. If you don't, create a Treasure token.
 * Whenever you cast a spell you don't own, put a +1/+1 counter on each Scout, Pirate, and Rogue you control.
 *
 * First ability is a per-damaged-player combat batch ([OneOrMoreDealCombatDamageToPlayerEvent]): it
 * fires once for each player dealt combat damage by a matching creature, and `Player.TriggeringPlayer`
 * resolves to that damaged player so we exile the top card of *their* library. The cast is offered
 * during this ability's resolution ([MayEffect] + [CastFromCollectionWithoutPayingCostEffect] with
 * `payManaCost = true`) — per the official ruling you must cast it while the ability is on the stack;
 * you can't wait. Declining runs the `otherwise` branch and makes a Treasure. Because the exiled card
 * is owned by the damaged player, casting it is "a spell you don't own" and triggers the second ability.
 *
 * Second ability reads the spell's owner vs. its controller via [SpellCastPredicate.NotOwnedByController]
 * and distributes a +1/+1 counter to each Scout/Pirate/Rogue you control (including Vaan itself).
 */
val VaanStreetThief = card("Vaan, Street Thief") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Scout"
    power = 2
    toughness = 2
    oracleText = "Whenever one or more Scouts, Pirates, and/or Rogues you control deal combat damage to a player, " +
        "exile the top card of that player's library. You may cast it. If you don't, create a Treasure token.\n" +
        "Whenever you cast a spell you don't own, put a +1/+1 counter on each Scout, Pirate, and Rogue you control."

    // Whenever one or more Scouts, Pirates, and/or Rogues you control deal combat damage to a player,
    // exile the top card of that player's library. You may cast it. If you don't, create a Treasure token.
    triggeredAbility {
        trigger = TriggerSpec(
            OneOrMoreDealCombatDamageToPlayerEvent(
                sourceFilter = GameObjectFilter.Creature.withAnySubtype("Scout", "Pirate", "Rogue")
            ),
            TriggerBinding.ANY
        )
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1), player = Player.TriggeringPlayer),
                storeAs = "vaanLooked"
            ),
            MoveCollectionEffect(
                from = "vaanLooked",
                destination = CardDestination.ToZone(Zone.EXILE, Player.TriggeringPlayer),
                storeMovedAs = "vaanExiled"
            ),
            MayEffect(
                effect = CastFromCollectionWithoutPayingCostEffect(from = "vaanExiled", payManaCost = true),
                descriptionOverride = "Cast the exiled card",
                otherwise = Effects.CreateTreasure(1)
            )
        )
    }

    // Whenever you cast a spell you don't own, put a +1/+1 counter on each Scout, Pirate, and Rogue you control.
    triggeredAbility {
        trigger = Triggers.youCastSpell(requires = setOf(SpellCastPredicate.NotOwnedByController))
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(
                GameObjectFilter.Creature.withAnySubtype("Scout", "Pirate", "Rogue").youControl()
            ),
            effect = AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "168"
        artist = "Jake Murray"
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50e1ec29-9de3-4f1b-b818-057e030d475b.jpg?1748706390"
        ruling("2025-06-06", "You cast the exiled card while Vaan's first ability is still on the stack. You can't wait to cast it later in the turn. Timing restrictions based on the card's type are ignored.")
        ruling("2025-06-06", "Vaan's last ability resolves before the spell that caused it to trigger. It resolves even if that spell is countered or otherwise leaves the stack.")
    }
}
