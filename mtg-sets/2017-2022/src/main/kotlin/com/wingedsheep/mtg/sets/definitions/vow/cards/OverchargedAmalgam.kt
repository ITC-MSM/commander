package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.exploit
import com.wingedsheep.sdk.model.Rarity

/**
 * Overcharged Amalgam
 * {2}{U}{U}
 * Creature — Zombie Horror
 * 3/3
 * Flash
 * Flying
 * Exploit (When this creature enters, you may sacrifice a creature.)
 * When this creature exploits a creature, counter target spell, activated ability, or triggered
 * ability.
 *
 * Flash lets you cast it in response, and the exploit reflexive then lets you counter something —
 * the classic "flash exploit counterspell on a stick". The payoff targets a stack object
 * ([exploit]'s `onExploitTargets` = [Targets.SpellOrAbility]), chosen after the sacrifice resolves;
 * [Effects.CounterSpellOrAbility] dispatches by what's actually on the stack. The reflexive is only
 * offered when a legal spell/ability is on the stack, so exploiting with nothing to counter just
 * performs the sacrifice.
 */
val OverchargedAmalgam = card("Overcharged Amalgam") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Horror"
    power = 3
    toughness = 3
    oracleText = "Flash\n" +
        "Flying\n" +
        "Exploit (When this creature enters, you may sacrifice a creature.)\n" +
        "When this creature exploits a creature, counter target spell, activated ability, or " +
        "triggered ability."

    keywords(Keyword.FLASH, Keyword.FLYING)

    exploit(
        onExploit = Effects.CounterSpellOrAbility(),
        onExploitTargets = listOf(Targets.SpellOrAbility)
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Mike Jordana"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbbcd63d-6c25-47a6-a76c-ac53bf12949c.jpg?1783924890"
    }
}
