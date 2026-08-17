package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.msh.cards.BaronHelmutZemo
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Baron Helmut Zemo (MSH #87) — {B}{B}{B} Legendary Creature — Human Noble Villain 3/3.
 *
 *   Whenever you cast a black spell from your hand, Baron Helmut Zemo connives.
 *   Boast — Exile any number of black cards from your graveyard with fifteen or more black mana
 *   symbols among their mana costs: Copy those exiled cards. You may cast up to three of the copies
 *   without paying their mana costs.
 *
 * The card composes four existing pieces and one new cost:
 *  - the cast trigger with a colour filter plus `SpellCastPredicate.CastFromZone(HAND)` — the rider
 *    that keeps a spell cast from anywhere else (including Zemo's own free casts out of exile) from
 *    firing it — over the shared `Effects.Connive()`;
 *  - **Boast** (CR 702.142a), the keyword marker whose two rules clauses are ordinary activation
 *    restrictions (pinned as a keyword in `BoastKeywordScenarioTest`);
 *  - the new sum-gated graveyard exile cost (pinned as a primitive in
 *    `GraveyardTotalExileCostScenarioTest`);
 *  - `CardSource.ExiledAsCost` → `CopyCollectionIntoCollection` →
 *    `CastUpToNFromCollectionWithoutPayingCost(maxCasts = 3)`, the copy-then-cast pipeline The Tale
 *    of Tamiyo IV and Doom Reigns Supreme already use.
 *
 * What is left for *this* file is the wiring between them: that the cost's own selection is what the
 * effect copies, that the copies are capped at three casts while the originals stay exiled, and that
 * the free casts do not feed the from-hand trigger.
 */
class BaronHelmutZemoScenarioTest : ScenarioTestBase() {

    /** {B}{B}{B} — three black pips, so five of them are exactly the fifteen the boast wants. */
    private val relic = card("Test Zemo Relic") {
        manaCost = "{B}{B}{B}"
        colorIdentity = "B"
        typeLine = "Creature — Zombie"
        power = 2
        toughness = 2
    }

    private val blackBolt = card("Test Zemo Black Bolt") {
        manaCost = "{B}"
        colorIdentity = "B"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(1) }
    }

    private val redBolt = card("Test Zemo Red Bolt") {
        manaCost = "{R}"
        colorIdentity = "R"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(1) }
    }

    // --- Harness -------------------------------------------------------------------------------

    private fun boastAbilityId() =
        cardRegistry.getCard("Baron Helmut Zemo")!!.script.activatedAbilities[0].id

    private fun TestGame.nameOf(id: EntityId): String? =
        state.getEntity(id)?.get<CardComponent>()?.name

    private fun TestGame.exileIds(): List<EntityId> = state.getZone(ZoneKey(player1Id, Zone.EXILE))

    private fun TestGame.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun TestGame.boastAction() =
        getLegalActions(1).firstOrNull { it.description.startsWith("Boast —") }

    private fun TestGame.boast(exiled: List<EntityId>): ExecutionResult = execute(
        ActivateAbility(
            playerId = player1Id,
            sourceId = findPermanent("Baron Helmut Zemo")!!,
            abilityId = boastAbilityId(),
            costPayment = AdditionalCostPayment(exiledCards = exiled),
        )
    )

    /** Zemo on the battlefield with [relicsInGraveyard] black relics in the graveyard, ready to attack. */
    private fun combatScenario(relicsInGraveyard: Int): TestGame {
        var builder = scenario()
            .withPlayers("Zemo", "Defender")
            .withCardOnBattlefield(1, "Baron Helmut Zemo")
            .withActivePlayer(1)
            .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
        repeat(relicsInGraveyard) { builder = builder.withCardInGraveyard(1, "Test Zemo Relic") }
        return builder.build()
    }

    init {
        cardRegistry.register(BaronHelmutZemo)
        cardRegistry.register(relic)
        cardRegistry.register(blackBolt)
        cardRegistry.register(redBolt)

        // -----------------------------------------------------------------------------------
        // "Whenever you cast a black spell from your hand, Baron Helmut Zemo connives."
        // -----------------------------------------------------------------------------------

        test("casting a black spell from hand connives, and a nonland discard grows Zemo") {
            val game = scenario()
                .withPlayers("Zemo", "Defender")
                .withCardOnBattlefield(1, "Baron Helmut Zemo")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withCardInHand(1, "Test Zemo Black Bolt")
                .withCardInHand(1, "Test Zemo Relic")
                .withCardInLibrary(1, "Test Zemo Relic")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val zemo = game.findPermanent("Baron Helmut Zemo")!!

            game.castSpell(1, "Test Zemo Black Bolt").error shouldBe null
            game.resolveStack()

            withClue("connive draws, then pauses on the discard") {
                game.hasPendingDecision() shouldBe true
            }
            val toDiscard = game.findCardsInHand(1, "Test Zemo Relic").first()
            game.selectCards(listOf(toDiscard)).error shouldBe null
            game.resolveStack()

            withClue("a discarded nonland card puts a +1/+1 counter on Zemo") {
                game.plusOneCounters(zemo) shouldBe 1
            }
            game.isInGraveyard(1, "Test Zemo Relic") shouldBe true
        }

        test("a nonblack spell cast from hand does not connive") {
            val game = scenario()
                .withPlayers("Zemo", "Defender")
                .withCardOnBattlefield(1, "Baron Helmut Zemo")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInHand(1, "Test Zemo Red Bolt")
                .withCardInLibrary(1, "Test Zemo Relic")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val zemo = game.findPermanent("Baron Helmut Zemo")!!
            val handBefore = game.handSize(1)

            game.castSpell(1, "Test Zemo Red Bolt").error shouldBe null
            game.resolveStack()

            withClue("the trigger filters on the spell's colour, so nothing conniveed") {
                game.hasPendingDecision() shouldBe false
                game.plusOneCounters(zemo) shouldBe 0
                game.handSize(1) shouldBe handBefore - 1
            }
        }

        // -----------------------------------------------------------------------------------
        // The boast: fifteen black pips buys copies of what you exiled
        // -----------------------------------------------------------------------------------

        test("the boast is unavailable until Zemo attacks, even with fifteen pips banked") {
            val game = combatScenario(relicsInGraveyard = 5)
            withClue("CR 702.142a — only if this creature attacked this turn") {
                game.boastAction() shouldBe null
            }
        }

        test("attacking with only fourteen pips in the graveyard still can't pay for it") {
            // Four relics is twelve black pips; the fifth is what makes fifteen.
            val game = combatScenario(relicsInGraveyard = 4)
            game.declareAttackers(mapOf("Baron Helmut Zemo" to 2))

            withClue("the cost fails closed, so the ability is absent rather than offered") {
                game.boastAction() shouldBe null
            }
        }

        test("fifteen black pips copies the exiled cards and casts three of the copies free") {
            val game = combatScenario(relicsInGraveyard = 5)
            game.declareAttackers(mapOf("Baron Helmut Zemo" to 2))
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            val action = game.boastAction()
            withClue("attacked this turn, and the graveyard reaches fifteen — now it is offered") {
                (action != null) shouldBe true
            }
            withClue("the whole graveyard is the pool; five relics at three pips each is the floor") {
                action!!.additionalCostInfo!!.exileMinTotalWeight shouldBe 15
                action.additionalCostInfo!!.validExileTargets.size shouldBe 5
            }

            val fodder = game.findCardsInGraveyard(1, "Test Zemo Relic")
            fodder.size shouldBe 5
            game.boast(fodder).error shouldBe null

            withClue("the cost is paid on activation (CR 601.2h), so the cards are gone already") {
                game.exileIds().toSet() shouldBe fodder.toSet()
                game.graveyardSize(1) shouldBe 0
            }

            game.resolveStack()

            withClue("resolution copies what the cost exiled and offers the copies to cast") {
                game.hasPendingDecision() shouldBe true
            }

            // Take three of the five copies, one per pass of the capped loop.
            repeat(3) {
                val copies = game.exileIds().filter { it !in fodder }
                game.selectCards(listOf(copies.first())).error shouldBe null
            }

            withClue("maxCasts = 3 is spent, so the last two copies are never offered") {
                game.hasPendingDecision() shouldBe false
            }
            game.resolveStack()

            withClue("three copies resolved as creatures under Zemo's controller") {
                game.findAllPermanents("Test Zemo Relic").size shouldBe 3
            }
            withClue("the exiled originals stay exiled; the uncast copies cease to exist (CR 707.10a)") {
                game.exileIds().toSet() shouldBe fodder.toSet()
            }
            withClue("the free casts came from exile, not hand — the connive trigger never fired") {
                game.hasPendingDecision() shouldBe false
                game.plusOneCounters(game.findPermanent("Baron Helmut Zemo")!!) shouldBe 0
            }
        }

        test("the boast can only be activated once each turn") {
            val game = combatScenario(relicsInGraveyard = 10)
            game.declareAttackers(mapOf("Baron Helmut Zemo" to 2))
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            val fodder = game.findCardsInGraveyard(1, "Test Zemo Relic")
            game.boast(fodder.take(5)).error shouldBe null
            game.resolveStack()
            while (game.hasPendingDecision()) {
                game.selectCards(emptyList()).error shouldBe null
            }
            game.resolveStack()

            withClue("five relics are still banked, but boast is once each turn (CR 702.142a)") {
                game.boastAction() shouldBe null
                game.boast(fodder.drop(5)).error shouldBe
                    "This ability can only be activated once each turn"
            }
        }
    }
}
