package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Delney, Streetwise Lookout
 * {2}{W}
 * Legendary Creature — Human Scout
 * 2/2
 *
 * Creatures you control with power 2 or less can't be blocked by creatures with power 3 or greater.
 * If a triggered ability of a creature you control with power 2 or less triggers, that ability
 * triggers an additional time.
 */
val DelneyStreetwiseLookout = card("Delney, Streetwise Lookout") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Scout"
    power = 2
    toughness = 2
    oracleText = "Creatures you control with power 2 or less can't be blocked by creatures with " +
        "power 3 or greater.\n" +
        "If a triggered ability of a creature you control with power 2 or less triggers, that " +
        "ability triggers an additional time."

    staticAbility {
        ability = CantBeBlockedBy(
            blockerFilter = GameObjectFilter.Creature.powerAtLeast(3),
            filter = GroupFilter.AllCreaturesYouControl.powerAtMost(2),
        )
    }

    staticAbility {
        ability = AdditionalSourceTriggers(
            sourceFilter = GameObjectFilter.Creature.powerAtMost(2),
            excludeSelf = false,
            description = "If a triggered ability of a creature you control with power 2 or less " +
                "triggers, that ability triggers an additional time",
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "12"
        artist = "Darren Tan"
        flavorText = "\"It's not a matter of what I know. It's a matter of making it worth my " +
            "while to tell you.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/e/" +
            "be219928-3d0e-4d00-b124-152ce8a8c13b.jpg?1783912925"

        ruling("2024-02-02", "Once a creature you control has become blocked, reducing its power " +
            "to 2 or less and/or increasing the power of the creature blocking it to 3 or greater " +
            "won't cause it to become unblocked.")
        ruling("2024-02-02", "Replacement effects and abilities that apply as a creature enters " +
            "the battlefield or is turned face up are unaffected by Delney's last ability.")
        ruling("2024-02-02", "Each additional instance of a triggered ability has its choices, " +
            "including modes and targets, made separately.")
        ruling("2024-02-02", "Multiple Delneys are additive: two cause qualifying abilities to " +
            "trigger three times, three cause them to trigger four times, and so on.")
        ruling("2024-02-02", "A face-up trigger is doubled only if that creature's power is 2 or " +
            "less after it has been turned face up.")
        ruling("2024-02-02", "Whether Delney adds a trigger is determined when the ability " +
            "triggers; later power changes don't add or remove the additional instance.")
    }
}
