package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Compelling Deterrence
 * {1}{U}
 * Instant
 * Return target nonland permanent to its owner's hand. Then that player discards a card
 * if you control a Zombie.
 */
val CompellingDeterrence = card("Compelling Deterrence") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target nonland permanent to its owner's hand. " +
        "Then that player discards a card if you control a Zombie."

    spell {
        val t = target("target nonland permanent", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        effect = Effects.ReturnToHand(t) then
            ConditionalEffect(
                condition = Conditions.YouControl(GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE)),
                effect = Effects.Discard(1, EffectTarget.PlayerRef(Player.OwnerOf("target nonland permanent"))),
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Seb McKinnon"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d89de92b-2890-42f5-a7cf-72c67e3b70c2.jpg?1782750069"
    }
}
