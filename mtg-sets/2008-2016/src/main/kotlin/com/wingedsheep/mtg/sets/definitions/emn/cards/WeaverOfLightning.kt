package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Weaver of Lightning
 * {2}{R}
 * Creature — Human Shaman
 * 1/4
 *
 * Reach (This creature can block creatures with flying.)
 * Whenever you cast an instant or sorcery spell, this creature deals 1 damage to target creature an opponent controls.
 */
val WeaverOfLightning = card("Weaver of Lightning") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman"
    oracleText = "Reach (This creature can block creatures with flying.)\nWhenever you cast an instant or sorcery spell, this creature deals 1 damage to target creature an opponent controls."
    power = 1
    toughness = 4

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        val creature = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.DealDamage(1, creature)
        description = "Whenever you cast an instant or sorcery spell, this creature deals 1 damage to " +
            "target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "149"
        artist = "John Stanko"
        flavorText = "\"Lightning in a bottle? That's not where I need it.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba1e6885-dca0-4a4b-abb8-0f32680f618d.jpg?1783937450"
    }
}
