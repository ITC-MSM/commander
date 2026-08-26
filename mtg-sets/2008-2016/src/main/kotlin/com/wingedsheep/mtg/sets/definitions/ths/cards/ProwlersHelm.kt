package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Prowler's Helm
 * {2}
 * Artifact — Equipment
 *
 * Equipped creature can't be blocked except by Walls.
 * Equip {2}
 *
 * The evasion is about the *equipped creature*, never the Equipment itself, so the static is scoped
 * with [GroupFilter.attachedCreature] — `CantBeBlockedExceptBy` defaults to `GroupFilter.source()`,
 * which would land the restriction on a permanent that never blocks or is blocked. Invisibility
 * (LEA) is the same shape on an Aura.
 */
val ProwlersHelm = card("Prowler's Helm") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature can't be blocked except by Walls.\nEquip {2}"

    staticAbility {
        ability = CantBeBlockedExceptBy(
            blockerFilter = GameObjectFilter.Permanent.withSubtype(Subtype.WALL),
            filter = GroupFilter.attachedCreature()
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Igor Kieryluk"
        flavorText = "\"The youths prattle on about heroic deeds, but avoiding the noose is a feat more daring than their entire careers.\"\n—Basarios the Blade"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c100a22c-bf34-42b7-9339-4733698c0935.jpg"
    }
}
