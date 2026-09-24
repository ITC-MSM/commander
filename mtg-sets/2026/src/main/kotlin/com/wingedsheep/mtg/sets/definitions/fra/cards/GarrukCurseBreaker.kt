package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Garruk, Curse Breaker
 * {3}{G}{G}
 * Legendary Planeswalker — Garruk
 * Starting Loyalty: 5
 *
 * - The first line is Garruk's Uprising's draw trigger on a planeswalker: a filter-scoped
 *   enters trigger over creatures you control with power 4 or greater (the filter reads the
 *   entering creature's projected power, so static pumps and entry counters count).
 * - +2 is one "up to two target lands" requirement (distinct objects, zero allowed), untapped
 *   per target with [ForEachTargetEffect].
 * - −4 installs a filter-scoped delayed trigger ([Triggers.CreaturesAttackYourOpponent]) that lives
 *   until Garruk's controller's next turn ([DelayedTriggerExpiry.UntilControllersNextTurn]) — it is
 *   Garruk's delayed ability, so it keeps working after Garruk leaves the battlefield. It fires once
 *   per attack declaration in which at least one creature attacks one of your opponents, and
 *   "those creatures" are the creatures attacking one of your opponents
 *   ([GameObjectFilter.attackingAnOpponent]) — creatures attacking a planeswalker or battle don't
 *   get the bonus.
 */
val GarrukCurseBreaker = card("Garruk, Curse Breaker") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Planeswalker — Garruk"
    startingLoyalty = 5
    oracleText = "Whenever a creature you control with power 4 or greater enters, draw a card.\n" +
        "+2: Untap up to two target lands.\n" +
        "−3: Create a 4/4 green Beast creature token with trample.\n" +
        "−4: Until your next turn, whenever one or more creatures attack one of your opponents, " +
        "those creatures get +2/+2 and gain trample until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            GameObjectFilter.Creature.youControl().powerAtLeast(4),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DrawCards(1)
        description = "Whenever a creature you control with power 4 or greater enters, draw a card."
    }

    // +2: Untap up to two target lands.
    loyaltyAbility(+2) {
        target(
            "up to two target lands",
            TargetPermanent(count = 2, optional = true, filter = TargetFilter(GameObjectFilter.Land)),
        )
        effect = ForEachTargetEffect(listOf(Effects.Untap(EffectTarget.ContextTarget(0))))
        description = "Untap up to two target lands."
    }

    // −3: Create a 4/4 green Beast creature token with trample.
    loyaltyAbility(-3) {
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Beast"),
            keywords = setOf(Keyword.TRAMPLE),
            imageUri = "https://cards.scryfall.io/normal/front/8/5/859bda9a-fa90-4ad3-b0c1-6fc62e27c12f.jpg?1789736256",
        )
        description = "Create a 4/4 green Beast creature token with trample."
    }

    // −4: Until your next turn, whenever one or more creatures attack one of your opponents,
    //     those creatures get +2/+2 and gain trample until end of turn.
    loyaltyAbility(-4) {
        effect = CreateDelayedTriggerEffect(
            trigger = Triggers.CreaturesAttackYourOpponent,
            effect = Patterns.Group.pumpAndGrantToAll(
                power = 2,
                toughness = 2,
                keyword = Keyword.TRAMPLE,
                filter = GroupFilter(GameObjectFilter.Creature.attackingAnOpponent()),
            ),
            expiry = DelayedTriggerExpiry.UntilControllersNextTurn,
        )
        description = "Until your next turn, whenever one or more creatures attack one of your " +
            "opponents, those creatures get +2/+2 and gain trample until end of turn."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "259"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90ca5812-ceb5-46bd-b049-aed7ff10e6af.jpg?1788329370"
        inBooster = false
    }
}
