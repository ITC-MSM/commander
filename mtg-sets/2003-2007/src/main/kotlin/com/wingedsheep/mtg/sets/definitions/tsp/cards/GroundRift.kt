package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ground Rift
 * {R}
 * Sorcery
 * Target creature without flying can't block this turn.
 * Storm (When you cast this spell, copy it for each spell cast before it this turn. You may
 * choose new targets for the copies.)
 *
 * "Without flying" is a restriction on the *target*, not the effect: `NotKeyword(FLYING)` on
 * the requirement's filter, so a creature that gains flying in response is no longer a legal
 * target and the spell (or that copy) fizzles under CR 608.2b.
 *
 * Storm copies the spell off `script.spellEffect`, so this stays a plain `spell { effect = … }`.
 */
val GroundRift = card("Ground Rift") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Target creature without flying can't block this turn.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn. You may choose new targets for the copies.)"

    spell {
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING)))
        )
        effect = Effects.CantBlock(t)
    }

    keywords(Keyword.STORM)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "162"
        artist = "Thomas M. Baxa"
        flavorText = "Some time rifts didn't take away the people but just the ground they stood on."
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62333783-6a18-4461-88ce-1c37eaf64e2b.jpg"
    }
}
