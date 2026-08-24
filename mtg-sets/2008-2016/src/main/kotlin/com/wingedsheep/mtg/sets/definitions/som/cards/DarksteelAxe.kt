package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Darksteel Axe — Scars of Mirrodin #149
 * {1} · Artifact — Equipment
 *
 * Indestructible (Effects that say "destroy" don't destroy this Equipment.)
 * Equipped creature gets +2/+0.
 * Equip {2}
 *
 * Indestructible is the Equipment's own keyword — it protects the Axe, not its bearer — so it is a
 * `keywords(...)` entry rather than a grant over [GroupFilter.attachedCreature]. Only the pump
 * crosses to the equipped creature.
 */
val DarksteelAxe = card("Darksteel Axe") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Indestructible (Effects that say \"destroy\" don't destroy this Equipment.)\n" +
        "Equipped creature gets +2/+0.\n" +
        "Equip {2}"

    keywords(Keyword.INDESTRUCTIBLE)

    staticAbility {
        ability = ModifyStats(2, 0, GroupFilter.attachedCreature())
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "149"
        artist = "Daniel Ljunggren"
        flavorText = "Heavier than it looks, tricky to wield, guaranteed to last."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b997c3e6-4b0e-4f4a-9f66-3fc1d8395494.jpg?1783941712"
    }
}
