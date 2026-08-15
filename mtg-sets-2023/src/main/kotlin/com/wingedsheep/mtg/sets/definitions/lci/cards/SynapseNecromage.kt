package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Synapse Necromage
 * {2}{B}
 * Creature — Fungus Wizard
 * 3/1
 *
 * When this creature dies, create two 1/1 black Fungus creature tokens with
 * "This token can't block."
 *
 * Dies is CR 700.4 (battlefield to graveyard); modelled as the SELF-bound
 * [Triggers.Dies]. The tokens are identical to Broodrage Mycoid / The Mycotyrant's
 * 1/1 black Fungus "can't block" token — the "This token can't block" restriction is a
 * static ability on the token itself, [CantBlock] with [GroupFilter.source()].
 */
val SynapseNecromage = card("Synapse Necromage") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Fungus Wizard"
    power = 3
    toughness = 1
    oracleText = "When this creature dies, create two 1/1 black Fungus creature tokens with \"This token can't block.\""

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Fungus"),
            staticAbilities = listOf(CantBlock(GroupFilter.source())),
            imageUri = "https://cards.scryfall.io/normal/front/7/3/73ff66e3-ea24-4542-887f-c41abb1759e6.jpg?1783913609",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Piotr Foksowicz"
        flavorText = "\"It's no good killing them? Then what do we do? Distract them with a tea party?\"\n—Mervin, Brazen Coalition prospector"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efeb22a4-37bf-487a-9cf4-74de4cbbc0a3.jpg?1782694510"
    }
}
