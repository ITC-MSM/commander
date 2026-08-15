package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility

/**
 * Cathar's Call
 * {2}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has vigilance and "At the beginning of your end step, create a 1/1 white
 * Human creature token."
 *
 * The vigilance grant is a plain [GrantKeyword] over the enchanted creature. The quoted ability is
 * granted TO the enchanted creature via [GrantTriggeredAbility] (default attached-creature filter),
 * so "your end step" and the token's controller resolve against the enchanted creature's
 * controller (correct even when enchanting an opponent's creature) — the Combat Research shape.
 */
val CatharsCall = card("Cathar's Call") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has vigilance and \"At the beginning of your end step, create a 1/1 " +
        "white Human creature token.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            TriggeredAbility.create(
                trigger = Triggers.YourEndStep.event,
                binding = Triggers.YourEndStep.binding,
                effect = Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Human")
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "11"
        artist = "Matt Stewart"
        flavorText = "Some cathars succumbed to despair at Avacyn's unmaking. The rest redoubled their " +
            "efforts to carry out her work."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10d044a9-0149-4302-86e5-90623e54e36b.jpg?1782703729"
    }
}
