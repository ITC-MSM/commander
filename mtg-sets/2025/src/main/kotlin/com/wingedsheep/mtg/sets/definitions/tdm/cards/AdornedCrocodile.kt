package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Adorned Crocodile
 * {4}{B}
 * Creature — Crocodile
 * 5/3
 *
 * When this creature dies, create a 2/2 black Zombie Druid creature token.
 * Renew — {B}, Exile this card from your graveyard: Put a +1/+1 counter on target
 *   creature. Activate only as a sorcery.
 */
val AdornedCrocodile = card("Adorned Crocodile") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Crocodile"
    power = 5
    toughness = 3
    oracleText = "When this creature dies, create a 2/2 black Zombie Druid creature token.\n" +
        "Renew — {B}, Exile this card from your graveyard: Put a +1/+1 counter on target creature. " +
        "Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie", "Druid"),
            imageUri = "https://cards.scryfall.io/normal/front/f/1/f10d5813-7818-43e8-b08d-4ed8c54d0366.jpg?1748452772"
        )
        description = "When this creature dies, create a 2/2 black Zombie Druid creature token."
    }

    renew("{B}") {
        effect = Effects.AddCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            1,
            target("creature", Targets.Creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Nathaniel Himawan"
        flavorText = "\"Eat you? Of course she won't. Pherah's got better taste than that.\"\n—Visk, Qarsi Palace caretaker"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb13a34b-6ac8-47cb-9e91-47106a585fc1.jpg?1743697510"
        ruling("2025-04-04", "If a card with a renew ability is put into your graveyard during your turn, you can activate that ability if it's legal to do so before any other player can take any actions.")
    }
}
