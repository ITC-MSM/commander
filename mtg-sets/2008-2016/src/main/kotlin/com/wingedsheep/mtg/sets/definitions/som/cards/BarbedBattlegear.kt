package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Barbed Battlegear — Scars of Mirrodin #139
 * {3} · Artifact — Equipment
 *
 * Equipped creature gets +4/-1.
 * Equip {2}
 *
 * A layer-7c [ModifyStats] over [Filters.EquippedCreature] — the filter is spelled even though it
 * is the default, because the neighbouring keyword-granting family defaults the other way and an
 * omitted filter on an attachment is silently inert. The negative toughness half is not damage:
 * it lowers toughness in the same layer the +4 raises power, so a 1/1 wearing it is a 5/0 and dies
 * to state-based actions rather than being destroyed.
 */
val BarbedBattlegear = card("Barbed Battlegear") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +4/-1.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(4, -1, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "139"
        artist = "Steve Argyle"
        flavorText = "\"One need only look at the inhabitants of this world to see that they are forged halfway to perfection. All they need is a whisper of the Glorious Word.\"\n—Urabrask the Hidden"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03b80b2f-8d07-4ad3-9b20-4ba0fe9f37a2.jpg?1783941713"
    }
}
