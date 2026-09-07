package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.ScatteringStroke
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scattering Stroke — "Counter target spell. Clash with an opponent. If you win, at the beginning of
 * your next main phase, you may add an amount of {C} equal to that spell's mana value."
 *
 * The mana value has to survive three separate erasures before anyone can spend it, and each one
 * would silently pay out **zero** rather than fail loudly:
 *
 *  1. The counter puts "that spell" in the graveyard before the clash starts, and a target read is
 *     `LIVE_ONLY` (CR 608.2b). Hence the up-front `Effects.StoreNumber`.
 *  2. The clash pauses twice for the top-or-bottom decisions, so the win rider runs on the far side
 *     of a gated pause — both players are given a clash-legal library here so those prompts are
 *     genuinely asked rather than auto-answered onto the synchronous path.
 *  3. The payoff fires a phase later, when the resolution pipeline is gone, so the delayed trigger
 *     has to bake the number in at creation time.
 *
 * Player 2 is deliberately the Stroke's controller: "your next main phase" then lands on the *next*
 * turn's precombat main rather than on a phase the caster is already standing in, which is the case
 * that actually crosses a turn boundary. The countered spell costs {5}, a mana value distinct from
 * both Scattering Stroke's own 4 and from zero, so a wrong read is visible rather than plausible.
 */
class ScatteringStrokeScenarioTest : FunSpec({

    /** Costs {5}: a mana value distinct from Scattering Stroke's own 4 and from zero. */
    val boulder = card("Scattering Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }

    // Built during spec construction: `TestCards.all` scans the whole card corpus, and paying that
    // inside the first test body puts it under the per-test timeout.
    val cards = TestCards.all + listOf(ScatteringStroke, boulder)

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(cards)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Colorless mana currently floating in [player]'s pool. */
    fun GameTestDriver.colorlessInPool(player: EntityId): Int =
        state.getEntity(player)?.get<ManaPoolComponent>()?.colorless ?: 0

    /**
     * Player 1 casts a {5} artifact; player 2 counters it with Scattering Stroke and clashes.
     * [win] stacks the two libraries so the Stroke's controller wins or loses the clash.
     */
    fun GameTestDriver.counterABoulder(win: Boolean): EntityId {
        val boulderCard = putCardInHand(player1, "Scattering Boulder")
        giveColorlessMana(player1, 5)
        castSpell(player1, boulderCard).error shouldBe null
        passPriority(player1).error shouldBe null

        val stroke = putCardInHand(player2, "Scattering Stroke")
        giveMana(player2, Color.BLUE, 4)
        castSpellWithTargets(player2, stroke, listOf(ChosenTarget.Spell(boulderCard))).error shouldBe null

        // A real card on top for each player, so both top-or-bottom prompts are actually asked.
        // Islands are mana value 0, so whoever gets the Boulder wins.
        putCardOnTopOfLibrary(player2, if (win) "Scattering Boulder" else "Island")
        putCardOnTopOfLibrary(player1, if (win) "Island" else "Scattering Boulder")

        bothPass().error shouldBe null
        repeat(2) {
            val choice = pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            submitCardSelection(choice.playerId, emptyList()).error shouldBe null
        }

        pendingDecision shouldBe null
        withClue("the counter happens on a win and a loss alike") {
            state.getZone(ZoneKey(player1, Zone.GRAVEYARD)).contains(boulderCard) shouldBe true
        }
        return boulderCard
    }

    /** Roll into player 2's next turn, stopping the moment their precombat main begins. */
    fun GameTestDriver.advanceToStrokeControllersMainPhase() {
        passPriorityUntil(Step.END)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        state.activePlayerId shouldBe player2
    }

    test("winning the clash pays out the countered spell's mana value at your next main phase") {
        val d = driver()
        d.counterABoulder(win = true)

        withClue("the payoff is a delayed trigger — nothing is added while the Stroke resolves") {
            d.colorlessInPool(d.player2) shouldBe 0
        }

        d.advanceToStrokeControllersMainPhase()
        d.bothPass().error shouldBe null

        val consent = d.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        withClue("the delayed ability belongs to the Stroke's controller") {
            consent.playerId shouldBe d.player2
        }
        d.submitYesNo(d.player2, true).error shouldBe null

        withClue("{C} equal to the countered spell's mana value — 5, not the Stroke's own 4, and not 0") {
            d.colorlessInPool(d.player2) shouldBe 5
        }
    }

    test("declining the may adds nothing — it is all or nothing") {
        val d = driver()
        d.counterABoulder(win = true)

        d.advanceToStrokeControllersMainPhase()
        d.bothPass().error shouldBe null

        d.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        d.submitYesNo(d.player2, false).error shouldBe null

        d.colorlessInPool(d.player2) shouldBe 0
    }

    test("losing the clash counters the spell but schedules no delayed ability at all") {
        val d = driver()
        d.counterABoulder(win = false)

        d.advanceToStrokeControllersMainPhase()

        withClue("no trigger was created, so the main phase begins with an empty stack") {
            d.state.stack.isEmpty() shouldBe true
        }
        d.bothPass().error shouldBe null
        d.pendingDecision shouldBe null
        d.colorlessInPool(d.player2) shouldBe 0
    }
})
