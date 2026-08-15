package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Broodrage Mycoid
 * {3}{B}
 * Creature — Fungus
 * 4/3
 *
 * At the beginning of your end step, if you descended this turn, create a 1/1 black
 * Fungus creature token with "This token can't block."
 * (You descended if a permanent card was put into your graveyard from anywhere.)
 *
 * "Descended this turn" is CR 700.11: at least one nontoken permanent card was put into
 * your graveyard from any zone this turn. The intervening-if gate fires only once per end
 * step regardless of how many times you descended, and cannot trigger if you have not yet
 * descended as the end step begins.
 *
 * The token's "This token can't block" is a static ability on the token itself, modelled
 * as [CantBlock] with [GroupFilter.source()] — identical to the decayed keyword's
 * cant-block component and to Kavu Aggressor's printed restriction.
 */
val BroodrageMycoid = card("Broodrage Mycoid") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Fungus"
    power = 4
    toughness = 3
    oracleText = "At the beginning of your end step, if you descended this turn, create a 1/1 black Fungus creature token with \"This token can't block.\" " +
        "(You descended if a permanent card was put into your graveyard from anywhere.)"

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouDescendedThisTurn()
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Fungus"),
            staticAbilities = listOf(CantBlock(GroupFilter.source())),
            imageUri = "https://cards.scryfall.io/normal/front/7/3/73ff66e3-ea24-4542-887f-c41abb1759e6.jpg?1783913609",
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Domenico Cava"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08318a16-a9ed-42c8-9433-876b7a72e368.jpg?1782694534"
        ruling("2023-11-10", "Some cards refer to a player who has \"descended this turn.\" This means that a permanent card has been put into that player's graveyard from anywhere this turn.")
        ruling("2023-11-10", "A permanent card is an artifact, battle, creature, enchantment, land, or planeswalker card. Tokens are not cards, and while tokens are put into the graveyard before ceasing to exist, that action doesn't count as a player having descended.")
        ruling("2023-11-10", "Abilities that begin with \"At the beginning of your end step, if you descended this turn\" will trigger only once during your end step, no matter how many times you descended this turn. However, if you haven't descended this turn as your end step begins, the ability won't trigger at all. It's not possible to put a permanent card into your graveyard during the end step in time to have the ability trigger.")
    }
}
