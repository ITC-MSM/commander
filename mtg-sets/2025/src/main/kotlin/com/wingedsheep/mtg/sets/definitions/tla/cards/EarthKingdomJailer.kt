package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Earth Kingdom Jailer
 * {2}{W}
 * Creature — Human Soldier Ally
 * 3/3
 *
 * When this creature enters, exile up to one target artifact, creature, or enchantment
 * an opponent controls with mana value 3 or greater until this creature leaves the
 * battlefield.
 *
 * "Up to one target" is an optional target ([TargetPermanent.optional]); the exile is
 * linked to this creature via [Effects.ExileUntilLeaves], and the LeavesBattlefield
 * trigger returns the card with [Effects.ReturnLinkedExileUnderOwnersControl].
 */
val EarthKingdomJailer = card("Earth Kingdom Jailer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier Ally"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, exile up to one target artifact, creature, or enchantment an opponent controls with mana value 3 or greater until this creature leaves the battlefield."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "up to one target artifact, creature, or enchantment an opponent controls with mana value 3 or greater",
            TargetPermanent(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.ArtifactCreatureOrEnchantment
                        .opponentControls()
                        .manaValueAtLeast(3)
                )
            )
        )
        effect = Effects.ExileUntilLeaves(permanent)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Danciao"
        flavorText = "\"Go ahead and run. My ostrich-horse loves a good chase.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/2/9207362c-3605-4794-803f-ad1d0175fcca.jpg?1764119978"
    }
}
