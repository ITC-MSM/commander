package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Amalia Benavides Aguirre — {W}{B}
 * Legendary Creature — Vampire Scout
 * 2/2
 *
 * Ward—Pay 3 life.
 * Whenever you gain life, Amalia Benavides Aguirre explores. Then destroy all other
 * creatures if its power is exactly 20.
 *
 * Implementation notes:
 *
 * **Ward—Pay 3 life:** `KeywordAbility.Ward(WardCost.Life(3))`. When an opponent
 * targets Amalia, they must pay 3 life or the spell/ability is countered (CR 702.21).
 *
 * **YouGainLife trigger:** Fires once per life-gain event (`Triggers.YouGainLife`).
 * The triggered effect is a sequential `Composite`:
 *
 * 1. `Effects.Explore(EffectTarget.Self)` — Amalia explores (CR 701.44): reveal the top
 *    card of your library; a land goes to your hand (no counter); a nonland puts a +1/+1
 *    counter on Amalia and gives the controller the option to put the card in the graveyard.
 *
 * 2. `ConditionalEffect(Conditions.SourceMatches(GameObjectFilter.Creature.power(20)), ...)` —
 *    AFTER explore completes, check Amalia's projected power. If exactly 20, destroy all
 *    other creatures. `Patterns.Group.destroyAll(GroupFilter(GameObjectFilter.Creature,
 *    excludeSelf = true))` gathers every creature on the battlefield except the source
 *    (Amalia) and destroys each.
 *
 * Ordering is rules-faithful: explore runs first and may change Amalia's power via a
 * +1/+1 counter; the power-20 check reads the post-explore projected power.
 */
val AmaliaBenavidesAguirre = card("Amalia Benavides Aguirre") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Vampire Scout"
    oracleText = "Ward—Pay 3 life.\n" +
        "Whenever you gain life, Amalia Benavides Aguirre explores. Then destroy all other " +
        "creatures if its power is exactly 20. (To have this creature explore, reveal the top " +
        "card of your library. Put that card into your hand if it's a land. Otherwise, put a " +
        "+1/+1 counter on this creature, then put the card back or put it into your graveyard.)"
    power = 2
    toughness = 2

    // Ward—Pay 3 life (CR 702.21a): whenever an opponent targets this creature, they must
    // pay 3 life or their spell/ability is countered.
    keywordAbility(KeywordAbility.Ward(WardCost.Life(3)))

    // Whenever you gain life, Amalia explores, then wipe if power == 20.
    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.Composite(listOf(
            // Step 1: Amalia explores. Reveal top card; land → hand (no counter, no pause);
            // nonland → +1/+1 counter on Amalia, then optional graveyard decision.
            Effects.Explore(EffectTarget.Self),
            // Step 2: After explore, check Amalia's projected power. If exactly 20, destroy
            // all other creatures (excludeSelf = true excludes Amalia as the source).
            ConditionalEffect(
                condition = Conditions.SourceMatches(GameObjectFilter.Creature.power(20)),
                effect = Patterns.Group.destroyAll(GroupFilter(GameObjectFilter.Creature, excludeSelf = true))
            )
        ))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "221"
        artist = "Alix Branwyn"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9acf80a5-f2ca-45b4-aca8-fbc690e35401.jpg?1782694434"
    }
}
