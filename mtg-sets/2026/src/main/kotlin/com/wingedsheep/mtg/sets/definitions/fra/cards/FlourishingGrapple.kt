package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Flourishing Grapple — Reality Fracture #102
 * {G} · Instant
 *
 * A Bite Down with an ability-strip first: the red-or-white opponent's permanent loses all
 * abilities until end of turn, then your creature deals damage equal to its power to it. The
 * strip lands before the damage, so an indestructible or protection ability it had is gone.
 */
val FlourishingGrapple = card("Flourishing Grapple") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature or planeswalker an opponent controls that's red or white loses all " +
        "abilities until end of turn. Target creature you control deals damage equal to its power to " +
        "that permanent."

    spell {
        val theirs = target(
            "target red or white creature or planeswalker an opponent controls",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.CreatureOrPlaneswalker.opponentControls()
                        .withAnyColor(Color.RED, Color.WHITE)
                )
            )
        )
        val mine = target(
            "target creature you control",
            TargetCreature(filter = TargetFilter.Creature.youControl())
        )
        effect = Effects.RemoveAllAbilities(theirs)
            .then(Effects.DealDamage(DynamicAmounts.targetPower(1), theirs, damageSource = mine))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Omar Rayyan"
        flavorText = "Lorehold believed in learning from history. Hexhaven made sure that history was brief."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f71958e9-6d6d-4393-8b49-567103b50877.jpg?1789470852"
        inBooster = false
    }
}
