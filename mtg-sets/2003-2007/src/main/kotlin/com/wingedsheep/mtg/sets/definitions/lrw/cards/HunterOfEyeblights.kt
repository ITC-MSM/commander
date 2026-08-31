package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Hunter of Eyeblights
 * {3}{B}{B}
 * Creature — Elf Assassin
 * 3/3
 * When this creature enters, put a +1/+1 counter on target creature you don't control.
 * {2}{B}, {T}: Destroy target creature with a counter on it.
 *
 * The ETB gift is how the Hunter marks its own prey — but per the 2007-10-01 ruling the activated
 * ability targets a creature with *any* kind of counter, not just the +1/+1 it just handed out, so
 * the removal filter is `withAnyCounter()` rather than a `+1/+1`-specific one.
 */
val HunterOfEyeblights = card("Hunter of Eyeblights") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Assassin"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, put a +1/+1 counter on target creature you don't control.\n" +
        "{2}{B}, {T}: Destroy target creature with a counter on it."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val marked = target(
            "creature you don't control",
            TargetCreature(filter = TargetFilter.CreatureOpponentControls),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, marked)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Tap)
        val prey = target(
            "creature with a counter on it",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withAnyCounter())),
        )
        effect = Effects.Destroy(prey)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Jesper Ejsing"
        flavorText = "Snokk ran as fast as he could, but the sound of hooves grew ever louder in his ears."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbd5b8db-4a56-45bd-9704-a1316016ec5b.jpg?1783942889"
        ruling("2007-10-01", "The activated ability can target a creature with any kind of counter on it, not just a +1/+1 counter.")
    }
}
