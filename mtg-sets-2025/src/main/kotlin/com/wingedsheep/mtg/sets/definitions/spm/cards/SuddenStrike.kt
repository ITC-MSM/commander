package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Sudden Strike
 * {1}{W}
 * Instant
 * Destroy target attacking or blocking creature.
 */
val SuddenStrike = card("Sudden Strike") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target attacking or blocking creature."
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Le Vuong"
        flavorText = "Sometimes, one decisive blow is all it takes to turn a fight around."
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6eea2718-93d2-4d83-9b5d-eb943a0f1d11.jpg?1757376858"
    }
}
