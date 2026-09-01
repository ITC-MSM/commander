package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Littjara Glade-Warden
 * {3}{G}
 * Creature — Shapeshifter
 * 3/3
 * Changeling (This card is every creature type.)
 * {2}{G}, {T}, Exile a creature card from your graveyard: Put two +1/+1 counters on target creature. Activate only as a sorcery.
 *
 * A repeatable counter engine gated on graveyard fuel. The exile is part of the cost, so it happens
 * on activation and the ability cannot be activated at all with an empty graveyard; "Activate only as
 * a sorcery" is [TimingRule.SorcerySpeed]. Changeling makes the Warden itself a legal Elf for every
 * tribal payoff in the set.
 */
val LittjaraGladeWarden = card("Littjara Glade-Warden") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "{2}{G}, {T}, Exile a creature card from your graveyard: Put two +1/+1 counters on target creature. Activate only as a sorcery."
    power = 3
    toughness = 3

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{G}"),
            Costs.Tap,
            Costs.ExileFromGraveyard(1, GameObjectFilter.Creature)
        )
        val recipient = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, recipient)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "182"
        artist = "Deruchenko Alexander"
        flavorText = "In every tree, a restless spirit."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a92dde51-310e-4f28-bd3b-d43b639785ec.jpg"
    }
}
