package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Ashiok's Reaper
 * {3}{B}
 * Creature — Nightmare
 * 3/3
 *
 * Whenever an enchantment you control is put into a graveyard from the battlefield, draw a card.
 *
 * Same trigger shape as Warehouse Tabby: any enchantment you control hitting the graveyard from
 * the battlefield — destroyed, sacrificed, or a Role token falling off when it's replaced — so it
 * uses the generic `leavesBattlefield` factory with an `ANY` binding rather than a SELF-bound
 * constant. Role tokens count: per the WOE ruling, an enchantment token is put into its owner's
 * graveyard before it ceases to exist, so the Reaper sees it.
 */
val AshioksReaper = card("Ashiok's Reaper") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightmare"
    power = 3
    toughness = 3
    oracleText = "Whenever an enchantment you control is put into a graveyard from the battlefield, draw a card."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "79"
        artist = "Denis Zhbankov"
        flavorText = "Neva's dreams were haunted by memories of the invasion—oil-black tendrils " +
            "grasping at her through the mist. Or . . . was this something new?"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83957fe0-6500-420c-9b7a-2448a1c1d3b3.jpg?1783915111"

        ruling(
            "2023-09-01",
            "Enchantment tokens (such as Roles) that are sacrificed, destroyed, or would otherwise go to " +
                "the graveyard are put into their owner's graveyard before ceasing to exist. If you controlled " +
                "the token, Ashiok's Reaper's ability will trigger."
        )
    }
}
