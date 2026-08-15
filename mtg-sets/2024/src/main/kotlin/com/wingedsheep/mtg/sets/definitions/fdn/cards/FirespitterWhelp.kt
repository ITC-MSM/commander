package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Firespitter Whelp
 * {2}{R}
 * Creature — Dragon
 * 2/2
 * Flying
 * Whenever you cast a noncreature or Dragon spell, this creature deals 1 damage to each opponent.
 */
val FirespitterWhelp = card("Firespitter Whelp") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature or Dragon spell, this creature deals 1 damage to each opponent."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Noncreature or GameObjectFilter.Any.withSubtype(Subtype.DRAGON)
        )
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "761"
        artist = "David Álvarez"
        flavorText = "In subsequent terms, first-year students were prohibited from enrolling in Introduction to Dragonkeeping."
        imageUri = "https://cards.scryfall.io/normal/front/d/d/ddcc3c1b-b564-4444-9a1a-0f62f8e6b8bb.jpg?1782683456"
    }
}
