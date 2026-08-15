package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Summon: Fenrir
 * {2}{G}
 * Enchantment Creature — Saga Wolf
 * 3/2
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Crescent Fang — Search your library for a basic land card, put it onto the battlefield
 *     tapped, then shuffle.
 * II — Heavenward Howl — When you next cast a creature spell this turn, that creature enters
 *     with an additional +1/+1 counter on it.
 * III — Ecliptic Growl — Draw a card if you control the creature with the greatest power or
 *     tied for the greatest power.
 *
 * Chapter II installs a one-shot, end-of-turn delayed triggered ability whose
 * `SpellCastEvent` filter matches any creature spell you cast. When the next such spell is
 * cast that turn, the trigger adds a +1/+1 counter to the spell on the stack via
 * `EffectTarget.TriggeringEntity`; the counter travels with the spell to the battlefield as
 * it resolves, satisfying "enters with an additional +1/+1 counter on it" naturally. Same
 * primitive as Long List of the Ents, minus the noted-type filter (here it's any creature).
 *
 * Chapter III's "you control the creature with the greatest power or tied for the greatest
 * power" ≡ you control a creature AND your max creature-power >= the global max
 * creature-power. The `ControlCreature` conjunct excludes the 0-vs-0 case when no creatures
 * exist (same condition shape as Thickest in the Thicket).
 */
val SummonFenrir = card("Summon: Fenrir") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Saga Wolf"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Crescent Fang — Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.\n" +
        "II — Heavenward Howl — When you next cast a creature spell this turn, that creature enters with an additional +1/+1 counter on it.\n" +
        "III — Ecliptic Growl — Draw a card if you control the creature with the greatest power or tied for the greatest power."
    power = 3
    toughness = 2

    sagaChapter(1) {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
        )
    }

    sagaChapter(2) {
        effect = CreateDelayedTriggerEffect(
            trigger = TriggerSpec(
                event = EventPattern.SpellCastEvent(
                    spellFilter = GameObjectFilter.Creature,
                    player = Player.You,
                ),
            ),
            fireOnce = true,
            expiry = DelayedTriggerExpiry.EndOfTurn,
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.TriggeringEntity),
        )
    }

    sagaChapter(3) {
        effect = ConditionalEffect(
            condition = Conditions.All(
                Conditions.ControlCreature,
                Compare(
                    DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).maxPower(),
                    ComparisonOperator.GTE,
                    DynamicAmounts.battlefield(Player.Each, GameObjectFilter.Creature).maxPower(),
                ),
            ),
            effect = Effects.DrawCards(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "Chun Lo"
        flavorText = "\"Who dares summon me from the darkness?\""
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93feb9d5-d004-4598-a448-b3488c869c05.jpg?1748706522"
    }
}
