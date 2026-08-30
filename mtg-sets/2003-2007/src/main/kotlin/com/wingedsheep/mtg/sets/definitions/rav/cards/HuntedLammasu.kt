package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hunted Lammasu — Ravnica: City of Guilds #22
 * {2}{W}{W} · Creature — Lammasu · 5/5
 *
 * Flying
 * When this creature enters, target opponent creates a 4/4 black Horror creature token.
 *
 * The white member of the Hunted cycle: an over-statted body paid for by handing the defender a
 * blocker. The Horror enters under the *targeted opponent's* control, which is what
 * [Effects.CreateToken]'s `controller` parameter expresses — the trigger's controller creates
 * nothing (Hunted Bonebrute takes the same route). If the targeted opponent has become an illegal
 * target by resolution the whole trigger is removed from the stack and no Horror appears, leaving
 * the Lammasu's drawback unpaid.
 */
val HuntedLammasu = card("Hunted Lammasu") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Lammasu"
    oracleText = "Flying\n" +
        "When this creature enters, target opponent creates a 4/4 black Horror creature token."
    power = 5
    toughness = 5

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Horror"),
            controller = opponent,
        )
        description = "When this creature enters, target opponent creates a 4/4 black Horror " +
            "creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Mark Zug"
        flavorText = "The lammasu ruled the velds before the city grew. Now they roam Ravnica's " +
            "skies, but their ancient enemies have not forgotten them."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b84de052-65e5-4723-a746-5c554fa1612d.jpg?1783943698"
    }
}
