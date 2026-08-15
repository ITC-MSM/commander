package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fear of Infinity
 * {1}{U}{B}
 * Enchantment Creature — Nightmare
 * 2/2
 * Flying, lifelink
 * This creature can't block.
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, you may
 * return this card from your graveyard to your hand.
 *
 * The Eerie ability triggers from the graveyard (CR 603.10a — a triggered ability that returns the
 * card from a graveyard functions there). Both halves of the Eerie keyword (an enchantment you
 * control entering, and fully unlocking a Room) are modeled as two `triggerZone = GRAVEYARD`
 * triggered abilities, each a [MayEffect] ("you may") wrapping [Effects.Move] of [EffectTarget.Self]
 * to hand.
 */
val FearOfInfinity = card("Fear of Infinity") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Enchantment Creature — Nightmare"
    power = 2
    toughness = 2
    oracleText = "Flying, lifelink\nThis creature can't block.\n" +
        "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, " +
        "you may return this card from your graveyard to your hand."

    keywords(Keyword.FLYING, Keyword.LIFELINK, Keyword.EERIE)

    staticAbility {
        ability = CantBlock()
    }

    // Eerie trigger — part 1: whenever an enchantment you control enters (functions from graveyard).
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        triggerZone = Zone.GRAVEYARD
        effect = MayEffect(Effects.Move(target = EffectTarget.Self, destination = Zone.HAND))
        description = "Eerie — Whenever an enchantment you control enters, you may return this card " +
            "from your graveyard to your hand."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room (functions from graveyard).
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        triggerZone = Zone.GRAVEYARD
        effect = MayEffect(Effects.Move(target = EffectTarget.Self, destination = Zone.HAND))
        description = "Eerie — Whenever you fully unlock a Room, you may return this card from your " +
            "graveyard to your hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Fernando Falcone"
        flavorText = "There are some nightmares so terrible, you're never truly rid of them."
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81756844-c642-406f-842d-35c1e404fec0.jpg?1726286668"
    }
}
