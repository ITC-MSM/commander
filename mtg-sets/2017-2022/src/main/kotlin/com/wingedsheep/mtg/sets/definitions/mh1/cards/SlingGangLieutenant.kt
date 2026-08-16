package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sling-Gang Lieutenant
 * {3}{B}
 * Creature — Goblin
 * 1/1
 * When this creature enters, create two 1/1 red Goblin creature tokens.
 * Sacrifice a Goblin: Target player loses 1 life and you gain 1 life.
 *
 * The bare tribal noun "Goblin" in the sacrifice cost names every *permanent* with the subtype,
 * and [Costs.Sacrifice] doesn't exclude the source — so Sling-Gang Lieutenant can sacrifice
 * itself, as the ruling notes.
 */
val SlingGangLieutenant = card("Sling-Gang Lieutenant") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, create two 1/1 red Goblin creature tokens.\nSacrifice a Goblin: Target player loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            imageUri = "https://cards.scryfall.io/normal/front/7/0/70f8a1de-cd4c-4afa-bf03-0245d375d42e.jpg?1782727474",
        )
        description = "When this creature enters, create two 1/1 red Goblin creature tokens."
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype("Goblin"))
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.LoseLife(1, player),
            Effects.GainLife(1),
        )
        description = "Sacrifice a Goblin: Target player loses 1 life and you gain 1 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "Craig J Spearing"
        flavorText = "Freshly promoted to \"first rock,\" Zaz was eager to make an impact."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f82aec21-441b-4fcd-b666-61723bd79531.jpg?1783933120"
        ruling("2019-06-14", "You can sacrifice any Goblin you control to activate Sling-Gang Lieutenant's activated ability, not just the ones its triggered ability creates. You can even sacrifice Sling-Gang Lieutenant itself.")
    }
}
