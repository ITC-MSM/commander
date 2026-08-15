package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Trapped in the Screen
 * {2}{W}
 * Enchantment
 * Ward {2}
 * When this enchantment enters, exile target artifact, creature, or enchantment an opponent
 * controls until this enchantment leaves the battlefield.
 *
 * An Oblivion Ring variant restricted to "artifact, creature, or enchantment an opponent
 * controls". Reuses the standard linked-exile pattern: [Effects.ExileUntilLeaves] on the enters
 * trigger links the exiled card to this permanent, and [Effects.ReturnLinkedExileUnderOwnersControl]
 * on the leaves trigger returns it (same wiring as Banishing Light). The new target filter
 * [TargetFilter.ArtifactCreatureOrEnchantmentOpponentControls] is the reusable three-type
 * opponent-controlled permanent restriction. Ward {2} is the standard [KeywordAbility.Ward].
 */
val TrappedInTheScreen = card("Trapped in the Screen") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Ward {2} (Whenever this enchantment becomes the target of a spell or ability an " +
        "opponent controls, counter it unless that player pays {2}.)\n" +
        "When this enchantment enters, exile target artifact, creature, or enchantment an opponent " +
        "controls until this enchantment leaves the battlefield."

    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "artifact, creature, or enchantment an opponent controls",
            TargetPermanent(filter = TargetFilter.ArtifactCreatureOrEnchantmentOpponentControls)
        )
        effect = Effects.ExileUntilLeaves(permanent)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Michael Phillippi"
        flavorText = "Maggie had always wanted to be on television."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1fe95bfb-8ca7-434f-a2e7-a6b2e699584e.jpg?1726285993"
    }
}
