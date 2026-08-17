package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Alquist Proft, Master Sleuth — Murders at Karlov Manor #185
 * {1}{W}{U} · Legendary Creature — Human Detective · 3/3 · Mythic
 *
 * Vigilance
 * When Alquist Proft enters, investigate.
 * {X}{W}{U}{U}, {T}, Sacrifice a Clue: You draw X cards and gain X life.
 *
 * The enters trigger is the set's plain [Effects.Investigate] — one Clue, which is also the first
 * payment for the activated ability, so the card arrives holding its own fuel.
 *
 * Two details in the activated ability are worth stating, because both are places a card like this
 * is usually modelled wrong:
 *
 * - **The Clue is a cost, not a target.** `Sacrifice a Clue` is filtered on the *artifact subtype*
 *   ([GameObjectFilter.Artifact] `.withSubtype("Clue")`), never on tokenness — Clue is an artifact
 *   type like any other (2024-02-02 ruling), so a printed Clue artifact such as Wrench pays it just
 *   as well as an investigated token. Because it is a cost, the sacrifice is spent: the same Clue
 *   can't also pay for its own "{2}, Sacrifice this token: Draw a card", and any
 *   `Triggers.YouSacrificeA(Clue)` payoff on the board sees it.
 * - **X is one value read twice.** `{X}` is announced when the ability is activated (CR 601.2b via
 *   the activation path), and both halves of the payoff read the same
 *   [DynamicAmount.XValue] — so the draw and the lifegain can never disagree, which a pair of
 *   independently-evaluated amounts could.
 *
 * The composition needs nothing new: [Costs.Composite] already stacks mana + tap + sacrifice, and
 * the payoff is a two-element [Effects.Composite]. Ballista Squad established the same
 * `{X}`-in-an-activated-cost shape.
 */
val AlquistProftMasterSleuth = card("Alquist Proft, Master Sleuth") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Detective"
    power = 3
    toughness = 3
    oracleText = "Vigilance\n" +
        "When Alquist Proft enters, investigate. (Create a Clue token. It's an artifact with " +
        "\"{2}, Sacrifice this token: Draw a card.\")\n" +
        "{X}{W}{U}{U}, {T}, Sacrifice a Clue: You draw X cards and gain X life."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Investigate()
        description = "When Alquist Proft enters, investigate."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{X}{W}{U}{U}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Artifact.withSubtype("Clue"))
        )
        effect = Effects.Composite(
            Effects.DrawCards(DynamicAmount.XValue),
            Effects.GainLife(DynamicAmount.XValue)
        )
        description = "{X}{W}{U}{U}, {T}, Sacrifice a Clue: You draw X cards and gain X life."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "185"
        artist = "Andreas Zafiratos"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41129b44-4fa7-473b-b2b7-48c6a58be03c.jpg?1783912857"

        ruling(
            "2024-02-02",
            "Clue is an artifact type. Even though it appears on some cards with other permanent " +
                "types, it's never a creature type, a land type, or anything but an artifact type."
        )
        ruling(
            "2024-02-02",
            "If an effect refers to a Clue, it means any Clue artifact, not just a Clue artifact " +
                "token. For example, you can sacrifice Wrench to pay for Alquist Proft, Master " +
                "Sleuth's activated ability."
        )
        ruling(
            "2024-02-02",
            "You can't sacrifice a Clue to pay multiple costs. For example, you can't sacrifice a " +
                "Clue token to activate its own ability and also to activate Alquist Proft, " +
                "Master Sleuth's ability."
        )
    }
}
