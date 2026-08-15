package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Long List of the Ents
 * {G}
 * Enchantment — Saga (6 chapters)
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after VI.)
 * I, II, III, IV, V, VI — Note a creature type that hasn't been noted for this Saga.
 * When you next cast a creature spell of that type this turn, that creature enters with
 * an additional +1/+1 counter on it.
 *
 * Each chapter does two things in sequence:
 *  1. `NoteCreatureType("notedType")` — prompts the controller for a creature type not
 *     already noted on this Saga, persisting the pick to its `NotedCreatureTypesComponent`
 *     and stashing it under `chosenValues["notedType"]` for the next pipeline step. The
 *     dedup is the heart of the card: each chapter has to pick a *different* type.
 *  2. `CreateDelayedTriggerEffect` — installs a one-shot, end-of-turn delayed triggered
 *     ability whose `SpellCastEvent` filter is `Creature.withSubtypeFromVariable("notedType")`.
 *     `CreateDelayedTriggerExecutor.bakeChosenValuesIntoTrigger` substitutes the
 *     just-noted type into the filter at trigger-creation time, because the
 *     EffectContext that holds chosenValues is gone by the time the trigger fires. When
 *     a matching creature spell is cast that turn, the trigger fires and adds a +1/+1
 *     counter to the spell on the stack via `EffectTarget.TriggeringEntity`; the counter
 *     travels with the spell to the battlefield as it resolves, satisfying "enters with
 *     an additional +1/+1 counter on it" naturally.
 *
 * Up to six different noted types can be active simultaneously by the time chapter VI
 * resolves (one delayed trigger per chapter), each watching for a different creature type.
 * Each is one-shot — at most one matching cast per type per chapter consumes its trigger.
 */
val LongListOfTheEnts = card("Long List of the Ents") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after VI.)\n" +
        "I, II, III, IV, V, VI — Note a creature type that hasn't been noted for this Saga. " +
        "When you next cast a creature spell of that type this turn, that creature enters with " +
        "an additional +1/+1 counter on it."

    sagaChapter(1) { effect = noteAndBuff() }
    sagaChapter(2) { effect = noteAndBuff() }
    sagaChapter(3) { effect = noteAndBuff() }
    sagaChapter(4) { effect = noteAndBuff() }
    sagaChapter(5) { effect = noteAndBuff() }
    sagaChapter(6) { effect = noteAndBuff() }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Logan Feliciano"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27347256-2ac4-4b12-b288-0a8d578a1ff2.jpg?1715045040"
    }
}

/**
 * One chapter's effect — picks a new creature type, then installs a one-shot delayed
 * trigger that adds a +1/+1 counter to the next creature spell of that type cast this
 * turn. A fresh instance is built per chapter so each chapter spawns its own
 * `DelayedTriggeredAbility`.
 */
private fun noteAndBuff(): Effect = Effects.Composite(
    Effects.NoteCreatureType("notedType"),
    CreateDelayedTriggerEffect(
        trigger = TriggerSpec(
            event = EventPattern.SpellCastEvent(
                spellFilter = GameObjectFilter.Creature.withSubtypeFromVariable("notedType"),
                player = Player.You
            )
        ),
        fireOnce = true,
        expiry = DelayedTriggerExpiry.EndOfTurn,
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.TriggeringEntity)
    )
)
