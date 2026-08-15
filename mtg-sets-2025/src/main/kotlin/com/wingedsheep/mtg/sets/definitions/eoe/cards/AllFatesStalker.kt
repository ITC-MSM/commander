package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * All-Fates Stalker — {3}{W}
 * Creature — Drix Assassin
 * 2/3
 * When this creature enters, exile up to one target non-Assassin creature until this creature
 * leaves the battlefield.
 * Warp {1}{W}
 */
val AllFatesStalker = card("All-Fates Stalker") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Drix Assassin"
    oracleText = "When this creature enters, exile up to one target non-Assassin creature until this creature leaves the battlefield.\nWarp {1}{W} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "up to one target non-Assassin creature",
            TargetPermanent(
                optional = true,
                filter = TargetFilter(GameObjectFilter.Creature.notSubtype(Subtype("Assassin")))
            )
        )
        effect = Effects.ExileUntilLeaves(creature)
        description = "When this creature enters, exile up to one target non-Assassin creature until this creature leaves the battlefield."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
        description = "Return the exiled card to the battlefield under its owner's control." 
    }

    warp = "{1}{W}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "3"
        artist = "Lius Lasahido"
        flavorText = "\"You again?\""
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82ae4f7b-8122-4af6-8079-888eabf1a11e.jpg?1752946565"
    }
}
