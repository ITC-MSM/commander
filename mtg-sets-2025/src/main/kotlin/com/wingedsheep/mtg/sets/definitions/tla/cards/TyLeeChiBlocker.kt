package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect

/**
 * Ty Lee, Chi Blocker
 * {2}{U}
 * Legendary Creature — Human Performer Ally
 * Flash
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * When Ty Lee enters, tap up to one target creature. It doesn't untap during its controller's
 * untap step for as long as you control Ty Lee.
 * 2/1
 */
val TyLeeChiBlocker = card("Ty Lee, Chi Blocker") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Performer Ally"
    power = 2
    toughness = 1
    oracleText = "Flash\n" +
        "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "When Ty Lee enters, tap up to one target creature. It doesn't untap during its " +
        "controller's untap step for as long as you control Ty Lee."

    keywords(Keyword.FLASH)
    prowess()

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.UpToCreatures(1))
        effect = Effects.Tap(creature) then
            GrantKeywordEffect(
                AbilityFlag.DOESNT_UNTAP.name,
                creature,
                Duration.WhileYouControlSource("Ty Lee"),
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "76"
        artist = "Gemi"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/308cc687-9cb2-4e3a-98db-c5ba2a7da115.jpg?1764120501"
    }
}
