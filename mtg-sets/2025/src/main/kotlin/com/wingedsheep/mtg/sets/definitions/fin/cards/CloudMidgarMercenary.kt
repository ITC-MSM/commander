package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Cloud, Midgar Mercenary
 * {W}{W}
 * Legendary Creature — Human Soldier Mercenary
 * 2/1
 * When Cloud enters, search your library for an Equipment card, reveal it, put it into your hand,
 * then shuffle.
 * As long as Cloud is equipped, if a triggered ability of Cloud or an Equipment attached to it
 * triggers, that ability triggers an additional time.
 *
 * The doubling reuses [AdditionalSourceTriggers], extended for this card with:
 *  - `alsoSource = true` — Cloud's *own* triggered abilities are doubled ("a triggered ability of
 *    Cloud or …"), independently of the filter that catches its Equipment.
 *  - a `condition` gate — [Conditions.SourceMatches] over `equipped()` ("As long as Cloud is
 *    equipped, …"); when nothing is attached the whole ability is inert.
 * The filter `Artifact.withSubtype("Equipment").attachedToSource()` catches the triggers of any
 * Equipment currently attached to Cloud. Per the ruling this doesn't copy the ability — it makes it
 * trigger one extra time, with choices made independently for each instance.
 */
val CloudMidgarMercenary = card("Cloud, Midgar Mercenary") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier Mercenary"
    power = 2
    toughness = 1
    oracleText = "When Cloud enters, search your library for an Equipment card, reveal it, put it " +
        "into your hand, then shuffle.\n" +
        "As long as Cloud is equipped, if a triggered ability of Cloud or an Equipment attached " +
        "to it triggers, that ability triggers an additional time."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Artifact.withSubtype("Equipment"),
            destination = SearchDestination.HAND,
            shuffleAfter = true,
            reveal = true
        )
    }

    staticAbility {
        ability = AdditionalSourceTriggers(
            sourceFilter = GameObjectFilter.Artifact.withSubtype("Equipment").attachedToSource(),
            excludeSelf = true,
            alsoSource = true,
            condition = Conditions.SourceMatches(GameObjectFilter.Any.equipped()),
            description = "As long as Cloud is equipped, if a triggered ability of Cloud or an " +
                "Equipment attached to it triggers, that ability triggers an additional time"
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "10"
        artist = "Kazto Furuya"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cf7e8a3-fad7-413d-b17c-7519a9cf5fb5.jpg?1782686592"
        ruling("2025-06-06", "Cloud's last ability doesn't copy the triggered ability; it just causes the ability to trigger an additional time. Any choices made as you put the ability onto the stack, such as modes and targets, are made separately for each instance of the ability. Any choices made on resolution are also made individually.")
    }
}
