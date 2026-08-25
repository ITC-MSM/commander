package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Archdemon of Unx
 * {5}{B}{B}
 * Creature — Demon
 * 6 / 6
 * Flying, trample
 * At the beginning of your upkeep, sacrifice a non-Zombie creature, then create a 2/2 black Zombie creature token.
 *
 * [Triggers.YourUpkeep] over a two-step composite. The printed "sacrifice a non-Zombie creature" is
 * the bare imperative — the ability's own controller sacrifices and no player is named — so it is
 * [Effects.SacrificeOwn], not the `Sacrifice` that names a player; the "non-Zombie" half is a
 * predicate on the filter (`Creature.notSubtype(ZOMBIE)`), which keeps the exclusion in the choice
 * the player is offered rather than in the effect. The `then` is plain sequencing, so the token is
 * created after the sacrifice resolves even when there was nothing to sacrifice.
 */
val ArchdemonOfUnx = card("Archdemon of Unx") {
    manaCost = "{5}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "Flying, trample\n" +
        "At the beginning of your upkeep, sacrifice a non-Zombie creature, then create a 2/2 black Zombie creature token."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature.notSubtype(Subtype.ZOMBIE)) then
            Effects.CreateToken(
                power = 2,
                toughness = 2,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Zombie"),
                imageUri = "https://cards.scryfall.io/normal/front/b/a/ba9b6612-5372-4e6f-841f-20009f71a736.jpg"
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "64"
        artist = "Dave Allsop"
        flavorText = "The necropolis at Unx was once a living city, its streets untrodden by death."
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b260fbe1-21cb-4a66-aca3-e504b67ed712.jpg"
    }
}
