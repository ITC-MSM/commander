package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Codex Shredder
 * {1}
 * Artifact
 *
 * {T}: Target player mills a card. (They put the top card of their library into their graveyard.)
 * {5}, {T}, Sacrifice this artifact: Return target card from your graveyard to your hand.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two activated abilities. The mill is [Patterns.Library.mill] pointed at the bound player, which
 * lowers to the gather-then-move pipeline that marks the move as a mill (so mill-watchers see
 * it). The recursion targets a card in the *graveyard* zone, which is a zoned [TargetObject]
 * rather than a permanent target.
 */
val CodexShredder = card("Codex Shredder") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "{T}: Target player mills a card. (They put the top card of their library into their graveyard.)\n" +
        "{5}, {T}, Sacrifice this artifact: Return target card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Tap
        val p = target("target player", Targets.Player)
        effect = Patterns.Library.mill(1, p)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        val c = target(
            "target card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Any.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.ReturnToHand(c)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "228"
        artist = "Jason Felix"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f7b632b-ee20-4082-8376-1dae53f91b70.jpg?1783940324"
    }
}
