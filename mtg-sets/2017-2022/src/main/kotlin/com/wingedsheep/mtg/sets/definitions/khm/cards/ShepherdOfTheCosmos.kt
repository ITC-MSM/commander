package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Shepherd of the Cosmos
 * {4}{W}{W}
 * Creature — Angel Warrior
 * 3/3
 * Flying
 * When this creature enters, return target permanent card with mana value 2 or less from your graveyard to the battlefield.
 * Foretell {3}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A six-mana flier that reanimates a cheap permanent. The target lives in the graveyard, so the
 * requirement is a graveyard-scoped [TargetObject] rather than a battlefield one; the mana-value cap
 * reads the card in the graveyard, where no continuous effect can change it.
 *
 * `fromZone = Zone.GRAVEYARD` on the move is load-bearing: without it the move looks up the target on
 * the battlefield, finds nothing, and the trigger resolves as a no-op.
 */
val ShepherdOfTheCosmos = card("Shepherd of the Cosmos") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel Warrior"
    oracleText = "Flying\n" +
        "When this creature enters, return target permanent card with mana value 2 or less from your graveyard to the battlefield.\n" +
        "Foretell {3}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target(
            "target",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.manaValueAtMost(2).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.Move(card, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
    }

    keywordAbility(KeywordAbility.foretell("{3}{W}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce33c84e-008c-48d8-a8e8-77cd19cc4f1f.jpg"
    }
}
