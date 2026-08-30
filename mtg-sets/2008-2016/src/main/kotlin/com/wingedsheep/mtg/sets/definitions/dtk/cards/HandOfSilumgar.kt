package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hand of Silumgar
 * {1}{B}
 * Creature — Human Warrior
 * 2 / 1
 *
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 *
 * One evergreen keyword; the damage rule is applied by the engine's damage step, so the bare
 * `Keyword.DEATHTOUCH` is the whole card.
 */
val HandOfSilumgar = card("Hand of Silumgar") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 1
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Lius Lasahido"
        flavorText = "Silumgar trains those whom he favors in his magic, granting them the ability to spread his disdain across the land."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/884cbc26-f164-47bd-878c-dd46652465d0.jpg?1783938597"
    }
}
