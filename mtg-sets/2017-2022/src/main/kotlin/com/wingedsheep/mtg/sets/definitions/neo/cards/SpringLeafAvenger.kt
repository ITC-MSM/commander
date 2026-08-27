package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Spring-Leaf Avenger — Kamigawa: Neon Dynasty #208 (canonical printing)
 * {3}{G}{G} · Creature — Insect Ninja · 6/5
 *
 * Ninjutsu {3}{G}
 * Whenever this creature deals combat damage to a player, return target permanent card from your
 * graveyard to your hand.
 *
 * "Permanent card" is the whole permanent family, not just creatures — a land, artifact,
 * enchantment or planeswalker in your graveyard all qualify.
 */
val SpringLeafAvenger = card("Spring-Leaf Avenger") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Ninja"
    power = 6
    toughness = 5
    oracleText = "Ninjutsu {3}{G} ({3}{G}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, return target permanent card " +
        "from your graveyard to your hand."

    ninjutsu("{3}{G}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val t = target(
            "permanent card in your graveyard",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Permanent.ownedByYou(), zone = Zone.GRAVEYARD),
            ),
        )
        effect = Effects.ReturnToHand(t)
        description = "Whenever this creature deals combat damage to a player, return target " +
            "permanent card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "208"
        artist = "Wisnu Tan"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74b1ba9c-ee62-461f-8422-f791274e2f1c.jpg?1783923840"
    }
}
