package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Shivan Meteor
 * {3}{R}{R}
 * Sorcery
 * Shivan Meteor deals 13 damage to target creature.
 * Suspend 2—{1}{R}{R}
 */
val ShivanMeteor = card("Shivan Meteor") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Shivan Meteor deals 13 damage to target creature.\n" +
        "Suspend 2—{1}{R}{R} (Rather than cast this card from your hand, you may pay {1}{R}{R} and exile it with two time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)"

    keywordAbility(KeywordAbility.suspend("{1}{R}{R}", 2))

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(13, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "106"
        artist = "Chippy"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/108599c3-f641-48f1-af68-8e9a66c6afbc.jpg"
    }
}
