package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Solitary Cell — Reality Fracture #149
 * {R}{W} · Artifact · Rare
 *
 * When this artifact enters, exile target nonland permanent an opponent controls with mana value
 * 3 or less until this artifact leaves the battlefield.
 * {1}, {T}, Discard a legendary card: Draw a card.
 *
 * The Banishing Light pair ([Effects.ExileUntilLeaves] + a leaves trigger returning the linked
 * exile) with a mana-value cap on the target, and Key to the Side-Door's legendary discard cost
 * without its name-matching clause.
 */
val SolitaryCell = card("Solitary Cell") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, exile target nonland permanent an opponent controls with mana " +
        "value 3 or less until this artifact leaves the battlefield.\n" +
        "{1}, {T}, Discard a legendary card: Draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "target nonland permanent an opponent controls with mana value 3 or less",
            TargetPermanent(filter = TargetFilter.NonlandPermanentOpponentControls.manaValueAtMost(3))
        )
        effect = Effects.ExileUntilLeaves(permanent)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.Discard(GameObjectFilter.Any.legendary())
        )
        effect = Effects.DrawCards(1)
        description = "Draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "149"
        artist = "Andrea Piparo"
        flavorText = "Denied all tools of necromancy, Liliana endured her least favorite feeling: boredom."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/5142bbb6-194c-4b12-b11a-1a21c9fe81a6.jpg?1788260690"
        inBooster = false
    }
}
