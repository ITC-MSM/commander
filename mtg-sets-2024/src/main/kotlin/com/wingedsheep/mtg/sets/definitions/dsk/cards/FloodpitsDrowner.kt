package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Floodpits Drowner
 * {1}{U}
 * Creature — Merfolk
 * 2/1
 *
 * Flash
 * Vigilance
 * When this creature enters, tap target creature an opponent controls and put a stun counter on it.
 * {1}{U}, {T}: Shuffle this creature and target creature with a stun counter on it into their
 * owners' libraries.
 *
 * The ETB is the shared "tap + stun" shape (a stun counter replaces the next untap — the Grappling
 * Kraken / Kitnap idiom). The activated ability shuffles two permanents into their owners'
 * libraries: the source itself ([EffectTarget.Self]) plus a target creature carrying a stun
 * counter, modelled as two sequential [Effects.ShuffleIntoLibrary] moves (each goes to its own
 * owner's library).
 */
val FloodpitsDrowner = card("Floodpits Drowner") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk"
    power = 2
    toughness = 1
    oracleText = "Flash\nVigilance\nWhen this creature enters, tap target creature an opponent " +
        "controls and put a stun counter on it.\n{1}{U}, {T}: Shuffle this creature and target " +
        "creature with a stun counter on it into their owners' libraries."

    keywords(Keyword.FLASH, Keyword.VIGILANCE)

    // When this creature enters, tap target creature an opponent controls and put a stun counter on it.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.opponentControls()))
        effect = Effects.Composite(
            Effects.Tap(t),
            AddCountersEffect(counterType = Counters.STUN, count = 1, target = t)
        )
    }

    // {1}{U}, {T}: Shuffle this creature and target creature with a stun counter on it into their
    // owners' libraries.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withCounter(Counters.STUN)))
        )
        effect = Effects.ShuffleIntoLibrary(EffectTarget.Self)
            .then(Effects.ShuffleIntoLibrary(t))
        description = "{1}{U}, {T}: Shuffle this creature and target creature with a stun counter " +
            "on it into their owners' libraries."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "John Tedrick"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6a62aa3-8edb-4000-8ebd-15ec4b00eed7.jpg?1726286073"
    }
}
