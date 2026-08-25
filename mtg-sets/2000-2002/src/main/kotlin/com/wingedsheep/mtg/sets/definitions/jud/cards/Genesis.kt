package com.wingedsheep.mtg.sets.definitions.jud.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Genesis
 * {4}{G}
 * Creature — Incarnation
 * 4/4
 * At the beginning of your upkeep, if this creature is in your graveyard, you may pay {2}{G}. If you do, return target creature card from your graveyard to your hand.
 *
 * The Judgment incarnation cycle: the upkeep ability functions from the graveyard, which is
 * `triggerZone = Zone.GRAVEYARD` (the "if this creature is in your graveyard" clause *is* the zone
 * declaration, not a separate intervening-if). Same shape as Pyre Zombie and Ghastly Remains.
 */
val Genesis = card("Genesis") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Incarnation"
    power = 4
    toughness = 4
    oracleText = "At the beginning of your upkeep, if this creature is in your graveyard, you may pay {2}{G}. If you do, return target creature card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        triggerZone = Zone.GRAVEYARD
        val t = target("target", TargetObject(filter = TargetFilter.CreatureInYourGraveyard))
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}{G}"),
            effect = Effects.ReturnToHand(t),
        )
        description = "At the beginning of your upkeep, if this creature is in your graveyard, " +
            "you may pay {2}{G}. If you do, return target creature card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "117"
        artist = "Mark Zug"
        flavorText = "\"First through the Riftstone was Genesis—and the world was lifeless no more.\"\n—*Scroll of Beginnings*"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b43aee5e-b12e-43ea-9fae-16310acdc640.jpg?1783945111"
    }
}
