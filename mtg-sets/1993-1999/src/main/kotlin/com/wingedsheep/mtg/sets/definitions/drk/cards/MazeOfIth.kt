package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Maze of Ith
 * Land
 * {T}: Untap target attacking creature. Prevent all combat damage that would be dealt to and
 * dealt by that creature this turn.
 *
 * Untapping an attacker does not remove it from combat, which is why the second half matters:
 * the same creature is shielded in both directions ([Effects.PreventCombatDamageToAndBy]), so
 * it neither deals nor receives combat damage for the rest of the turn.
 */
val MazeOfIth = card("Maze of Ith") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Untap target attacking creature. Prevent all combat damage that would be " +
        "dealt to and dealt by that creature this turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.Composite(
            Effects.Untap(creature),
            Effects.PreventCombatDamageToAndBy(creature)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42dcceee-2a47-4eaa-a6a3-2931b3d50244.jpg?1783947922"

        ruling("2022-12-08", "Maze of Ith doesn't have a mana ability. It doesn't tap for colorless mana.")
        ruling(
            "2022-12-08",
            "Maze of Ith can target an untapped attacking creature. It will still prevent the combat damage it " +
                "would deal and be dealt."
        )
        ruling(
            "2022-12-08",
            "The creature isn't removed from combat; it just has its damage prevented. It's still an attacking " +
                "creature until the combat phase is complete."
        )
        ruling(
            "2022-12-08",
            "You can activate Maze of Ith's ability targeting an attacking creature you control during the combat " +
                "damage step or the end of combat step, even though it will already have dealt combat damage. This " +
                "will untap the creature. The damage it dealt will be unaffected."
        )
    }
}
