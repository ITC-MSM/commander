package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ishgard, the Holy See // Faith & Grief
 * Land — Town // Sorcery — Adventure
 *
 * Ishgard, the Holy See:
 *   This land enters tapped.
 *   {T}: Add {W}.
 *
 * Faith & Grief — {3}{W}{W}, Sorcery — Adventure:
 *   Return up to two target artifact and/or enchantment cards from your graveyard to your hand.
 *   (Then exile this card. You may play the land later from exile.)
 *
 * Town land // spell Adventure (CR 715, modeled on the [com.wingedsheep.sdk.model.CardLayout.ADVENTURE]
 * layout): the land is the *primary* face. You either play the land or cast Faith & Grief; casting
 * the Adventure resolves its graveyard recursion, exiles the card, and grants permission to *play
 * the land* from exile later (CR 715.3d — the engine's generic may-play permission covers playing a
 * land, not just casting a creature).
 */
val IshgardTheHolySee = card("Ishgard, the Holy See") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Land — Town"
    oracleText = "This land enters tapped.\n{T}: Add {W}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    adventure("Faith & Grief") {
        manaCost = "{3}{W}{W}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Return up to two target artifact and/or enchantment cards from your graveyard " +
            "to your hand. (Then exile this card. You may play the land later from exile.)"
        spell {
            target = TargetObject(
                count = 2,
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.ArtifactOrEnchantment.ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
            effect = ForEachTargetEffect(
                effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND))
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "283"
        artist = "Kohei Yamada"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/068bc755-9d3d-430b-abc5-c775a5415bf9.jpg?1748706839"
    }
}
