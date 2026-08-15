package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shoot the Sheriff {1}{B}
 * Instant
 *
 * Destroy target non-outlaw creature.
 * (Assassins, Mercenaries, Pirates, Rogues, and Warlocks are outlaws.
 *  Everyone else is fair game.)
 */
val ShootTheSheriff = card("Shoot the Sheriff") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target non-outlaw creature. (Assassins, Mercenaries, Pirates, Rogues, and Warlocks are outlaws. Everyone else is fair game.)"

    spell {
        val creature = target("non-outlaw creature", Targets.NonOutlawCreature)
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "106"
        artist = "Fariba Khamseh"
        flavorText = "\"Now, where is that deputy?\"\n—Jana, Hellspur thunderslinger"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/180d6528-c524-4bb8-8a72-b3775cd2c177.jpg?1712355672"

        ruling("2024-04-12", "A card, spell, or permanent is an outlaw if it has the Assassin, Mercenary, Pirate, Rogue, or Warlock creature type. It doesn't matter if it has more than one of those creature types; as long as it has at least one, it's an outlaw.")
        ruling("2024-04-12", "Outlaw is not a creature type. If an effect asks you to choose a creature type, you can't choose outlaw.")
    }
}
