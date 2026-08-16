package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Aethershield Artificer
 * {3}{W}
 * Creature — Dwarf Artificer
 * 3/3
 * At the beginning of combat on your turn, target artifact creature you control gets +2/+2 and gains indestructible until end of turn. (Damage and effects that say "destroy" don't destroy it.)
 */
val AethershieldArtificer = card("Aethershield Artificer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Artificer"
    power = 3
    toughness = 3
    oracleText = "At the beginning of combat on your turn, target artifact creature you control gets +2/+2 and gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target(
            "artifact creature you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.ArtifactCreature.youControl()))
        )
        effect = Effects.ModifyStats(2, 2, creature)
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature, Duration.EndOfTurn))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Izzy"
        flavorText = "Most smiths shape metal, but some prefer more delicate materials."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/226f7c45-db9f-4d48-b575-4d2f1904c963.jpg?1783934613"
    }
}
