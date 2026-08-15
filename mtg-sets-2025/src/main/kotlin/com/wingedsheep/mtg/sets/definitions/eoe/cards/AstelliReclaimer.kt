package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Astelli Reclaimer
 * {3}{W}{W}
 * Creature — Angel Warrior
 * 5/4
 *
 * Flying
 * When this creature enters, return target noncreature, nonland permanent card with mana
 * value X or less from your graveyard to the battlefield, where X is the amount of mana
 * spent to cast this creature.
 * Warp {2}{W}
 *
 * X is the mana actually paid to cast this creature: 5 cast for {3}{W}{W}, 3 cast with warp
 * for {2}{W}, 0 if cast for free or created as a copy on the stack (per Scryfall rulings).
 * The mana-value-vs-spent comparison is expressed as the
 * [com.wingedsheep.sdk.scripting.predicates.CardPredicate.ManaValueAtMostEntityManaSpent]
 * target predicate, which reads the source's `CastRecordComponent` snapshot.
 */
val AstelliReclaimer = card("Astelli Reclaimer") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel Warrior"
    power = 5
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, return target noncreature, nonland permanent card with " +
        "mana value X or less from your graveyard to the battlefield, where X is the amount " +
        "of mana spent to cast this creature.\n" +
        "Warp {2}{W} (You may cast this card from your hand for its warp cost. Exile this " +
        "creature at the beginning of the next end step, then you may cast it from exile on " +
        "a later turn.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target(
            "noncreature, nonland permanent card with mana value X or less in your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = (GameObjectFilter.NoncreaturePermanent and GameObjectFilter.Nonland)
                        .ownedByYou()
                        .manaValueAtMostEntityManaSpent(EntityReference.Source),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOntoBattlefield(card)
        description = "When this creature enters, return target noncreature, nonland permanent card " +
            "with mana value X or less from your graveyard to the battlefield, where X is the amount " +
            "of mana spent to cast this creature."
    }

    warp = "{2}{W}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Carly Milligan"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fb36405-cd28-432f-b0a4-e74ff8be928d.jpg?1752946568"
        ruling("2025-07-25", "Astelli Reclaimer's enters ability cares about the mana you actually paid to cast Astelli Reclaimer, not its mana cost. If you cast Astelli Reclaimer for {3}{W}{W}, X is 5. If you cast it with warp for {2}{W}, X is 3. Any effects that increase or decrease the cost to cast it will also be taken into account.")
        ruling("2025-07-25", "If an effect allows you to cast Astelli Reclaimer without paying its mana cost, X will be 0.")
        ruling("2025-07-25", "If a copy of Astelli Reclaimer is created on the stack, no mana was spent to cast the copy, so X will be 0. The amount of mana spent to cast the original spell is not copied.")
        ruling("2025-07-25", "If a card in your graveyard has {X} in its mana cost, X is 0 for the purpose of determining its mana value.")
    }
}
