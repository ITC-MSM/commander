package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Season of Renewal — Kamigawa: Neon Dynasty #205 (canonical printing)
 * {2}{G} · Instant
 *
 * Choose one or both —
 * • Return target creature card from your graveyard to your hand.
 * • Return target enchantment card from your graveyard to your hand.
 *
 * "Choose one or both" is the *count* — `chooseCount = 2, minChooseCount = 1` (CR 700.2) — not a
 * third mode. An enchantment creature card in your graveyard is a legal target for either mode,
 * but each mode targets separately, so both modes can name the same card and the second does
 * nothing when the first has already moved it.
 */
val SeasonOfRenewal = card("Season of Renewal") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Return target creature card from your graveyard to your hand.\n" +
        "• Return target enchantment card from your graveyard to your hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Return target creature card from your graveyard to your hand.") {
                val t = target(
                    "creature card in your graveyard",
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Creature.ownedByYou(), zone = Zone.GRAVEYARD),
                    ),
                )
                effect = Effects.ReturnToHand(t)
            }
            mode("Return target enchantment card from your graveyard to your hand.") {
                val t = target(
                    "enchantment card in your graveyard",
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Enchantment.ownedByYou(), zone = Zone.GRAVEYARD),
                    ),
                )
                effect = Effects.ReturnToHand(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "205"
        artist = "Rovina Cai"
        flavorText = "Pavement ripped like paper as the ancient kami awoke from her long slumber " +
            "beneath the city."
        imageUri = "https://cards.scryfall.io/normal/front/8/4/841f0ec2-94c7-4cec-94bb-b365084ca45f.jpg?1783923842"
    }
}
