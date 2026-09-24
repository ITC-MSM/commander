package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.CantBeBlockedByFewerThan
import com.wingedsheep.sdk.scripting.ExileCounteredSpellInstead
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Guile — Lorwyn #69
 *
 * The counter replacement is [ExileCounteredSpellInstead]: the spell is exiled instead of
 * countered (so it was never countered), and Guile's controller may cast it right away, without
 * paying its mana cost, before the countering spell or ability goes on resolving. Declining leaves
 * the card in exile for good. A spell that can't be countered isn't, so Guile never sees it.
 */
val Guile = card("Guile") {
    manaCost = "{3}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Incarnation"
    power = 6
    toughness = 6
    oracleText = "This creature can't be blocked except by three or more creatures.\nIf a spell or ability you control would counter a spell, instead exile that spell and you may play that card without paying its mana cost.\nWhen Guile is put into a graveyard from anywhere, shuffle it into its owner's library."

    staticAbility {
        ability = CantBeBlockedByFewerThan(3)
    }

    replacementEffect(
        ExileCounteredSpellInstead(
            then = MayEffect(
                Effects.CastFromCollectionWithoutPayingCost(ExileCounteredSpellInstead.EXILED_CARD),
                descriptionOverride = "You may play that card without paying its mana cost"
            )
        )
    )

    triggeredAbility {
        triggerZone = Zone.GRAVEYARD
        trigger = TriggerSpec(
            event = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            binding = TriggerBinding.SELF
        )
        // Shuffle even if the card has left the graveyard before this resolves.
        effect = Effects.Move(EffectTarget.Self, Zone.LIBRARY, fromZone = Zone.GRAVEYARD) then
            ShuffleLibraryEffect()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "69"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2eec6ab7-afbe-46ea-a7a6-6b678256f33e.jpg?1783942902"
        ruling("2007-10-01", "Guile's second ability replaces \"counter [a certain spell]\" with \"exile [a certain spell] and you may cast it without paying its mana cost.\" You have the option to cast it immediately upon its exile. If you choose not to, it remains exiled and you don't get another chance to cast it. If the spell or ability that tried to counter the spell has additional effects, it then continues to resolve.")
        ruling("2007-10-01", "Exiling the spell is mandatory. Casting it is not.")
        ruling("2007-10-01", "A spell exiled this way was never actually countered.")
        ruling("2007-10-01", "If a spell or ability you control attempts to counter a spell that can't be countered, it doesn't. Since the spell wouldn't be countered, Guile's ability has no effect on it. The spell will continue to resolve normally.")
        ruling("2007-10-01", "The last ability triggers when the Incarnation is put into its owner’s graveyard from any zone, not just from on the battlefield.")
        ruling("2007-10-01", "Although this ability triggers when the Incarnation is put into a graveyard from the battlefield, it doesn’t *specifically* trigger on leaving the battlefield, so it doesn’t behave like other leaves-the-battlefield abilities. The ability will trigger from the graveyard.")
        ruling("2007-10-01", "If the Incarnation had lost this ability while on the battlefield (due to Lignify, for example) and then was destroyed, the ability would still trigger and it would get shuffled into its owner’s library. However, if the Incarnation lost this ability when it was put into the graveyard (due to Yixlid Jailer, for example), the ability wouldn’t trigger and the Incarnation would remain in the graveyard.")
        ruling("2007-10-01", "If the Incarnation is removed from the graveyard after the ability triggers but before it resolves, it will remain in its new zone when its owner shuffles their library. Similarly, if a replacement effect has the Incarnation move to a different zone instead of being put into the graveyard, the ability won’t trigger at all.")
    }
}
