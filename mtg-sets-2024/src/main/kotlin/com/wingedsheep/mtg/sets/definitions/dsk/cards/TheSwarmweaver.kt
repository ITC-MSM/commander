package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * The Swarmweaver
 * {2}{B}{G}
 * Legendary Artifact Creature — Scarecrow
 * 2/3
 *
 * When The Swarmweaver enters, create two 1/1 black and green Insect creature tokens with flying.
 * Delirium — As long as there are four or more card types among cards in your graveyard, Insects
 * and Spiders you control get +1/+1 and have deathtouch.
 *
 * The ETB makes two 1/1 black-green flying Insect tokens (DSK Insect token). Delirium is an ability
 * word with no rules meaning; the conditional lord is two static abilities over `Insects and Spiders
 * you control` ([GameObjectFilter.Creature].withAnyOfSubtypes(Insect, Spider).youControl()), each
 * gated by [Conditions.Delirium] (four+ distinct card types in your graveyard): a +1/+1 [ModifyStats]
 * (layer 7c) and a deathtouch [GrantKeyword] (layer 6). The filter carries no `excludeSelf` because
 * the printed text says "Insects and Spiders you control", not "other" — and The Swarmweaver is a
 * Scarecrow, so it never matches the filter anyway.
 */
val TheSwarmweaver = card("The Swarmweaver") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Artifact Creature — Scarecrow"
    power = 2
    toughness = 3
    oracleText = "When The Swarmweaver enters, create two 1/1 black and green Insect creature " +
        "tokens with flying.\n" +
        "Delirium — As long as there are four or more card types among cards in your graveyard, " +
        "Insects and Spiders you control get +1/+1 and have deathtouch."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK, Color.GREEN),
            creatureTypes = setOf("Insect"),
            keywords = setOf(Keyword.FLYING),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/3/7/377f1a20-b270-4b07-9892-7170cd0bee38.jpg?1726236771"
        )
        description = "When The Swarmweaver enters, create two 1/1 black and green Insect creature " +
            "tokens with flying."
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature
                    .withAnyOfSubtypes(listOf(Subtype.INSECT, Subtype.SPIDER))
                    .youControl()
            )
        )
        condition = Conditions.Delirium()
    }

    staticAbility {
        ability = GrantKeyword(
            Keyword.DEATHTOUCH,
            GroupFilter(
                GameObjectFilter.Creature
                    .withAnyOfSubtypes(listOf(Subtype.INSECT, Subtype.SPIDER))
                    .youControl()
            )
        )
        condition = Conditions.Delirium()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "236"
        artist = "Helge C. Balzer"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcf3b17b-f2f6-4702-864d-8c96100b0563.jpg?1726286750"
    }
}
