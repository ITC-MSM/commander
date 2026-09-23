package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val PrudentFateseer = card("Prudent Fateseer") {
    manaCost = "{1}{W/U}{W/U}"
    colorIdentity = "UW"
    typeLine = "Creature — Dwarf Wizard"
    power = 1
    toughness = 4
    oracleText = "This creature enters prepared.\n" +
        "Whenever you scry or surveil, creatures you control get +1/+0 until end of turn. This ability triggers only once each turn."

    keywords(Keyword.PREPARED)

    triggeredAbility {
        trigger = Triggers.WheneverYouScryOrSurveil
        oncePerTurn = true
        effect = Patterns.Group.modifyStatsForAll(1, 0, GroupFilter(GameObjectFilter.Creature.youControl()))
        description = "Whenever you scry or surveil, creatures you control get +1/+0 until end of turn. " +
            "This ability triggers only once each turn."
    }

    prepare("Peer Review") {
        manaCost = "{2}{W/U}"
        typeLine = "Sorcery"
        oracleText = "Create a 2/2 colorless Wizard Soldier creature token named Cadet. Surveil 1."
        spell {
            effect = Effects.CreateToken(power = 2, toughness = 2, name = "Cadet", creatureTypes = setOf("Wizard", "Soldier"), imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318")
                .then(Patterns.Library.surveil(1))
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Gustavo Pelissari"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c1c790b-9e0e-4964-9ea3-554843907f06.jpg?1788329412"
        inBooster = false
    }
}
