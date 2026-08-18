package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ulamog, the Ceaseless Hunger — Battle for Zendikar #15
 * {10} · Legendary Creature — Eldrazi · 10/10 · Mythic
 *
 * When you cast this spell, exile two target permanents.
 * Indestructible
 * Whenever Ulamog attacks, defending player exiles the top twenty cards of their library.
 *
 * Modeling notes:
 *  - The exile is a **cast trigger** ([Triggers.WhenYouCastThisSpell]), not an ETB — per the
 *    2015-08-25 ruling it resolves independently of Ulamog and before Ulamog itself, and it
 *    still resolves even if Ulamog is countered (see [com.wingedsheep.mtg.sets.definitions.roe.cards.ArtisanOfKozilek]
 *    for the same pattern on a smaller Eldrazi titan). Two simultaneous targets use
 *    [TargetPermanent] with `count = 2` plus [ForEachTargetEffect] so each is exiled
 *    independently even if one becomes illegal.
 *  - The attack trigger is the lowering of the (unprinted, purely descriptive here) "mill 20"
 *    effect via [Patterns.Library.exileTop], targeting [Player.DefendingPlayer] — same shape as
 *    Malboro's "exile the top N cards of a player's library." Per the 2015-08-25 ruling, if the
 *    defending player has fewer than twenty cards left, all of them are exiled and the empty
 *    library alone doesn't cause a loss (only a forced draw from it would).
 */
val Ulamog = card("Ulamog, the Ceaseless Hunger") {
    manaCost = "{10}"
    colorIdentity = ""
    typeLine = "Legendary Creature — Eldrazi"
    power = 10
    toughness = 10
    oracleText = "When you cast this spell, exile two target permanents.\n" +
        "Indestructible\n" +
        "Whenever Ulamog attacks, defending player exiles the top twenty cards of their library."

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        val targets = target("target permanents", TargetPermanent(count = 2))
        effect = ForEachTargetEffect(
            listOf(Effects.Exile(EffectTarget.ContextTarget(0)))
        )
        description = "When you cast this spell, exile two target permanents."
    }

    keywords(Keyword.INDESTRUCTIBLE)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Library.exileTop(20, EffectTarget.PlayerRef(Player.DefendingPlayer))
        description = "Whenever Ulamog attacks, defending player exiles the top twenty cards of their library."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "15"
        artist = "Michael Komarck"
        flavorText = "A force as voracious as time itself."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/1192f7a9-102e-4b3a-b154-18c8eb332217.jpg?1783938222"
        ruling(
            "2015-08-25",
            "Ulamog's first ability resolves independently of Ulamog once they've both been put " +
                "on the stack. If Ulamog is countered, that triggered ability will still resolve. " +
                "That triggered ability will always resolve before Ulamog does."
        )
        ruling(
            "2015-08-25",
            "If the player has less than twenty cards in their library, exile all of them. That " +
                "player won't lose the game until they have to draw a card from an empty library."
        )
    }
}
