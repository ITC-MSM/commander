package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.ReturnFace
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Joshua, Phoenix's Dominant // Phoenix, Warden of Fire — Final Fantasy #229
 * {1}{R}{W} · Legendary Creature — Human Noble Wizard 3/4
 * // Legendary Enchantment Creature — Saga Phoenix 4/4
 *
 * Front — Joshua, Phoenix's Dominant:
 *   When Joshua enters, discard up to two cards, then draw that many cards.
 *   {3}{R}{W}, {T}: Exile Joshua, then return it to the battlefield transformed under its
 *   owner's control. Activate only as a sorcery.
 *
 * Back — Phoenix, Warden of Fire (eikon Saga):
 *   (As this Saga enters and after your draw step, add a lore counter.)
 *   I, II — Rising Flames — Phoenix deals 2 damage to each opponent.
 *   III — Flames of Rebirth — Return any number of target creature cards with total mana
 *   value 6 or less from your graveyard to the battlefield. Exile Phoenix, then return it to
 *   the battlefield (front face up).
 *   Flying, lifelink
 *
 * The ETB loot is the Sokka idiom: gather hand → choose up to two to discard → draw the
 * discarded `_count` (declining discards draws zero, per the 2025-06-06 ruling). The Dominant
 * transform loop follows Dion, Bahamut's Dominant — an [Effects.ExileAndReturnTransformed]
 * on the front's sorcery-speed activated ability and again (with [ReturnFace.FRONT]) at the
 * eikon's final chapter. Note the Saga has no "Sacrifice after III" clause: chapter III
 * flips it back to the front face rather than sacrificing it. Flames of Rebirth is the
 * Michelangelo's Technique idiom — gather creature cards from the graveyard, choose any
 * number capped at total mana value 6 ([SelectionRestriction.TotalManaValueAtMost]), and move
 * the chosen ones to the battlefield; unchosen cards stay in the graveyard. Rising Flames'
 * damage feeds the back face's lifelink automatically.
 */
private val PhoenixWardenOfFire = card("Phoenix, Warden of Fire") {
    manaCost = ""
    colorIdentity = "RW"
    typeLine = "Legendary Enchantment Creature — Saga Phoenix"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter.)\n" +
        "I, II — Rising Flames — Phoenix deals 2 damage to each opponent.\n" +
        "III — Flames of Rebirth — Return any number of target creature cards with total mana " +
        "value 6 or less from your graveyard to the battlefield. Exile Phoenix, then return it " +
        "to the battlefield (front face up).\n" +
        "Flying, lifelink"
    power = 4
    toughness = 4

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    // I, II — Rising Flames — Phoenix deals 2 damage to each opponent.
    val risingFlames = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
    sagaChapter(1) {
        effect = risingFlames
    }
    sagaChapter(2) {
        effect = risingFlames
    }

    // III — Flames of Rebirth — Return any number of target creature cards with total mana
    // value 6 or less from your graveyard to the battlefield. Exile Phoenix, then return it
    // to the battlefield (front face up).
    sagaChapter(3) {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        Zone.GRAVEYARD,
                        Player.You,
                        GameObjectFilter.Creature,
                    ),
                    storeAs = "graveyardCreatures",
                ),
                SelectFromCollectionEffect(
                    from = "graveyardCreatures",
                    selection = SelectionMode.ChooseAnyNumber,
                    restrictions = listOf(SelectionRestriction.TotalManaValueAtMost(6)),
                    storeSelected = "toBattlefield",
                    prompt = "Return any number of creature cards with total mana value 6 or " +
                        "less to the battlefield",
                    selectedLabel = "Return to the battlefield",
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, Player.You),
                ),
                Effects.ExileAndReturnTransformed(EffectTarget.Self, ReturnFace.FRONT),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "229"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/back/4/5/457fdbb9-5439-460f-8e37-176f8919362c.jpg?1782686420"
    }
}

private val JoshuaPhoenixsDominantFront = card("Joshua, Phoenix's Dominant") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Noble Wizard"
    oracleText = "When Joshua enters, discard up to two cards, then draw that many cards.\n" +
        "{3}{R}{W}, {T}: Exile Joshua, then return it to the battlefield transformed under " +
        "its owner's control. Activate only as a sorcery."
    power = 3
    toughness = 4

    // When Joshua enters, discard up to two cards, then draw that many cards.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.HAND, Player.You),
                    storeAs = "hand",
                ),
                SelectFromCollectionEffect(
                    from = "hand",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
                    storeSelected = "discarded",
                    prompt = "Discard up to two cards",
                ),
                MoveCollectionEffect(
                    from = "discarded",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.You),
                    moveType = MoveType.Discard,
                ),
                DrawCardsEffect(DynamicAmount.VariableReference("discarded_count")),
            ),
        )
        description = "When Joshua enters, discard up to two cards, then draw that many cards."
    }

    // {3}{R}{W}, {T}: Exile Joshua, then return it to the battlefield transformed under its
    // owner's control. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{R}{W}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        effect = Effects.ExileAndReturnTransformed()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "229"
        artist = "Lius Lasahido"
        flavorText = "\"In ashen grip, let ember glow, to kindle flames anew.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/5/457fdbb9-5439-460f-8e37-176f8919362c.jpg?1782686420"

        ruling(
            "2025-06-06",
            "You can choose to discard no cards when Joshua's first ability resolves. If you " +
                "do, you won't draw any cards."
        )
    }
}

val JoshuaPhoenixsDominant: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = JoshuaPhoenixsDominantFront,
    backFace = PhoenixWardenOfFire,
)
