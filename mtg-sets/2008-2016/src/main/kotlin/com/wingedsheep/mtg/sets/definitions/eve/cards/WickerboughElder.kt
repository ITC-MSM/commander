package com.wingedsheep.mtg.sets.definitions.eve.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wickerbough Elder
 * {3}{G}
 * Creature — Treefolk Shaman
 * 4/4
 *
 * This creature enters with a -1/-1 counter on it.
 * {G}, Remove a -1/-1 counter from this creature: Destroy target artifact or enchantment.
 *
 * The -1/-1 counter is what makes the printed 4/4 a 3/3 on the battlefield, and removing it as
 * part of the activation cost is what turns the body back into a 4/4 — so the counter is the
 * ability's one-shot fuel, not a drawback the card ever sheds for free.
 */
val WickerboughElder = card("Wickerbough Elder") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Shaman"
    power = 4
    toughness = 4
    oracleText = "This creature enters with a -1/-1 counter on it.\n" +
        "{G}, Remove a -1/-1 counter from this creature: Destroy target artifact or enchantment."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.MinusOneMinusOne,
            count = 1,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{G}"),
            Costs.RemoveCounterFromSelf(Counters.MINUS_ONE_MINUS_ONE)
        )
        target = Targets.ArtifactOrEnchantment
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Jesper Ejsing"
        flavorText = "\"Living scarecrows make a mockery of the natural order. Dead ones make fine hats.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/c/acc283b4-9106-4a32-88b4-000f2a3bed84.jpg"
    }
}
