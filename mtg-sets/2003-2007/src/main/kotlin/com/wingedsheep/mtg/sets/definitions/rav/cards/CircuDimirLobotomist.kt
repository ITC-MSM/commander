package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PlayersCantCastSpells
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Circu, Dimir Lobotomist — Ravnica: City of Guilds #196
 * {2}{U}{B} · Legendary Creature — Human Wizard · Rare · 2/3
 *
 * Whenever you cast a blue spell, exile the top card of target player's library.
 * Whenever you cast a black spell, exile the top card of target player's library.
 * Your opponents can't cast spells with the same name as a card exiled with Circu.
 *
 * Modelling notes:
 * - **Two separate triggered abilities, not one with an "or" filter.** A blue *and* black spell
 *   fires both (the 2005-10-01 ruling), and each picks its own target — a filter reading
 *   "blue or black" would fire once and exile one card. Writing them as two abilities is what
 *   makes "you can target two different players or target the same player twice" fall out.
 * - The exile is a **linked** one (CR 607): `linkToSource = true` files each card against this
 *   permanent so the static below can read the whole pile. That is also why a replacement Circu
 *   does not inherit the old one's exiles.
 * - The prohibition is the reused [PlayersCantCastSpells] static scoped to `EachOpponent` — the
 *   card is not symmetric, per its own ruling that the last ability applies to all of its
 *   controller's opponents rather than to the owner of the exiled card.
 * - **New SDK vocabulary:** `CardPredicate.SharesNameWithLinkedExile`, reached here through
 *   `GameObjectFilter.Any.sharingNameWithLinkedExile()`. It is the *name* axis of the existing
 *   `SharesCardTypeWithLinkedExile` (Cemetery Illuminator) and, like it, has to be pile-wide:
 *   `sharingNameWith(EntityReference.LinkedExiledCard())` reads exactly one index, and Circu's
 *   pile grows on every blue and every black spell you cast, so no index names "a card exiled
 *   with Circu". Comparing printed names is correct on both sides — neither an exiled card nor a
 *   card in a hand or library has a battlefield projection a Layer-3 rename could have touched.
 * - Split cards: the ruling says exiling one half locks out both. Our `CardComponent.name` for a
 *   split card is the full "A // B" string, so a split card exiled this way blocks casting either
 *   half only insofar as the halves share that name — an approximation the corpus can't currently
 *   improve on, and one no RAV-era card exercises (no split cards are legal alongside it in
 *   Standard-era play).
 */
val CircuDimirLobotomist = card("Circu, Dimir Lobotomist") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Wizard"
    oracleText = "Whenever you cast a blue spell, exile the top card of target player's library.\n" +
        "Whenever you cast a black spell, exile the top card of target player's library.\n" +
        "Your opponents can't cast spells with the same name as a card exiled with Circu."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Any.withColor(Color.BLUE))
        target("target player", TargetPlayer())
        effect = exileTopOfTargetPlayersLibrary("circuBlueExile")
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Any.withColor(Color.BLACK))
        target("target player", TargetPlayer())
        effect = exileTopOfTargetPlayersLibrary("circuBlackExile")
    }

    staticAbility {
        ability = PlayersCantCastSpells(
            affected = Player.EachOpponent,
            spellFilter = GameObjectFilter.Any.sharingNameWithLinkedExile()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "196"
        artist = "Cyril Van Der Haegen"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b19c0f3-68a5-4544-985a-677aa2c2b50b.jpg?1783943626"

        ruling(
            "2005-10-01",
            "If you cast a blue and black spell, both of Circu's triggered abilities trigger. " +
                "You can target two different players or target the same player twice."
        )
        ruling(
            "2005-10-01",
            "Circu's last ability applies to all of its controller's opponents, not just the " +
                "owner of the exiled card."
        )
        ruling(
            "2005-10-01",
            "If a split card is exiled this way, opponents can't cast either half of the split card."
        )
    }
}

/**
 * "Exile the top card of target player's library", filed against Circu so the cast prohibition can
 * read it. A gather → move pair rather than a bespoke effect; an empty library gathers nothing and
 * the move is a no-op, which is the card's own behaviour.
 */
private fun exileTopOfTargetPlayersLibrary(slot: String) = Effects.Composite(
    GatherCardsEffect(
        source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1), Player.TargetPlayer),
        storeAs = slot
    ),
    MoveCollectionEffect(
        from = slot,
        destination = CardDestination.ToZone(Zone.EXILE),
        linkToSource = true
    )
)
