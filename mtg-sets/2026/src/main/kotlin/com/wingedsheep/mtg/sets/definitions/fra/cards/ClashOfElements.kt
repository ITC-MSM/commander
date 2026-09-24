package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Clash of Elements
 * {1}{U}{R}
 * Instant
 *
 * Choose target nonland permanent. Its owner may put it on the top of their library. If they do,
 * Clash of Elements deals 2 damage to them. If they didn't put the card on top of their library,
 * they put it on the bottom.
 *
 * A `MayEffect` whose `decisionMaker` is the target's **owner** (not the caster): "yes" puts it on
 * top and then deals 2 damage to that owner; "no" (`otherwise`) puts it on the bottom.
 */
val ClashOfElements = card("Clash of Elements") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Choose target nonland permanent. Its owner may put it on the top of their library. " +
        "If they do, Clash of Elements deals 2 damage to them. If they didn't put the card on top of " +
        "their library, they put it on the bottom."

    spell {
        val permanent = target("target nonland permanent", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        val owner = EffectTarget.PlayerRef(Player.OwnerOf("target nonland permanent"))
        effect = MayEffect(
            effect = Effects.Composite(
                Effects.PutOnTopOfLibrary(permanent),
                Effects.DealDamage(2, owner),
            ),
            descriptionOverride = "Put it on the top of your library? If you do, Clash of Elements " +
                "deals 2 damage to you. Otherwise it goes on the bottom of your library.",
            decisionMaker = owner,
            otherwise = Effects.PutOnBottomOfLibrary(permanent),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Anna Pavleeva"
        flavorText = "\"More fire? Is that your solution to everything?\" one Chandra asked. " +
            "\"Works pretty well, doesn't it?\" the other replied."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b61bcef7-5832-45e6-a2bc-26d4f23707fc.jpg?1788878210"
        inBooster = false
    }
}
