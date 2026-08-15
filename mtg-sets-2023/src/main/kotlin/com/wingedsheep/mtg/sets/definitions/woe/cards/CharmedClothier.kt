package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Charmed Clothier
 * {4}{W}
 * Creature — Faerie Advisor
 * 3/3
 *
 * Flying
 * When this creature enters, create a Royal Role token attached to another target creature you
 * control. (If you control another Role on it, put that one into the graveyard. Enchanted creature
 * gets +1/+1 and has ward {1}.)
 *
 * "Another" excludes Charmed Clothier itself, hence [Targets.OtherCreatureYouControl]. The
 * one-Role-per-creature rule (CR 704.5s) lives in the Role token machinery behind
 * [Effects.CreateRoleToken], not here.
 */
val CharmedClothier = card("Charmed Clothier") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Faerie Advisor"
    oracleText = "Flying\n" +
        "When this creature enters, create a Royal Role token attached to another target creature " +
        "you control. (If you control another Role on it, put that one into the graveyard. " +
        "Enchanted creature gets +1/+1 and has ward {1}.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.OtherCreatureYouControl)
        effect = Effects.CreateRoleToken("Royal Role", t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/994f4473-dfc9-45cd-8528-945db3aa6a9a.jpg?1783915135"
    }
}
