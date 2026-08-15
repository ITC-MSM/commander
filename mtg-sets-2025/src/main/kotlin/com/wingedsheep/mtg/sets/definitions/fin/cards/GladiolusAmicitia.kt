package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Gladiolus Amicitia
 * {4}{R}{G}
 * Legendary Creature — Human Warrior
 * 6/6
 * When Gladiolus Amicitia enters, search your library for a land card, put it onto the battlefield tapped, then shuffle.
 * Landfall — Whenever a land you control enters, another target creature you control gets +2/+2 and gains trample until end of turn.
 */
val GladiolusAmicitia = card("Gladiolus Amicitia") {
    manaCost = "{4}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Human Warrior"
    oracleText = "When Gladiolus Amicitia enters, search your library for a land card, put it onto the battlefield tapped, then shuffle.\nLandfall — Whenever a land you control enters, another target creature you control gets +2/+2 and gains trample until end of turn."
    power = 6
    toughness = 6
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Land,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true
        )
    }
    triggeredAbility {
        trigger = Triggers.entersBattlefield(filter = GameObjectFilter.Land.youControl(), binding = TriggerBinding.ANY)
        val t = target("target", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t)
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Gal Or"
        flavorText = "\"Guard the king with our lives—that's the way it's always been.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/4/442957fc-045d-4db6-b82a-445f172d23e4.jpg"
    }
}
