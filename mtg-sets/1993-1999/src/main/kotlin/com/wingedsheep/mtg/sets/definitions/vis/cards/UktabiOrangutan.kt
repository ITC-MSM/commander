package com.wingedsheep.mtg.sets.definitions.vis.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Uktabi Orangutan
 * {2}{G}
 * Creature — Ape
 * 2/2
 * When this creature enters, destroy target artifact.
 */
val UktabiOrangutan = card("Uktabi Orangutan") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ape"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, destroy target artifact."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target artifact", TargetPermanent(filter = TargetFilter.Artifact))
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "123"
        artist = "Una Fricker"
        flavorText = "\"Is it true that the apes wear furs of gold when they marry?\" —Rana, Suq'Ata market fool"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/101c7d58-43cc-4ebd-87f1-2016fbff56dd.jpg?1783946979"
    }
}
