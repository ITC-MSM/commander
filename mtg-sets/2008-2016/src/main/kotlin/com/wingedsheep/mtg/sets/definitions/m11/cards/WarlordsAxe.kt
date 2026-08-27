package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Warlord's Axe
 * {3}
 * Artifact — Equipment
 *
 * Equipped creature gets +3/+1.
 * Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * A vanilla Equipment: one [ModifyStats] static whose filter is left at its default
 * (`GroupFilter.attachedCreature()`, i.e. the equipped creature) plus the synthesized
 * `equipAbility` — the sorcery-speed, "target creature you control" activated ability the DSL
 * lowers, which also records the printed equip cost on the card.
 */
val WarlordsAxe = card("Warlord's Axe") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +3/+1.\n" +
        "Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(3, 1)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Franz Vohwinkel"
        flavorText = "To split wood with it would be sacrilege. This tool has but one purpose, and that is war."
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41ab6393-df29-475b-b56d-c56eb95de05d.jpg?1783941788"
    }
}
