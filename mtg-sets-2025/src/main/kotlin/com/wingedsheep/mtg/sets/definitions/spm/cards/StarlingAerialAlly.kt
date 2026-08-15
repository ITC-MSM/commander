package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Starling, Aerial Ally
 * {4}{W}
 * Legendary Creature — Human Hero
 * 3/4
 * Flying
 * When Starling enters, another target creature you control gains flying until end of turn.
 */
val StarlingAerialAlly = card("Starling, Aerial Ally") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Hero"
    oracleText = "Flying\nWhen Starling enters, another target creature you control gains flying until end of turn."
    power = 3
    toughness = 4
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Aniekan Udofia"
        flavorText = "\"Aaaah! Is this what it's like for people I swing around? I'm gonna throw up!\"\n—Spider-Man, Miles Morales"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/babbf53d-3e10-4110-8725-91f766c8cdad.jpg?1757376852"
    }
}
