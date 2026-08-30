package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ojutai's Command
 * {2}{W}{U}
 * Instant
 *
 * Choose two —
 * • Return target creature card with mana value 2 or less from your graveyard to the battlefield.
 * • You gain 4 life.
 * • Counter target creature spell.
 * • Draw a card.
 *
 * "Choose two —" over four modes is `modal(chooseCount = 2)`; two of the four target, so those two
 * declare their requirements inside their own `mode` block and the other two declare none.
 *
 * The reanimation mode keeps its `fromZone = GRAVEYARD` guard ([Effects.PutOntoBattlefieldFromGraveyard])
 * even though it targets — the asymmetry with the graveyard-to-*hand* return next door is
 * deliberate and empirical, and the facade's KDoc records why. The mana-value clause is a
 * refinement of the existing [TargetFilter.CreatureInYourGraveyard] rather than a hand-rolled
 * filter, which keeps the card-predicate order the same as every other "creature card with mana
 * value N or less" in the corpus.
 *
 * "Counter target creature spell" is [Effects.CounterSpell] over a stack-zone creature filter; the
 * effect itself carries no target because the requirement supplies it.
 */
val OjutaisCommand = card("Ojutai's Command") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Instant"
    oracleText = "Choose two —\n" +
        "• Return target creature card with mana value 2 or less from your graveyard to the battlefield.\n" +
        "• You gain 4 life.\n" +
        "• Counter target creature spell.\n" +
        "• Draw a card."

    spell {
        modal(chooseCount = 2) {
            mode("Return target creature card with mana value 2 or less from your graveyard to the battlefield") {
                val creatureCard = target(
                    "creature card with mana value 2 or less in your graveyard",
                    TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMost(2))
                )
                effect = Effects.PutOntoBattlefieldFromGraveyard(creatureCard)
            }
            mode("You gain 4 life") {
                effect = Effects.GainLife(4)
            }
            mode("Counter target creature spell") {
                target = Targets.CreatureSpell
                effect = Effects.CounterSpell()
            }
            mode("Draw a card") {
                effect = Effects.DrawCards(1)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "227"
        artist = "Willian Murai"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7a7f500-594d-4c7b-80e8-54ae1ada2444.jpg?1783938571"
    }
}
