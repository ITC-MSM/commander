package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Child of Alara
 * {W}{U}{B}{R}{G}
 * Legendary Creature — Avatar
 * 6 / 6
 * Trample
 * When Child of Alara dies, destroy all nonland permanents. They can't be regenerated.
 *
 * The wrath is [Patterns.Group.destroyAllPipeline], not a per-permanent iteration: the group is
 * gathered once from the battlefield and moved as one collection, so every permanent leaves
 * simultaneously and none of them sees the others go. `noRegenerate = true` is the printed "they
 * can't be regenerated" — the flag rides on the collection move rather than needing its own
 * `CantBeRegenerated` shield per permanent. [Triggers.Dies] is the plain battlefield → graveyard
 * self-trigger; the sweep reads nothing off the dying Child, so no last-known information is
 * involved.
 */
val ChildOfAlara = card("Child of Alara") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Avatar"
    power = 6
    toughness = 6
    oracleText = "Trample\n" +
        "When Child of Alara dies, destroy all nonland permanents. They can't be regenerated."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Group.destroyAllPipeline(
            filter = GameObjectFilter.NonlandPermanent,
            noRegenerate = true
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "101"
        artist = "Steve Argyle"
        flavorText = "The progeny of the Maelstrom shows no allegiance—and no mercy—to any of the five shards."
        imageUri = "https://cards.scryfall.io/normal/front/8/6/863f701f-fba2-48db-95ef-0926986cdac9.jpg"
    }
}
