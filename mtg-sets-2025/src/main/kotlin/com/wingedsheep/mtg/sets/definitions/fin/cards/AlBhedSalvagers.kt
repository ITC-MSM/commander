package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Al Bhed Salvagers
 * {2}{B}
 * Creature — Human Artificer Warrior
 * 2/3
 * Whenever this creature or another creature or artifact you control dies, target
 * opponent loses 1 life and you gain 1 life.
 *
 * Modeling: an ANY-binding leaves-to-graveyard trigger filtered to creatures/artifacts
 * you control — the filter matches the source itself, so "this creature or another"
 * is covered without an explicit self clause (CR 603.2 / 700.4).
 */
val AlBhedSalvagers = card("Al Bhed Salvagers") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Artificer Warrior"
    power = 2
    toughness = 3
    oracleText = "Whenever this creature or another creature or artifact you control dies, " +
        "target opponent loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.CreatureOrArtifact.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            listOf(
                Effects.LoseLife(1, opponent),
                Effects.GainLife(1)
            )
        )
        description = "Whenever this creature or another creature or artifact you control dies, " +
            "target opponent loses 1 life and you gain 1 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "Masateru Ikeda"
        flavorText = "\"We found the airship! The records were right.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58ccdcfc-a669-480f-bded-4273cfaf2045.jpg?1748706093"
    }
}
