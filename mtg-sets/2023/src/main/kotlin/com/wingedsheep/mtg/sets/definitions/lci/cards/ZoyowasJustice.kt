package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Zoyowa's Justice
 * {1}{R}
 * Instant
 *
 * The owner of target artifact or creature with mana value 1 or greater shuffles it into their
 * library. Then that player discovers X, where X is its mana value.
 *
 * The discover (and the shuffle) are performed by the *owner* of the target, who need not be the
 * spell's controller, so the whole action is wrapped in [Effects.ForEachPlayer] keyed to
 * [Player.OwnerOf] the target — the loop rebinds the resolution context's controller to the owner,
 * so the discover walks the owner's library and the cast/hand decision is presented to the owner.
 * X is read as the target's mana value ([EntityReference.Target] → [EntityNumericProperty.ManaValue]),
 * an intrinsic characteristic that survives the shuffle (the entity keeps its id and CardComponent
 * across the zone change).
 */
val ZoyowasJustice = card("Zoyowa's Justice") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "The owner of target artifact or creature with mana value 1 or greater shuffles it " +
        "into their library. Then that player discovers X, where X is its mana value. (They exile " +
        "cards from the top of their library until they exile a nonland card with that mana value or " +
        "less. They cast it without paying its mana cost or put it into their hand. They put the rest " +
        "on the bottom in a random order.)"

    spell {
        val permanent = target(
            "target artifact or creature",
            TargetPermanent(filter = TargetFilter.CreatureOrArtifact.manaValueAtLeast(1))
        )
        effect = Effects.ForEachPlayer(
            Player.OwnerOf("target artifact or creature"),
            listOf(
                Effects.ShuffleIntoLibrary(permanent),
                Effects.Discover(
                    DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.ManaValue)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Néstor Ossandón Leal"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04839717-d2f9-481d-9d13-e4038dbcbb0e.jpg?1782694472"
    }
}
