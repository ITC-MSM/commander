package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.exploit
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Repository Skaab
 * {3}{U}
 * Creature — Zombie
 * 3/3
 * Exploit (When this creature enters, you may sacrifice a creature.)
 * When this creature exploits a creature, return target instant or sorcery card from your
 * graveyard to your hand.
 *
 * The payoff targets a card in your graveyard ([exploit]'s `onExploitTargets` =
 * [Targets.InstantOrSorceryInYourGraveyard]), chosen after the sacrifice resolves;
 * [Effects.ReturnToHand] moves it to your hand. The reflexive is only offered when a legal
 * instant/sorcery card is in your graveyard, so exploiting with none just performs the sacrifice.
 */
val RepositorySkaab = card("Repository Skaab") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 3
    oracleText = "Exploit (When this creature enters, you may sacrifice a creature.)\n" +
        "When this creature exploits a creature, return target instant or sorcery card from your " +
        "graveyard to your hand."

    exploit(
        onExploit = Effects.ReturnToHand(EffectTarget.ContextTarget(0)),
        onExploitTargets = listOf(Targets.InstantOrSorceryInYourGraveyard)
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Olivier Bernard"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7cc22c2a-535a-46b5-817c-da5850abd669.jpg?1783924886"
    }
}
