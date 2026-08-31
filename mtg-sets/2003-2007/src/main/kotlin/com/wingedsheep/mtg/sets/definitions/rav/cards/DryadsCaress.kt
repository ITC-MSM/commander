package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dryad's Caress — Ravnica: City of Guilds #160
 * {4}{G}{G} · Instant
 *
 * You gain 1 life for each creature on the battlefield. If {W} was spent to cast this spell, untap
 * all creatures you control.
 *
 * "Each creature on the battlefield" is every creature, whoever controls it —
 * `AggregateBattlefield(Player.Each, Creature)`, the same amount Blunt the Assault counts — read on
 * resolution (CR 608.2).
 *
 * The white rider asks what was *paid*, not what colour the spell is: per the Ravnica rulings it
 * checks only that at least one {W} went into the cost, so one Plains or a Temple Garden is enough
 * and the amount doesn't matter. A copy of the spell had no mana spent for it and so never untaps.
 */
val DryadsCaress = card("Dryad's Caress") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "You gain 1 life for each creature on the battlefield. " +
        "If {W} was spent to cast this spell, untap all creatures you control."

    spell {
        effect = Effects.GainLife(
            DynamicAmount.AggregateBattlefield(Player.Each, GameObjectFilter.Creature)
        ).then(
            ConditionalEffect(
                condition = Conditions.ManaSpentToCastIncludes(requiredWhite = 1),
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature.youControl()),
                    Effects.Untap(EffectTarget.Self),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Randy Gallegos"
        flavorText = "\"I awoke to the face of beauty, my body fully healed. In that moment I knew my " +
            "destiny was the Conclave's to shape.\"\n—Rogad, Selesnya initiate"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cf707f2-1a62-458e-bcc8-8b1cd081f225.jpg?1783943640"
        ruling(
            "2005-10-01",
            "The spell checks on resolution to see if any mana of the stated color was spent to pay " +
                "its cost. It doesn't matter how much mana of that color was spent."
        )
        ruling(
            "2005-10-01",
            "If the spell is copied (such as with Twincast), the copy will never have had mana of the " +
                "stated color paid for it, no matter what colors were spent on the original spell."
        )
    }
}
