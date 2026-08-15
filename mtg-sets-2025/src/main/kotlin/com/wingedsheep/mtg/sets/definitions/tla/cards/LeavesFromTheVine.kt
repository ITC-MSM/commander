package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Leaves from the Vine
 * {1}{G}
 * Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Mill three cards, then create a Food token. (It's an artifact with "{2}, {T}, Sacrifice this
 *     token: You gain 3 life.")
 * II — Put a +1/+1 counter on each of up to two target creatures you control.
 * III — Draw a card if there's a creature or Lesson card in your graveyard.
 */
val LeavesFromTheVine = card("Leaves from the Vine") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Mill three cards, then create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")\n" +
        "II — Put a +1/+1 counter on each of up to two target creatures you control.\n" +
        "III — Draw a card if there's a creature or Lesson card in your graveyard."

    sagaChapter(1) {
        effect = Effects.Composite(
            listOf(
                Patterns.Library.mill(3),
                Effects.CreateFood()
            )
        )
    }

    sagaChapter(2) {
        target(
            "up to two target creatures you control",
            TargetCreature(count = 2, optional = true, filter = TargetFilter.CreatureYouControl)
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
            )
        )
    }

    sagaChapter(3) {
        effect = ConditionalEffect(
            condition = Conditions.GraveyardContains(
                GameObjectFilter.Creature or GameObjectFilter.Any.withSubtype("Lesson")
            ),
            effect = Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "185"
        artist = "Ittoku"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b18995c2-efe3-46ca-8204-e2dc0e42f6e3.jpg?1782135327"
    }
}
