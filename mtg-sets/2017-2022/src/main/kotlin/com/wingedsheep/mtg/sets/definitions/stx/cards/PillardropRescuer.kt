package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Pillardrop Rescuer — Strixhaven: School of Mages #23 (canonical printing)
 * {4}{W} · Creature — Spirit Cleric · 2/2
 *
 * Flying
 * When this creature enters, return target creature card with mana value 3 or less from your graveyard to your hand.
 *
 * Flying is the bare keyword. The ETB is a non-optional [Triggers.EntersBattlefield] whose target is
 * [TargetFilter.CreatureInYourGraveyard] narrowed to `manaValueAtMost(3)`, returned with a plain
 * [Effects.ReturnToHand] — the graveyard restriction lives in the target's zone, not in the move.
 */
val PillardropRescuer = card("Pillardrop Rescuer") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Cleric"
    oracleText =
        "Flying\n" +
        "When this creature enters, return target creature card with mana value 3 or less from your graveyard to your hand."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val rescued = target(
            "target",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMost(3))
        )
        effect = Effects.ReturnToHand(rescued)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Jason A. Engle"
        flavorText = "Gentle hands of stone whisk the careless from the crumbling ruins of Pillardrop."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0884666e-8f9b-44b1-a1e3-c1c8941e2152.jpg?1783927388"
    }
}
